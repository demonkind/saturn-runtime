/**
 * 
 *上海汇付网络科技有限公司
 *
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 从天天盈迁移
 * @author su.zhang
 * @version $Id: ServerUtils.java, v 0.1 2012-9-28 下午08:59:47 su.zhang Exp $
 */
public class ServersUtils {

    private static Log _logger = LogFactory.getLog(ServersUtils.class);

    /**
     * 
     * @param ip
     *            服务端Ip
     * @param port
     *            服务端Port
     * @param sendData
     *            发送的数据，以|作为分割，如"137018455553|younger|07"
     * @param timeOut
     *            连接超时时间
     * 
     * @return String[] 返回String数组，已按|分割好
     * 
     * @throws BusinessException
     */
    public static String[] writeAndReadTcp2(String ip, int port, String sendData, int timeOut)
                                                                                              throws Exception {
        byte[] bRecvData = writeAndReadTcp(ip, port, sendData, timeOut);
        return new String(bRecvData).split("\\|");
    }

    /**
     * 
     * @param ip
     *            服务端Ip
     * @param port
     *            服务端Port
     * @param sendData
     *            发送的数据，以|作为分割，如"137018455553|younger|07"
     * 
     * @return String[] 返回String数组，已按|分割好
     * 
     * @throws BusinessException
     */
    public static String[] writeAndReadTcp2(String ip, int port, String sendData) {
        try {
            return writeAndReadTcp2(ip, port, sendData, 32000);
        } catch (Exception e) {
            _logger.error("通讯错误", e);
            return null;
        }
    }

    /**
     * 
     * @param ip
     *            服务端Ip
     * @param port
     *            服务端Port
     * @param sendData
     *            发送的数据。pnr内部系统之间发送数据以|作为分割，如"137018455553|younger|07"
     * 
     * @return String，pnr内部系统之间接受的数据以|作为分割符
     */
    public static String writeAndReadTcp(String ip, int port, String sendData) {
        try {
            return new String(writeAndReadTcp(ip, port, sendData, 32000));
        } catch (Exception e) {
            _logger.error("通讯错误", e);
            return null;
        }
    }

    /**
     * 
     * @param ip
     *            服务端Ip
     * @param port
     *            服务端Port
     * @param sendData
     *            发送的数据，以|作为分割，如"137018455553|younger|07"
     * @param timeOut
     *            连接超时时间
     * 
     * @return byte[] 返回byte数组，以|作为分割
     * 
     * @throws BusinessException
     */
    public static byte[] writeAndReadTcp(String ip, int port, String sendData, int timeOut)
                                                                                           throws Exception {
        _logger.debug("[ip:" + ip + "]");
        _logger.debug("[port:" + port + "]");
        _logger.debug("[sendData:" + sendData + "]");
        _logger.debug("[timeOut:" + String.valueOf(timeOut) + "]");
        Socket sid = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        int len0 = 0, len = 0, k = 0;
        byte[] bData, bSendData, bRecvData;

        bSendData = sendData.getBytes();

        try {

            sid = new Socket(ip, port);
            out = new DataOutputStream(sid.getOutputStream());
            in = new DataInputStream(sid.getInputStream());

            sid.setSoTimeout(timeOut);

            bData = new byte[4];
            append(bData, 0, "" + bSendData.length, 4, 'r');
            out.write(bData);
            out.write(bSendData);

            in.read(bData, 0, 4);
            len0 = (new Integer(new String(bData))).intValue();

            int j = len0;
            bRecvData = new byte[len0];
            while (true) {
                if (j <= 0) {
                    break;
                }

                len = in.read(bRecvData, k, j);
                if (len < 0) {
                    break;
                }

                k += len;
                j -= len;
            }

            sid.close();
            in.close();
            out.close();
        } catch (Exception e) {
            try {
                sid.close();
                in.close();
                out.close();
            } catch (IOException sube) {
                _logger.error("io err" + sube + "]");
                return null;
            }
            _logger.error("other err" + e.getMessage() + "]", e);
            throw new Exception("sms send err", e);
        }
        return bRecvData;
    }

    /**
     * 将一个字符串取byte[]后附加在另一个后面。 先将bData从start开始len位填充为一固定值。然后再附加byte[]。
     * 
     * @param bData
     *            前面的byte[]
     * @param start
     *            位移从前面的byte[]的哪一位开始附加
     * @param s
     *            附加用来取byte[]的String
     * @param len
     *            附加多少位byte
     * @param mode
     *            如果是'|'，则开始填充' '，否则填充'0'；
     *            如果是'|'，则从start开始顺位附加，否则从start+len-s.length处开始附加。
     */

    private static void append(byte bData[], int Start, String s, int len, char mode) {

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
}
