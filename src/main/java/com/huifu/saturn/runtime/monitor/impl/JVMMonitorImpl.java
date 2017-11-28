/**
 * 
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.monitor.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;

import com.huifu.saturn.runtime.monitor.JVMMonitor;

/**
 * 
 * @author zhanghaijie
 * @version $Id: JVMMonitor.java, v 0.1 2012-9-26 下午01:38:53 zhanghaijie Exp $
 */
public class JVMMonitorImpl implements JVMMonitor {

    //JVM监控信息
    private static final MemoryMXBean  memoryMXBean  = ManagementFactory.getMemoryMXBean();
    private static final ThreadMXBean  threadMXBean  = ManagementFactory.getThreadMXBean();

    private static final String        SPLITTER      = ",";

    private static final DecimalFormat decimalformat = new DecimalFormat("#.##");

    public String jvmInfo() {
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(formatValue(memoryUsage.getUsed())).append(SPLITTER);
        sb.append(formatValue(memoryUsage.getMax()));
        sb.append(")");

        sb.append("(");
        sb.append(formatNanosecond(threadMXBean.getCurrentThreadCpuTime())).append(SPLITTER);
        sb.append(threadMXBean.getDaemonThreadCount()).append(SPLITTER);
        sb.append(threadMXBean.getThreadCount()).append(SPLITTER);
        sb.append(threadMXBean.getTotalStartedThreadCount());
        sb.append(")");

        return sb.toString();
    }

    /**
     * 将字节单位转为M，保留小数点两位
     * */
    private static String formatValue(long value) {
        Double tempValue = new Double(value) / 1024 / 1024;
        return decimalformat.format(tempValue);
    }

    private String formatNanosecond(long value) {
        Double tempValue = new Double(value) / 1000000000;
        return decimalformat.format(tempValue);
    }
}
