/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.configuration;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.huifu.saturn.common.constants.code.respCode.SaturnBasicRespCode;
import com.huifu.saturn.common.constants.constants.SaturnCommonConstants;
import com.huifu.saturn.common.utils.SaturnLoggerUtils;
import com.huifu.saturn.runtime.constants.SaturnRuntimeConstants;
import com.huifu.saturn.runtime.context.SaturnApplicationContext;
import com.huifu.saturn.runtime.mq.listener.SaturnMessageListener;
import com.huifu.saturn.runtime.ws.SaturnWebServiceProxyInterceptor;

/**
 * 
 * @author zhanghaijie
 * @version $Id: IpConfigServiceListener.java, v 0.1 2012-8-31 上午9:03:03 zhanghaijie Exp $
 */
public class IpConfigServiceListener extends SaturnMessageListener {

    private static final Logger logger = Logger.getLogger(IpConfigServiceListener.class);

    @Override
    protected void handleMessage(Object message) {

        if (null == message) {
            SaturnLoggerUtils.warn(logger, null, "【IP配置广播消息】IP推送信息为空");
            return;
        }

        SaturnLoggerUtils.info(logger, "【IP配置广播消息】IP地址刷新", message.toString());
        String xml = message.toString();

        if (StringUtils.isBlank(xml)) {
            SaturnLoggerUtils.warn(logger, null, "【IP配置广播消息】IP地址刷新，转换字符串为空");
            return;
        }

        //刷新全局
        configLocalVarialable(xml);
    }

    /**
     * 
     * @param xml
     */
    private synchronized void configLocalVarialable(String xml) {
        Element rootElement = getRootElementFromXML(xml);
        if (null == rootElement) {
            return;
        }

        String respCode = rootElement.getChildText(SaturnCommonConstants.CONFIG_CENTER_RESP_CODE);
        if (null == respCode || !SaturnBasicRespCode.SUCCESS.getReturnCode().equals(respCode)) {
            SaturnLoggerUtils.error(logger, null, "【IP推送刷新】配置中心返回异常", xml);
            return;
        }

        String systemName = rootElement
            .getChildText(SaturnCommonConstants.CONFIG_CENTER_SYSTEM_NAME);
        String ipString = rootElement.getChildText(SaturnCommonConstants.CONFIG_CENTER_IP);

        //刷新本地缓存
        refreshIP(systemName, ipString);
        refreshProxy(systemName, ipString);
    }

    /**
     * 更新本地代理
     * 
     * @param systemName
     * @param ipString
     */
    private void refreshProxy(String systemName, String ipString) {
        SaturnLoggerUtils.info(logger, "【IP推送刷新】刷新本地WS代理,IP地址", ipString);
        @SuppressWarnings("unchecked")
        Map<String, SaturnWebServiceProxyInterceptor> proxyMap = (Map<String, SaturnWebServiceProxyInterceptor>) SaturnApplicationContext
            .getApplicationContext(SaturnRuntimeConstants.WS_PROXY_KEY + systemName);
        if (null != proxyMap) {
            Iterator<String> keys = proxyMap.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                SaturnWebServiceProxyInterceptor saturnWebServiceProxyInterceptor = proxyMap
                    .get(key);
                if (null != saturnWebServiceProxyInterceptor) {
                    List<String> ips = getIpList(ipString);
                    saturnWebServiceProxyInterceptor.reConfigWSClient(ips);
                }
            }
        }
    }

    /**
     * 
     * @param systemName
     * @param ipString
     */
    private void refreshIP(String systemName, String ipString) {
        //刷新本地缓存
        SaturnLoggerUtils.info(logger, "【IP推送刷新】刷新本地IP信息,刷新目标系统", systemName);
        String localCacheKey = SaturnRuntimeConstants.IP_CONFIG_SYS_KEY + systemName;
        SaturnApplicationContext.updateApplicationContext(localCacheKey, ipString);

    }

    /**
     * 
     * @param xml
     * @return
     */
    private Element getRootElementFromXML(String xml) {
        Reader reader = new StringReader(xml);
        SAXBuilder saxb = new SAXBuilder();
        Document doc;
        try {
            doc = saxb.build(reader);
        } catch (JDOMException e) {
            SaturnLoggerUtils.error(logger, e, "【IP推送刷新】jdom解析失败", xml);
            return null;
        } catch (IOException e) {
            SaturnLoggerUtils.error(logger, e, "【IP推送刷新】IO异常", xml);
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                SaturnLoggerUtils.error(logger, e, "【IP推送刷新】StringReader关闭异常", xml);
            }
        }
        Element rootElement = (Element) doc.getRootElement();
        return rootElement;
    }

    /**
     * 
     * @param ipString
     * @return
     */
    private List<String> getIpList(String ipString) {
        String[] ips = ipString.split(SaturnCommonConstants.CONFIG_CENTER_IP_SPLIT_DOT);
        List<String> ipList = new LinkedList<String>();
        for (String ip : ips) {
            ipList.add(ip);
        }
        return ipList;
    }
}
