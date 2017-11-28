/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.heartBeat;

import org.apache.log4j.Logger;

import com.huifu.saturn.common.utils.SaturnLoggerUtils;
import com.huifu.saturn.runtime.monitor.JVMMonitor;
import com.huifu.saturn.runtime.monitor.OSMonitor;
import com.huifu.saturn.runtime.mq.sender.MessageSender;
import com.huifu.saturn.runtime.threadPool.SaturnTask;

/**
 * 
 * @author zhanghaijie
 * @version $Id: SaturnHeartBeat.java, v 0.1 2012-9-4 下午04:27:54 zhanghaijie Exp $
 */
public class SaturnHeartBeatTask extends SaturnTask {

    private static final Logger logger                     = Logger
                                                               .getLogger(SaturnHeartBeatTask.class);

    private static final String TP_SATURN_APP_MONITOR_INFO = "TP_SATURN_APP_MONITOR_INFO";

    private MessageSender       messageSender;

    private JVMMonitor          jvmMonitor;

    private long                heartBeatTime;

    private String              ip;

    private boolean             monitorOS;

    private OSMonitor           osMonitor;

    /** 
     * @see com.huifu.saturn.runtime.threadPool.SaturnTask#execute()
     */
    @Override
    public synchronized void execute() {
        while (true) {
            try {
                //SaturnLoggerUtils.info(logger, "【心跳】心跳发送");
                //messageSender.send(TP_SATURN_APP_HEART_BEAT, ip);
                //TODO:监控系统没有建立，JVM信息由日志打印
                SaturnLoggerUtils.info(logger, "【JVM监控信息】", jvmMonitor.jvmInfo());
                if (monitorOS) {
                    String osInfo = osMonitor.osMonitorInfo();
                    SaturnLoggerUtils.info(logger, "【OS监控信息】", osInfo);
                }
                Thread.sleep(heartBeatTime);
            } catch (Exception e) {
                SaturnLoggerUtils.error(logger, e, "【心跳】心跳发送失败", ip);
            }
        }
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void setHeartBeatTime(long heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setJvmMonitor(JVMMonitor jvmMonitor) {
        this.jvmMonitor = jvmMonitor;
    }

    public void setMonitorOS(boolean monitorOS) {
        this.monitorOS = monitorOS;
    }

    public void setOsMonitor(OSMonitor osMonitor) {
        this.osMonitor = osMonitor;
    }

}
