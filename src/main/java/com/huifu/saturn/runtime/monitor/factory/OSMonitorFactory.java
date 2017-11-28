/**
 * 
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.monitor.factory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.huifu.saturn.common.constants.constants.SaturnCommonConstants;
import com.huifu.saturn.common.utils.SaturnLoggerUtils;
import com.huifu.saturn.runtime.monitor.OSMonitor;
import com.huifu.saturn.runtime.monitor.impl.LinuxMonitor;

/**
 * 
 * @author zhanghaijie
 * @version $Id: OSMonitorFactory.java, v 0.1 2012-9-28 下午08:20:48 zhanghaijie Exp $
 */
public class OSMonitorFactory implements FactoryBean<Object>, InitializingBean {

    private static final Logger logger = Logger.getLogger(OSMonitorFactory.class);

    private OSMonitor           osMonitor;

    /** 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        String osName = System.getProperty(SaturnCommonConstants.OS_NAME);
        SaturnLoggerUtils.info(logger, "【系统初始化】操作平台信息", osName);
        if (StringUtils.contains(osName, SaturnCommonConstants.OPERATOR_SYS_LINUX)) {
            osMonitor = new LinuxMonitor();
        }
    }

    /** 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @Override
    public Object getObject() throws Exception {
        return osMonitor;
    }

    /** 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<?> getObjectType() {
        return null;
    }

    /** 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

}
