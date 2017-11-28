/**
 * 
 *上海汇付网络科技有限公司
 *
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.util.netpay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huifu.saturn.common.constants.code.systemCode.ChinaPNRSubSystemCode;
import com.huifu.saturn.common.constants.code.systemCode.ChinaPNRSystemCode;
import com.huifu.saturn.runtime.context.SaturnApplicationContext;

/**
 * 从钱管家迁移 报警
 * @author su.zhang
 * @version $Id: SendToFnm.java, v 0.1 2012-9-28 上午10:27:10 su.zhang Exp $
 */
public class SendToFnm {

    final static Logger   logger       = LoggerFactory.getLogger(SendToFnm.class);

    private static String monitor_ip   = (String) SaturnApplicationContext
                                           .getApplicationContext("global.monitor.server.main.ip");

    private static int    monitor_port = Integer
                                           .parseInt((String) SaturnApplicationContext
                                               .getApplicationContext("global.monitor.server.main.port"));

    public SendToFnm() {
    }

    public SendToFnm(String monitor_ip, int monitor_port) {

        this.monitor_ip = monitor_ip;
        this.monitor_port = monitor_port;
    }

    /**
     * 系统号转换
     * @param newSysId
     * @return
     */
    public static String sysIdConvert(String newSysId) {
        if (StringUtils.equals(ChinaPNRSystemCode.ACCT.getSystemCode(), newSysId)) {
            return "15";
        } else if (StringUtils.equals(ChinaPNRSystemCode.PAY.getSystemCode(), newSysId)) {
            return "16";
        } else if (StringUtils.equals(ChinaPNRSystemCode.REM.getSystemCode(), newSysId)) {
            return "17";
        } else if (StringUtils.equals(ChinaPNRSystemCode.UBS.getSystemCode(), newSysId)) {
            return "18";
        } else if (StringUtils.equals(ChinaPNRSystemCode.CASH.getSystemCode(), newSysId)) {
            return "19";
        } else if (StringUtils.equals(ChinaPNRSystemCode.BUSER.getSystemCode(), newSysId)) {
            return "20";
        } else if (StringUtils.equals(ChinaPNRSystemCode.CUSER.getSystemCode(), newSysId)) {
            return "21";
        } else if (StringUtils.equals(ChinaPNRSystemCode.RBS.getSystemCode(), newSysId)) {
            return "22";
        } else if (StringUtils.equals(ChinaPNRSystemCode.RECV.getSystemCode(), newSysId)) {
            return "23";
        } else if (StringUtils.equals(ChinaPNRSystemCode.POS.getSystemCode(), newSysId)) {
            return "24";
        } else if (StringUtils.equals(ChinaPNRSystemCode.MAG.getSystemCode(), newSysId)) {
            return "25";
        } else if (StringUtils.equals(ChinaPNRSystemCode.RPT.getSystemCode(), newSysId)) {
            return "26";
        }
        throw new RuntimeException("系统号不存在，newSysId=" + newSysId);
    }

