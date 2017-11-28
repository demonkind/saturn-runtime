/**
 * 
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.xfire.client.Client;

import com.huifu.common.utils.xml.configCenter.SaturnConfigCenterUtil;
import com.huifu.saturn.common.constants.constants.SaturnCommonConstants;
import com.huifu.saturn.common.utils.SaturnLoggerUtils;
import com.huifu.saturn.runtime.context.SaturnApplicationContext;
import com.huifu.saturn.runtime.exception.SaturnInitException;

/**
 * 全局变量配置信息
 * 
 * @author zhanghaijie
 * @version $Id: GlobalConfigService.java, v 0.1 2012-10-8 上午10:48:31 zhanghaijie Exp $
 */
public class GlobalConfigService {

    private static final Logger logger                          = Logger
                                                                    .getLogger(GlobalConfigService.class);

    /** ConfigCenter 获取配置信息 */
    private static final String CONFIG_CENTER_GET_CONFIGURATION = "getConfiguration";

    /**  */
    private static final String CONFIGURATION_SERVICE           = "configurationService";

    private String              url;

    public synchronized void initGlobalConfig(String systemName) {
        SaturnLoggerUtils.info(logger, "【系统初始化】配置信息加载,systemName:", systemName);
        assembleURL();
        Map<String, String> map = getGlobalConfig(systemName);
        setApplicationContext(map);
    }

    /**
     * 添加全局变量
     * 
     * @param map
     */
    private void setApplicationContext(Map<String, String> map) {
        Set<String> keys = map.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = map.get(key);
            SaturnLoggerUtils.info(logger, "【系统初始化】添加变量:", key, "=", value);
            SaturnApplicationContext.setApplicationContext(key, value);
        }
    }

    /**
     * 获取配置信息
     * 
     * @return
     */
    private Map<String, String> getGlobalConfig(String systemName) {
        Object[] returnXML = invokeRemoteService(systemName);
        if (null == returnXML) {
            SaturnLoggerUtils.error(logger, null, "【配置中心加载失败】配置中心返回结果为空", url, "systemName:",
                systemName);
            throw new SaturnInitException("配置中心加载失败,返回参数异常" + systemName);
        }

        String xml = returnXML[0].toString();
        SaturnLoggerUtils.info(logger, "【配置加载】返回配置项：", xml);

        Map<String, String> map;
        try {
            map = SaturnConfigCenterUtil.getConfiguration(xml);
        } catch (Exception e) {
            SaturnLoggerUtils.error(logger, e, "【系统初始化】变量获取失败", xml, "请求参数:", url, "systemName:",
                systemName);
            throw new SaturnInitException("变量获取失败,返回参数异常" + systemName);
        }

        return map;
    }

    /**
     * 拼装URL
     */
    private void assembleURL() {
        StringBuffer url = new StringBuffer("http://");
        url.append((String) SaturnApplicationContext
            .getApplicationContext(SaturnCommonConstants.CONFIG_CENTER_IP_ADDRESS));
        url.append(":");
        url.append((String) SaturnApplicationContext
            .getApplicationContext(SaturnCommonConstants.CONFIG_CENTER_APPLICATION_PORT));
        url.append("/");
        url.append((String) SaturnApplicationContext
            .getApplicationContext(SaturnCommonConstants.CONFIG_CENTER_APPLICATION_NAME));
        url.append("/");
        url.append("services/");
        url.append(CONFIGURATION_SERVICE);
        url.append("?wsdl");
        SaturnLoggerUtils.info(logger, "【系统加载】配置信息获取，配置中心地址：", url.toString());
        this.url = url.toString();
    }

    /**
     * 
     * @return
     */
    private Object[] invokeRemoteService(String systemName) {
        Client c = null;
        try {
            c = new Client(new URL(url));
            Object[] ob = c.invoke(
                CONFIG_CENTER_GET_CONFIGURATION,
                new Object[] {
                        systemName,
                        SaturnApplicationContext
                            .getApplicationContext(SaturnCommonConstants.ENVIRONMENT) });
            return ob;
        } catch (MalformedURLException e) {
            SaturnLoggerUtils.error(logger, e, "【配置中心加载失败】配置中心地址异常，配置中心地址:", url, "systemName:",
                systemName);
            throw new SaturnInitException("配置中心地址异常", e);
        } catch (Exception e) {
            SaturnLoggerUtils.error(logger, e, "【配置中心加载失败】系统异常，配置中心地址：", url, "systemName:",
                systemName);
            throw new SaturnInitException("系统异常", e);
        } finally {
            if (null != c) {
                try {
                    c.close();
                } catch (Exception e) {
                    SaturnLoggerUtils.error(logger, e, "【配置中心加载异常】连接关闭失败:", url, "systemName:",
                        systemName);
                    throw new SaturnInitException("系统异常", e);
                }
            }
        }
    }
}
