/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.ws;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.spring.SpringUtils;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.transport.http.CommonsHttpMessageSender;
import org.codehaus.xfire.util.Resolver;

import com.huifu.saturn.common.constants.constants.SaturnLoggerConstants;
import com.huifu.saturn.common.utils.SaturnLoggerUtils;
import com.huifu.saturn.runtime.exception.SaturnInitException;
import com.huifu.saturn.runtime.exception.SaturnRemoteInvokeException;

/**
 * 
 * @author zhanghaijie
 * @version $Id: SaturnWebServiceProxyInterceptor.java, v 0.1 2012-8-24 下午4:03:43 zhanghaijie Exp $
 */
@SuppressWarnings("rawtypes")
public class SaturnWebServiceProxyInterceptor implements MethodInterceptor {

    private static final Logger              logger          = Logger
                                                                 .getLogger(SaturnWebServiceProxyInterceptor.class);

    private static final Logger              wsLogger        = Logger
                                                                 .getLogger(SaturnLoggerConstants.WS_INVOKE_LOG);

    private static final String              WS_PROXY        = "ws_proxy";

    private static final String              WS_STATUS       = "ws_status";

    private static final String              WS_IP           = "ws_ip";

    private long                             retryTime       = 30000;

    private String                           requestTimeOut;

    private String                           appName;

    private String                           serviceName;

    private String                           username;

    private String                           password;

    private Class                            serviceInterface;

    private List<String>                     ipAddresses;

    private Set<String>                      failIpAddresses = new HashSet<String>();

    private Map<String, Map<String, Object>> serviceClients  = new HashMap<String, Map<String, Object>>();

