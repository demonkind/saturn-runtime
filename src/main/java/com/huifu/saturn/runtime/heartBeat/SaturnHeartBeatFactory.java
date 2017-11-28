/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.heartBeat;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.huifu.saturn.common.utils.SaturnLoggerUtils;
import com.huifu.saturn.runtime.exception.SaturnInitException;
import com.huifu.saturn.runtime.monitor.JVMMonitor;
import com.huifu.saturn.runtime.monitor.OSMonitor;
import com.huifu.saturn.runtime.mq.sender.MessageSender;

/**
 * 
 * @author zhanghaijie
 * @version $Id: SaturnHeartBeatFactory.java, v 0.1 2012-9-4 下午04:36:59 zhanghaijie Exp $
 */
public class SaturnHeartBeatFactory implements FactoryBean<Object>, InitializingBean {

    private static final Logger logger = Logger.getLogger(SaturnHeartBeatFactory.class);

    private MessageSender       messageSender;

    private String              heartBeatTime;

    private JVMMonitor          jvmMonitor;

    private String              ip;

    private OSMonitor           osMonitor;

    /** 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @Override
    public Object getObject() throws Exception {
        SaturnHeartBeatTask saturnHeartBeatTask = new SaturnHeartBeatTask();
        saturnHeartBeatTask.setMessageSender(messageSender);
        saturnHeartBeatTask.setHeartBeatTime(Long.valueOf(heartBeatTime));
        saturnHeartBeatTask.setIp(ip);
        saturnHeartBeatTask.setJvmMonitor(jvmMonitor);
        if (null != osMonitor) {
            saturnHeartBeatTask.setMonitorOS(true);
            saturnHeartBeatTask.setOsMonitor(osMonitor);
        }
        return saturnHeartBeatTask;
    }

    /** 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<Object> getObjectType() {
        return null;
    }

    /** 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    /** 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        ip = getAddress().getHostAddress();
    }

    public InetAddress getAddress() {
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces
                .hasMoreElements();) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || networkInterface.isVirtual()
                    || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        return ip;
                    }
                }
            }
        } catch (SocketException e) {
            SaturnLoggerUtils.error(logger, e, "【系统初始化失败】获取本地IP地址失败");
            throw new SaturnInitException("获取本地IP地址失败", e);
        }
        return null;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void setHeartBeatTime(String heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

    public void setJvmMonitor(JVMMonitor jvmMonitor) {
        this.jvmMonitor = jvmMonitor;
    }

    public void setOsMonitor(OSMonitor osMonitor) {
        this.osMonitor = osMonitor;
    }

}