    /**
     * 系统号转换
     * @param newSysId
     * @return
     */
    public static String subSysIdConvert(String newSubSysId) {
        if (StringUtils.equals(ChinaPNRSubSystemCode.ACCT.getSubSysCode(), newSubSysId)) {
            return "00";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.ACCTMAG.getSubSysCode(), newSubSysId)) {
            return "01";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.ACCTSERVICE.getSubSysCode(),
            newSubSysId)) {
            return "02";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.BUSER.getSubSysCode(), newSubSysId)) {
            return "00";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.BUSERMAG.getSubSysCode(), newSubSysId)) {
            return "01";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.CASH.getSubSysCode(), newSubSysId)) {
            return "00";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.CASHMAG.getSubSysCode(), newSubSysId)) {
            return "01";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.UBS.getSubSysCode(), newSubSysId)) {
            return "00";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.UBSMAG.getSubSysCode(), newSubSysId)) {
            return "01";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.RBS.getSubSysCode(), newSubSysId)) {
            return "00";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.RBSMAG.getSubSysCode(), newSubSysId)) {
            return "01";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.RECV.getSubSysCode(), newSubSysId)) {
            return "00";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.POS.getSubSysCode(), newSubSysId)) {
            return "00";
        } else if (StringUtils.equals(ChinaPNRSubSystemCode.MAG.getSubSysCode(), newSubSysId)) {
            return "00";
        }
        throw new RuntimeException("子系统号不存在，newSubSysId=" + newSubSysId);
    }

    /**
     * 
     * 
     * @param fault_level
     * @param sys_id   ChinaPNRSystemCode
     * @param sub_sys_id  ChinaPNRSubSystemCode
     * @param err_id
     * @param err_desc
     * @return
     */
    public static int snd_fnm(char fault_level, String sys_id, String sub_sys_id, String err_id,
                              String err_desc1) {
        String err_desc = "";
        try {
            err_desc = new String(err_desc1.getBytes(), "GBK");
        } catch (UnsupportedEncodingException e) {
            logger.error("utf8转gbk错误", e);
        }
        sys_id = sysIdConvert(sys_id);
        sub_sys_id = subSysIdConvert(sub_sys_id);
        if (sys_id.length() != 2 || sub_sys_id.length() != 2 || err_id.length() > 8
            || err_id.length() < 4 || err_desc.length() > 256 || err_desc.length() < 10) {
            return -1;
        }

        StringBuffer sBuf = new StringBuffer("0");
        sBuf.append("|").append(fault_level);
        sBuf.append("|").append(sys_id);
        sBuf.append("|").append(sub_sys_id);
        sBuf.append("|").append(err_id);
        sBuf.append("|").append(err_desc).append("|");

        int ret;
        ret = SendData(monitor_ip, monitor_port, sBuf);
        if (ret < 0) {
            return -1;
        }

        return 0;
    }

    static int SendData(String fnm_ip, int fnm_port, StringBuffer sBuf) {
        Socket sid = null;
        DataOutputStream out = null;
        DataInputStream in = null;

        try {
            sid = new Socket(fnm_ip, fnm_port);
            out = new DataOutputStream(sid.getOutputStream());
            in = new DataInputStream(sid.getInputStream());
            sid.setSoTimeout(10000);

            byte bData[], bSendData[];
            bSendData = sBuf.toString().getBytes("GBK");

            bData = new byte[4];
            append(bData, 0, "" + bSendData.length, 4, 'r');
            out.write(bData);
            out.write(bSendData);

            bData = new byte[4];
            in.read(bData, 0, 4);
            String sData = new String(bData);
            if (!sData.equals("0002")) {
                System.out.println("fnm in down");
                out.close();
                in.close();
                sid.close();
                return -1;
            }

            bData = new byte[2];
            in.read(bData, 0, 2);

            out.close();
            in.close();
            sid.close();

            sData = new String(bData);
            if (!sData.equals("00")) {
                System.out.println("fnm in down");
                return -1;
            }
        } catch (IOException e) {
            System.out.println("sendtoho err" + e + "]");
            try {
                sid.close();
                in.close();
                out.close();
            } catch (IOException sube) {
                System.out.println("io err" + sube + "]");
                return -1;
            } catch (Exception ee) {
                System.out.println("other err" + ee + "]");
                return -1;
            }
            // can not open read or write port
            return -5;
        } catch (Exception e) {
            System.out.println("other err" + e + "]");
            try {
                sid.close();
                in.close();
                out.close();
            } catch (IOException sube) {
                System.out.println("io err" + sube + "]");
                return -10;
            } catch (Exception ee) {
                System.out.println("other err" + ee + "]");
                return -1;
            }
            return -15;
        }

        return 0;
    }

    static void append(byte bData[], int Start, String s, int len, char mode) {

        byte bTmp[] = s.getBytes();
        int l = bTmp.length;

        byte bb = (byte) '0';
        if (mode == 'l') {
            bb = (byte) ' ';
        }

        int i;
        for (i = 0; i < len; i++)
            bData[Start + i] = bb;

        if (l > len)
            l = len;

        if (mode == 'l') {
            for (i = 0; i < l; i++)
                bData[Start + i] = bTmp[i];
        } else {
            for (i = 0; i < l; i++)
                bData[Start + len - l + i] = bTmp[i];
        }

    }

    /**
     * Setter method for property <tt>monitor_port</tt>.
     * 
     * @param monitor_port value to be assigned to property monitor_port
     */
    public void setMonitor_port(int monitor_port) {
        this.monitor_port = monitor_port;
    }

    /**
     * Getter method for property <tt>monitor_port</tt>.
     * 
     * @return property value of monitor_port
     */
    public int getMonitor_port() {
        return monitor_port;
    }

    /**
     * Getter method for property <tt>monitor_ip</tt>.
     * 
     * @return property value of monitor_ip
     */
    public String getMonitor_ip() {
        return monitor_ip;
    }

    /**
     * Setter method for property <tt>monitor_ip</tt>.
     * 
     * @param monitor_ip value to be assigned to property monitor_ip
     */
    public void setMonitor_ip(String monitor_ip) {
        this.monitor_ip = monitor_ip;
    }

}
