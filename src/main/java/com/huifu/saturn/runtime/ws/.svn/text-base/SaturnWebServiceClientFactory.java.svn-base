/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.huifu.saturn.common.constants.constants.SaturnCommonConstants;
import com.huifu.saturn.runtime.configuration.ConfigCenterIpService;
import com.huifu.saturn.runtime.constants.SaturnRuntimeConstants;
import com.huifu.saturn.runtime.context.SaturnApplicationContext;

/**
 * 
 * @author zhanghaijie
 * @version $Id: SaturnWebServiceClientFactory.java, v 0.1 2012-8-24 下午4:03:31 zhanghaijie Exp $
 */
@SuppressWarnings("rawtypes")
public class SaturnWebServiceClientFactory implements FactoryBean, InitializingBean {

    private Class        serviceInterface;

    private Object       proxy;

    private String       appName;

    private String       serviceName;

    private String       requestTimeOut = "750000";

    private List<String> ipAddresses;

    private String       configCenterURL;

    public void afterPropertiesSet() throws Exception {
        initParams();
        initProxy();
    }

    private void initParams() {
        initConfigCenter();
        initAppName();
        initIpAddresses();
        initServiceName();
    }

    @SuppressWarnings("unchecked")
    private void initProxy() {
        SaturnWebServiceProxyInterceptor saturnWebServiceProxyInterceptor = new SaturnWebServiceProxyInterceptor();
        saturnWebServiceProxyInterceptor.setIpAddresses(ipAddresses);
        saturnWebServiceProxyInterceptor.setServiceInterface(serviceInterface);
        saturnWebServiceProxyInterceptor.setAppName(appName);
        saturnWebServiceProxyInterceptor.setServiceName(serviceName);
        saturnWebServiceProxyInterceptor.setRequestTimeOut(requestTimeOut);
        saturnWebServiceProxyInterceptor.initClient();
        proxy = ProxyFactory.getProxy(serviceInterface, saturnWebServiceProxyInterceptor);

        this.setApplicationContext(saturnWebServiceProxyInterceptor);
    }

    /**
     * 
     * @param saturnWebServiceProxyInterceptor
     */
    private void setApplicationContext(SaturnWebServiceProxyInterceptor saturnWebServiceProxyInterceptor) {
        String proxyKey = SaturnRuntimeConstants.WS_PROXY_KEY + appName;

        @SuppressWarnings("unchecked")
        Map<String, SaturnWebServiceProxyInterceptor> proxyMap = (Map<String, SaturnWebServiceProxyInterceptor>) SaturnApplicationContext
            .getApplicationContext(proxyKey);
        if (null == proxyMap) {
            proxyMap = new HashMap<String, SaturnWebServiceProxyInterceptor>();
        }

        proxyMap.put(serviceName, saturnWebServiceProxyInterceptor);
        SaturnApplicationContext.updateApplicationContext(proxyKey, proxyMap);
    }

    private void initAppName() {
        String service = serviceInterface.getName();
        String[] parts = StringUtils.split(service, ".");
        appName = parts[2];
    }

    private void initConfigCenter() {
        String configCenterIP = (String) SaturnApplicationContext
            .getApplicationContext(SaturnCommonConstants.CONFIG_CENTER_IP_ADDRESS);
        String configCenterAppName = (String) SaturnApplicationContext
            .getApplicationContext(SaturnCommonConstants.CONFIG_CENTER_APPLICATION_NAME);
        String configCenterAppPort = (String) SaturnApplicationContext
            .getApplicationContext(SaturnCommonConstants.CONFIG_CENTER_APPLICATION_PORT);

        configCenterURL = "http://" + configCenterIP + ":" + configCenterAppPort + "/"
                          + configCenterAppName + "/" + "services/ipInfoService?wsdl";

    }

    private void initIpAddresses() {
        ConfigCenterIpService configServiceIpService = new ConfigCenterIpService(configCenterURL);
        String env = (String) SaturnApplicationContext
            .getApplicationContext(SaturnCommonConstants.ENVIRONMENT);
        ipAddresses = configServiceIpService.getIpAddresses(appName, env);
    }

    private void initServiceName() {
        this.serviceName = StringUtils.uncapitalize(serviceInterface.getSimpleName());
    }

    public Object getObject() throws Exception {
        return proxy;
    }

    public Class getObjectType() {
        return null;
    }

    public boolean isSingleton() {
        return false;
    }

    public void setServiceInterface(Class serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public void setRequestTimeOut(String requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

}
