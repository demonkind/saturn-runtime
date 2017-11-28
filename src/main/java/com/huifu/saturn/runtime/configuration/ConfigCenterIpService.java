/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.configuration;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.xfire.client.Client;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.huifu.saturn.common.constants.code.respCode.SaturnBasicRespCode;
import com.huifu.saturn.common.constants.constants.SaturnCommonConstants;
import com.huifu.saturn.common.utils.SaturnLoggerUtils;
import com.huifu.saturn.runtime.constants.SaturnRuntimeConstants;
import com.huifu.saturn.runtime.context.SaturnApplicationContext;
import com.huifu.saturn.runtime.exception.SaturnInitException;

/**
 * 
 * @author zhanghaijie
 * @version $Id: ConfigServerIpService.java, v 0.1 2012-8-28 下午9:01:27 zhanghaijie Exp $
 */
public class ConfigCenterIpService {

    private static final Logger logger        = Logger.getLogger(ConfigCenterIpService.class);

    private static final String CONFIG_METHOD = "getAvaliableIp";

    private String              url;

    public ConfigCenterIpService(String url) {
        this.url = url;
    }

    public List<String> getIpAddresses(String targetSystem, String env) {
        String appKey = SaturnRuntimeConstants.IP_CONFIG_SYS_KEY + targetSystem;
        String resources = (String) SaturnApplicationContext.getApplicationContext(appKey);
        if (StringUtils.isNotBlank(resources)) {
            List<String> ips = this.splitIps(resources);
            return ips;
        }

        Object[] returnXML = invokeRemoteService(targetSystem, env);
        if (null == returnXML) {
            SaturnLoggerUtils.error(logger, null, "【配置中心加载失败】配置中心返回结果为空", url);
            throw new SaturnInitException("配置中心加载失败,返回参数异常");
        }

        String xml = returnXML[0].toString();
        List<String> ips = this.analysisXML(xml, appKey);
        return ips;
    }

    private Object[] invokeRemoteService(String targetSystem, String env) {
        Client c = null;
        try {
            c = new Client(new URL(url));
            Object[] ob = c.invoke(CONFIG_METHOD, new Object[] { targetSystem, env });
            return ob;
        } catch (MalformedURLException e) {
            SaturnLoggerUtils.error(logger, e, "【配置中心加载失败】配置中心地址异常，配置中心地址:", url);
            throw new SaturnInitException("配置中心地址异常", e);
        } catch (Exception e) {
            SaturnLoggerUtils.error(logger, e, "【配置中心加载失败】系统异常，配置中心地址：", url);
            throw new SaturnInitException("系统异常", e);
        } finally {
            if (null != c) {
                try {
                    c.close();
                } catch (Exception e) {
                    SaturnLoggerUtils.error(logger, e, "【配置中心加载异常】连接关闭失败:", url);
                    throw new SaturnInitException("系统异常", e);
                }
            }
        }
    }

    /**
     * 
     * @param xml
     * @param appContextKey
     * @return
     */
    private List<String> analysisXML(String xml, String appContextKey) {
        String ips = fetchIPFromXML(xml);
        SaturnApplicationContext.setApplicationContext(appContextKey, ips);
        List<String> clusterIPs = this.splitIps(ips);
        return clusterIPs;
    }

    /**
     * Split ip from ip String which return from configCenter
     * 
     * @param ips
     * @return
     */
    private List<String> splitIps(String ips) {
        if (StringUtils.isBlank(ips)) {
            SaturnLoggerUtils.error(logger, null, "【配置中心加载失败】ip地址返回为空");
            throw new SaturnInitException("ip地址返回为空");
        }
        String[] ipArray = ips.split(SaturnCommonConstants.CONFIG_CENTER_IP_SPLIT_DOT);
        List<String> clusterIPs = new LinkedList<String>();
        for (String ip : ipArray) {
            clusterIPs.add(ip);
        }
        return clusterIPs;
    }

    /**
     * Get Ip String from XML which from configCenter
     * 
     * @param xml
     * @return
     */
    private String fetchIPFromXML(String xml) {
        Reader reader = new StringReader(xml);
        SAXBuilder saxb = new SAXBuilder();
        Document doc;
        try {
            doc = saxb.build(reader);
        } catch (JDOMException e) {
            SaturnLoggerUtils.error(logger, e, "【配置中心加载失败】jdom解析失败", xml);
            throw new SaturnInitException("jdom解析失败", e);
        } catch (IOException e) {
            SaturnLoggerUtils.error(logger, e, "【配置中心加载失败】IO异常", xml);
            throw new SaturnInitException("配置中心加载IO异常", e);
        }
        Element rootElement = (Element) doc.getRootElement();
        String respCode = rootElement.getChildText(SaturnCommonConstants.CONFIG_CENTER_RESP_CODE);
        if (null == respCode || !SaturnBasicRespCode.SUCCESS.getReturnCode().equals(respCode)) {
            SaturnLoggerUtils.error(logger, null, "【配置中心加载失败】配置中心返回异常", xml);
            throw new SaturnInitException("配置中心返回异常");
        }

        String ips = rootElement.getChildText(SaturnCommonConstants.CONFIG_CENTER_IP);
        return ips;
    }

}
