/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.context;

import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.huifu.saturn.common.constants.constants.SaturnCommonConstants;
import com.huifu.saturn.common.utils.SaturnLoggerUtils;
import com.huifu.saturn.runtime.configuration.GlobalConfigService;
import com.huifu.saturn.runtime.exception.SaturnInitException;

/**
 * 
 * @author zhanghaijie
 * @version $Id: SaturnContextLoaderListener.java, v 0.1 2012-9-5 下午04:54:25 zhanghaijie Exp $
 */
public class SaturnContextLoaderListener implements ServletContextListener {

    private static final Logger logger            = Logger
                                                      .getLogger(SaturnContextLoaderListener.class);

    private boolean             initiated         = false;

    private boolean             loadConfiguration = false;

    private String              applicationName;

    /** 
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public synchronized void contextInitialized(ServletContextEvent sce) {
        if (!initiated) {
            initContextParam(sce);
            initConfigProperties();
            initConfig();
            initiated = true;
        }
    }

    /**
     * 
     * @param sce
     */
    private void initContextParam(ServletContextEvent sce) {
        applicationName = sce.getServletContext().getInitParameter(
            SaturnCommonConstants.APPLICATION_NAME);
        String loadConfig = sce.getServletContext().getInitParameter(
            SaturnCommonConstants.LOAD_CONFIGURATION);
        if (StringUtils.isNotBlank(loadConfig)) {
            loadConfiguration = Boolean.valueOf(loadConfig);
        }
        SaturnApplicationContext.setApplicationContext(SaturnCommonConstants.APPLICATION_NAME,
            applicationName);

    }

    /**
     * 初始化全局配置
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    private void initConfig() {
        try {
            GlobalConfigService globalConfigService = (GlobalConfigService) Class.forName(
                GlobalConfigService.class.getName()).newInstance();
            globalConfigService.initGlobalConfig(SaturnCommonConstants.GLOBAL_CONFIG);
            if (loadConfiguration) {
                globalConfigService.initGlobalConfig(applicationName);
            }
        } catch (Exception e) {
            SaturnLoggerUtils.error(logger, e, "【系统初始化】初始化失败,GlobalConfigService加载失败");
            throw new SaturnInitException("GlobalConfigService加载失败", e);
        }
    }

    /**
     * 初始化本地配置
     */
    private void initConfigProperties() {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(SaturnCommonConstants.SATURN_PROPERTIES_FILE);
            Properties properties = new Properties();
            properties.load(fileInputStream);
            SaturnApplicationContext.setApplicationContext(
                SaturnCommonConstants.CONFIG_CENTER_IP_ADDRESS,
                properties.getProperty(SaturnCommonConstants.CONFIG_CENTER_IP_ADDRESS));
            SaturnApplicationContext.setApplicationContext(
                SaturnCommonConstants.CONFIG_CENTER_APPLICATION_NAME,
                properties.getProperty(SaturnCommonConstants.CONFIG_CENTER_APPLICATION_NAME));
            SaturnApplicationContext.setApplicationContext(
                SaturnCommonConstants.CONFIG_CENTER_APPLICATION_PORT,
                properties.getProperty(SaturnCommonConstants.CONFIG_CENTER_APPLICATION_PORT));
            SaturnApplicationContext.setApplicationContext(SaturnCommonConstants.ENVIRONMENT,
                properties.getProperty(SaturnCommonConstants.ENVIRONMENT));
        } catch (Exception e) {
            SaturnLoggerUtils.error(logger, e, "【系统初始化】配置文件读取失败");
            throw new SaturnInitException("配置文件读取失败", e);
        } finally {
            try {
                fileInputStream.close();
            } catch (Exception e) {
                SaturnLoggerUtils.error(logger, e, "【系统初始化】流关闭失败");
                throw new SaturnInitException("流关闭失败", e);
            }
        }
    }

    /** 
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        SaturnApplicationContext.destroy();
        initiated = false;
        loadConfiguration = false;
    }

}
