/**
 * 
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.monitor.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.huifu.saturn.common.utils.SaturnLoggerUtils;
import com.huifu.saturn.runtime.monitor.OSMonitor;

/**
 * 
 * @author zhanghaijie
 * @version $Id: LinuxCPUMonitor.java, v 0.1 2012-9-28 下午07:42:37 zhanghaijie Exp $
 */
public class LinuxMonitor implements OSMonitor {

    private static final Logger logger = Logger.getLogger(LinuxMonitor.class);

    /** 
     * @see com.huifu.saturn.runtime.monitor.OSMonitor#cupInfo()
     */
    public String osMonitorInfo() {
        return linuxInfo();
    }

    public String linuxInfo() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader brStat = null;
        StringBuffer monitorInfo = new StringBuffer();
        try {
            Process process = Runtime.getRuntime().exec("top -b -n 1");
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            brStat = new BufferedReader(isr);

            brStat.readLine();
            brStat.readLine();

            monitorInfo.append(brStat.readLine());
            monitorInfo.append("\t\n");
            monitorInfo.append(brStat.readLine());

        } catch (IOException ioe) {
            SaturnLoggerUtils.error(logger, ioe, "【Linux平台监控】Linux平台监控信息获取失败");
        } finally {
            freeResource(is, isr, brStat);
        }

        return monitorInfo.toString();
    }

    private void freeResource(InputStream is, InputStreamReader isr, BufferedReader br) {
        try {
            if (is != null)
                is.close();
            if (isr != null)
                isr.close();
            if (br != null)
                br.close();
        } catch (IOException ioe) {
            SaturnLoggerUtils.error(logger, ioe, "【Linux平台监控】Linux平台监控资源关闭失败");
        }
    }

}