    private ServiceFactory                   serviceFactory  = new ObjectServiceFactory();

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArguments();
        Map<String, Object> proxy = getClient();
        Object client = proxy.get(WS_PROXY);
        try {
            StringBuffer invokeParam = new StringBuffer();
            for (int x = 0; x < args.length; x++) {
                invokeParam.append(args[x] != null ? args[x].getClass().getName() : null)
                    .append(" : ").append(args[x] != null ? args[x].toString() : null).append(" |");
            }
            SaturnLoggerUtils.info(wsLogger, "【WS调用】", "WS开始调用:", client.toString(), "调用参数:",
                invokeParam.toString());
            Object ob = method.invoke(client, args);
            SaturnLoggerUtils.info(wsLogger, "【WS调用】", "WS调用成功:", client.toString(), "调用参数:",
                invokeParam.toString(), "返回参数:", null != ob ? ob.toString() : null);
            return ob;
        } catch (InvocationTargetException e) {
            Object target = SpringUtils.getUserTarget(client);
            Client c = Client.getInstance(target);
            StringBuffer callTarget = new StringBuffer(c.getUrl()).append(" arguments: ");
            for (int x = 0; x < args.length; x++) {
                callTarget.append(args[x] != null ? args[x].getClass().getName() : null)
                    .append(" : ").append(args[x] != null ? args[x].toString() : null).append(" |");
            }
            SaturnLoggerUtils.error(logger, e, "【WS调用失败】失败地址" + callTarget.toString());
            Throwable targetException = e.getTargetException();
            if (targetException instanceof XFireRuntimeException) {
                if (targetException.getCause() instanceof XFireFault) {
                    XFireFault xFireFault = (XFireFault) targetException.getCause();
                    QName qName = xFireFault.getFaultCode();
                    if (XFireFault.RECEIVER.getLocalPart().equals(qName.getLocalPart())
                        || XFireFault.SOAP11_SERVER.getLocalPart().equals(qName.getLocalPart())) {
                        SaturnWebServiceClientStatus status = (SaturnWebServiceClientStatus) proxy
                            .get(WS_STATUS);
                        synchronized (status) {
                            status.setStatus(SaturnWebServiceClientStatus.INAVAILABLE);
                            status.setInavaliableTime(new Date());
                        }
                        failIpAddresses.add(proxy.get(WS_IP).toString());
                        SaturnLoggerUtils.error(logger, e, "【WS调用失败】调用地址", callTarget.toString());
                        SaturnLoggerUtils.info(logger, "【WS调用失败】发起重试", "失败地址：",
                            callTarget.toString());
                        return invoke(invocation);
                    }
                }
            }
            throw targetException;
        }
    }

    /**
     * 
     */
    public void initClient() {
        if (serviceClients == null || serviceClients.size() < 1) {
            createClient();
        }
    }

    public synchronized void reConfigWSClient(List<String> ips) {
        this.ipAddresses = ips;
        serviceClients.clear();
        failIpAddresses.clear();
    }

    /**
     * Gets the actual client proxy. This implementation ensures only one
     * client proxy is ever created, even in multi-threaded situations
     * 
     * @return service client proxy
     * @throws MalformedURLException
     */
    private synchronized Map<String, Object> getClient() throws Exception {
        if (serviceClients == null || serviceClients.size() < 1) {
            createClient();
        }

        return loadBalanceClient();
    }

    private Map<String, Object> loadBalanceClient() {
        int size = ipAddresses.size();
        if (size < 1) {
            SaturnLoggerUtils.error(logger, null, "【WS调用】服务方无可用服务,IP地址为空", appName);
            throw new SaturnRemoteInvokeException("服务方无可用机器，IP地址为空", null);
        }
        int index = (int) (Math.random() * size);
        String ip = ipAddresses.get(index);
        Map<String, Object> proxy = serviceClients.get(ip);
        if (null == proxy) {
            SaturnLoggerUtils.error(logger, null, "【WS调用】服务方无可用服务", appName);
            throw new SaturnRemoteInvokeException("服务方无可用机器", null);
        }
        SaturnWebServiceClientStatus status = (SaturnWebServiceClientStatus) proxy.get(WS_STATUS);
        if (status.getStatus().equals(SaturnWebServiceClientStatus.INAVAILABLE)) {
            Date currentTime = new Date();
            long inVailable = currentTime.getTime() - status.getInavaliableTime().getTime();
            if (inVailable > retryTime) {
                failIpAddresses.remove(ip);
                status.setStatus(SaturnWebServiceClientStatus.AVAILABLE);
                return proxy;
            }

            if (failIpAddresses.size() >= ipAddresses.size()) {
                SaturnLoggerUtils.error(logger, null, "【WS调用异常】集群宕机,服务名:",
                    serviceInterface.getName());
                throw new SaturnWebServiceException();
            }
            return loadBalanceClient();
        }
        return proxy;
    }

    /**
     * Creates actual XFire client proxy that this interceptor will delegate to.
     * 
     * @throws Exception
     *             if the client proxy could not be created.
     */
    private void createClient() {
        if (null == ipAddresses || ipAddresses.size() < 1) {
            SaturnLoggerUtils.error(logger, null, "【WS初始化失败】集群IP列表为空，加载失败", "appName", appName);
            throw new RuntimeException("初始化失败，IP地址列表为空");
        }
        for (int i = 0; i < ipAddresses.size(); i++) {
            String ipAddress = ipAddresses.get(i);
            Object serviceClient = null;
            try {
                serviceClient = makeClient(ipAddress);
                SaturnLoggerUtils.info(logger, "【WS初始化】初始化客户端", ipAddress, appName);
            } catch (Exception e) {
                SaturnLoggerUtils.error(logger, e, "【WS初始化失败】serviceClient创建失败，连接异常:ipAddress",
                    ipAddress, "appName", appName, "serviceName:", serviceName);
                throw new SaturnInitException("serviceClient创建失败IP&AppName&ServiceName" + ipAddress
                                              + " " + appName + " " + serviceName, e);
                /* ipAddresses.remove(ipAddress);
                 i--;
                 continue;*/
            }
            Class interf = getServiceInterface();
            if (logger.isDebugEnabled()) {
                logger.debug("Created: " + toString());
            }

            Client client = Client.getInstance(serviceClient);

            String username = getUsername();
            if (username != null) {
                client.setProperty(Channel.USERNAME, username);

                String password = getPassword();
                client.setProperty(Channel.PASSWORD, password);

                if (logger.isDebugEnabled()) {
                    logger.debug("Enabled HTTP basic authentication for: " + interf
                                 + " with username: " + username);
                }
            }
            client.setProperty(CommonsHttpMessageSender.HTTP_TIMEOUT, requestTimeOut);

            SaturnWebServiceClientStatus status = new SaturnWebServiceClientStatus();
            status.setStatus(SaturnWebServiceClientStatus.AVAILABLE);

            Map<String, Object> clients = new HashMap<String, Object>();
            clients.put(WS_PROXY, serviceClient);
            clients.put(WS_STATUS, status);
            clients.put(WS_IP, ipAddress);

            serviceClients.put(ipAddress, clients);

        }

    }

    /**
     * Performs actual creation of XFire client proxy.
     * 
     * @return XFire proxy to the service
     * @throws java.net.MalformedURLException
     *             if {@link XFireProxyFactory#create} threw one
     */
    private Object makeClient(String ipAddress) throws Exception {
        String wsdlDocumentUrl = createWsdlDocumentURL(ipAddress);
        Resolver resolver;
        try {
            resolver = new Resolver(wsdlDocumentUrl);
        } catch (IOException e) {
            SaturnLoggerUtils.error(logger, e, "【WS初始化失败】wsdl解析失败，找不到地址：", wsdlDocumentUrl);
            throw new RuntimeException(e);
        }

        URI uri = resolver.getURI();
        if (uri == null) {
            SaturnLoggerUtils.error(logger, null, "【WS初始化失败】uri为空，ipAddress:", ipAddress,
                "appName:", appName);
            throw new XFireRuntimeException("Could not resolve uri " + uri);
        }

        Service serviceModel = getServiceFactory().create(getServiceInterface(), null, uri.toURL(),
            null);

        QName endpointName = findFirstSoapEndpoint(serviceModel.getEndpoints());

        if (endpointName != null) {
            Endpoint ep = serviceModel.getEndpoint(endpointName);
            if (ep == null) {
                SaturnLoggerUtils.error(logger, null, "【WS初始化失败】Endpoint为空，ipAddress:", ipAddress,
                    "appName:", appName);
                throw new IllegalStateException("Could not find endpoint with name " + endpointName
                                                + " on service.");
            }
            return new XFireProxyFactory().create(ep);
        } else {
            SaturnLoggerUtils.error(logger, null, "【WS初始化失败】WSDL URL or ServiceUrl 不能为空",
                "ipAddress:", ipAddress, "appName", appName);
            throw new IllegalStateException("A WSDL URL or service URL must be supplied.");
        }
    }

    private QName findFirstSoapEndpoint(Collection endpoints) {
        for (Iterator itr = endpoints.iterator(); itr.hasNext();) {
            Endpoint ep = (Endpoint) itr.next();

            if (ep.getBinding() instanceof AbstractSoapBinding)
                return ep.getName();
        }
        return null;
    }

    /**
     * 
     * @param ip
     * @param appName
     * @return
     */
    private String createWsdlDocumentURL(String ip) {
        StringBuffer wsdlURL = new StringBuffer();
        wsdlURL.append("http://");
        wsdlURL.append(ip);
        wsdlURL.append("/");
        wsdlURL.append(appName);
        wsdlURL.append("/services/");
        wsdlURL.append(serviceName);
        wsdlURL.append("?wsdl");
        return wsdlURL.toString();
    }

    public Map<String, Map<String, Object>> getServiceClients() {
        return serviceClients;
    }

    public void setServiceClients(Map<String, Map<String, Object>> serviceClients) {
        this.serviceClients = serviceClients;
    }

    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Class getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRequestTimeOut(String requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

}
