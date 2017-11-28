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
import java.net.Socket;
import java.net.UnknownHostException;

import com.huifu.saturn.runtime.context.SaturnApplicationContext;

/**
 * 加密解密 签名
 * 从钱管家迁移
 * @author su.zhang
 * @version $Id: SecureBase.java, v 0.1 2012-9-28 上午10:09:55 su.zhang Exp $
 */
public class SecureBase {
    private String EncMsg        = "", ChkValue = "";
    private byte[] DecMsg;

    private byte   bSendData[], bRecvData[];
    private byte   bData[];
    private String sData, RespCode;
    private int    len, ret;

    //以下四项从配置文件读取
    private String sserver1_ip   = (String) SaturnApplicationContext
                                     .getApplicationContext("global.security.server.main.ip");
    private int    sserver1_port = Integer.parseInt((String) SaturnApplicationContext
                                     .getApplicationContext("global.security.server.main.port"));

    private String sserver2_ip   = (String) SaturnApplicationContext
                                     .getApplicationContext("global.security.server.bak.ip");
    private int    sserver2_port = Integer.parseInt((String) SaturnApplicationContext
                                     .getApplicationContext("global.security.server.bak.port"));

    SendToFnm      sendToFnm     = new SendToFnm();

    /**
     * Getter method for property <tt>sserver2_ip</tt>.
     * 
     * @return property value of sserver2_ip
     */
    public String getSserver2_ip() {
        return sserver2_ip;
    }

    /**
     * Setter method for property <tt>sserver2_ip</tt>.
     * 
     * @param sserver2_ip value to be assigned to property sserver2_ip
     */
    public void setSserver2_ip(String sserver2_ip) {
        this.sserver2_ip = sserver2_ip;
    }

    /**
     * Getter method for property <tt>sserver2_port</tt>.
     * 
     * @return property value of sserver2_port
     */
    public int getSserver2_port() {
        return sserver2_port;
    }

    /**
     * Setter method for property <tt>sserver2_port</tt>.
     * 
     * @param sserver2_port value to be assigned to property sserver2_port
     */
    public void setSserver2_port(int sserver2_port) {
        this.sserver2_port = sserver2_port;
    }

    public SecureBase() {

    }

    /**
    *   8010 sign 
    *   8011 verify sign 
    *   8020 enc by rsa
    *   8021 dec by rsa
    *   8080 enc and sign by rsa
    *   8081 dec and verify sign by rsa
    */

    /**     加密和签名：
    *   send : 8080+MerId(6)+HashType(2)+MsgBuf
    *   recv : 8080+MerId(6)+RespCode(4)+EncMd( 256 ) + EncMsg
    */
    public int EncAndSignMsg(String MerId, int HashType, byte[] MsgBuf) {

        len = MsgBuf.length;
        bSendData = new byte[12 + len];
        gmsUtilAppend(bSendData, 0, "8080" + MerId, 10, 'r');
        sData = "" + HashType;
        gmsUtilAppend(bSendData, 10, sData, 2, 'r');
        gmsUtilAppend(bSendData, 12, MsgBuf, len, 'r');
        ret = SendAndRecv();
        if (ret < 0) {
            RespCode = "10010";
            return -1;
        }

        sData = new String(bRecvData);
        RespCode = sData.substring(10, 14);
        if (!RespCode.equals("0000")) {
            RespCode = "0" + RespCode;
            return (-1);
        }

        ChkValue = sData.substring(14, 270);
        EncMsg = sData.substring(270);
        return 0;
    }

    /**     解密和验签名  
    *   send : 8081+MerId(6)+HashType(2)+ChkValue(256)+EncMsg
    *   recv : 8081+MerId(6)+RespCode(4)+DecMsg
    */
    public int DecAndVeriSignMsg(String MerId, int HashType, String MsgBuf, String ChkValue) {

        len = MsgBuf.length();
        bSendData = new byte[268 + len];
        gmsUtilAppend(bSendData, 0, "8081" + MerId, 10, 'r');
        sData = "" + HashType;
        gmsUtilAppend(bSendData, 10, sData, 2, 'r');
        gmsUtilAppend(bSendData, 12, ChkValue, 256, 'r');
        gmsUtilAppend(bSendData, 268, MsgBuf, len, 'r');
        ret = SendAndRecv();
        //System.out.println("ret after SendAndRecv is [" + ret + "]");
        if (ret < 0) {
            RespCode = "10010";
            return -1;
        }

        sData = new String(bRecvData);
        RespCode = sData.substring(10, 14);
        //System.out.println("RespCode[" + RespCode + "]");
        if (!RespCode.equals("0000")) {
            RespCode = "0" + RespCode;
            return (-1);
        }

        DecMsg = gmsUtilExtract(bRecvData, 14, ret - 14);
        return 0;
    }

    /**     send : 8010+MerId(6)+HashType(2)+MsgBuf
    *   recv : 8010+MerId(6)+RespCode(4)+EncMd( 256 )
    */
    public int SignMsg(String MerId, int HashType, byte[] MsgBuf) {
        len = MsgBuf.length;
        bSendData = new byte[12 + len];
        gmsUtilAppend(bSendData, 0, "8010" + MerId, 10, 'r');
        sData = "" + HashType;
        gmsUtilAppend(bSendData, 10, sData, 2, 'r');
        gmsUtilAppend(bSendData, 12, MsgBuf, len, 'r');
        ret = SendAndRecv();
        if (ret < 0) {
            RespCode = "10010";
            return -1;
        }

        sData = new String(bRecvData);
        RespCode = sData.substring(10, 14);
        if (!RespCode.equals("0000")) {
            RespCode = "0" + RespCode;
            return (-1);
        }

        ChkValue = sData.substring(14);

        return 0;
    }

    /**     send : 8011+MerId(6)+HashType(2)+MsgBuf+ChkValue(256)
    *   recv : 8011+MerId(6)+RespCode(4)
    */
    public int VeriSignMsg(String MerId, int HashType, byte[] MsgBuf, String ChkValue) {
        len = MsgBuf.length;
        bSendData = new byte[12 + len + 256];
        gmsUtilAppend(bSendData, 0, "8011" + MerId, 10, 'r');
        sData = "" + HashType;
        gmsUtilAppend(bSendData, 10, sData, 2, 'r');
        gmsUtilAppend(bSendData, 12, MsgBuf, len, 'r');
        gmsUtilAppend(bSendData, 12 + len, ChkValue, 256, 'r');
        ret = SendAndRecv();
        if (ret < 0) {
            RespCode = "10010";
            return -1;
        }

        sData = new String(bRecvData);
        //System.out.println( "***************" + sData  );

        RespCode = sData.substring(10, 14);
        if (!RespCode.equals("0000")) {
            RespCode = "0" + RespCode;
            return (-1);
        }
        return 0;
    }

    /**     send : 8020+MerId(6)+MsgBuf
    *   recv : 8020+Merid(6)+RespCode(4)+EncMsg 
    */
    public int EncMsgRsa(String MerId, byte[] MsgBuf) {

        len = MsgBuf.length;
        bSendData = new byte[10 + len];
        gmsUtilAppend(bSendData, 0, "8020" + MerId, 10, 'r');
        gmsUtilAppend(bSendData, 10, MsgBuf, len, 'r');
        ret = SendAndRecv();
        if (ret < 0)
            return ret;

        sData = new String(bRecvData);
        RespCode = sData.substring(10, 14);
        if (!RespCode.equals("0000")) {
            ret = new Integer(RespCode).intValue();
            return (-10000 + ret * (-1));
        }

        EncMsg = sData.substring(14);

        return 0;
    }

    /**     send : 8021+MerId(6)+MsgBuf
    *   recv : 8021+Merid(6)+RespCode(4)+DecMsg 
    */
    public int DecMsgRsa(String MerId, String MsgBuf) {

        len = MsgBuf.length();
        bSendData = new byte[10 + len];
        gmsUtilAppend(bSendData, 0, "8021" + MerId, 10, 'r');
        gmsUtilAppend(bSendData, 10, MsgBuf, len, 'r');
        ret = SendAndRecv();
        if (ret < 0)
            return ret;
        //System.out.println( "comm_ret" + ret ) ;

        sData = new String(bRecvData, 0, 14);
        RespCode = sData.substring(10, 14);
        if (!RespCode.equals("0000")) {
            ret = new Integer(RespCode).intValue();
            return (-10000 + ret * (-1));
        }
        //System.out.println( "RespCode" + RespCode + "brecv" + bRecvData.length ) ;

        DecMsg = gmsUtilExtract(bRecvData, 14, ret - 14);
        //System.out.println("DecMsg in DecMsgRsa in SecureBase is [" + (new String(DecMsg)) + "]");
        //System.out.println( "dec succ" ) ;

        return 0;
    }

    public String getEncMsg() {
        return EncMsg;
    }

    public byte[] getDecMsg() {
        return DecMsg;
    }

    public String getChkValue() {
        return ChkValue;
    }

    public int EncMsgDes(String MerId, byte[] MsgBuf) {
        return 0;
    }

    public int DecMsgDes(String MerId, String MsgBuf) {
        return 0;
    }

    public int EncMsg3Des(String MerId, byte[] MsgBuf) {
        return 0;
    }

    public int DecMsg3Des(String MerId, String MsgBuf) {
        return 0;
    }

    private int SendAndRecv() {
        int len = ReadWriteTcp(sserver1_ip, sserver1_port);
        int warnResp = 0;
        if (len < 0) {
            warnResp = sendToFnm.snd_fnm('F', "01", "01", "0010", "无法连接安全服务器(主)");
            System.out.println("报警 server1 is down, warnResp[" + warnResp + "]");

            len = ReadWriteTcp(sserver2_ip, sserver2_port);
            if (len < 0) {
                warnResp = sendToFnm.snd_fnm('F', "01", "01", "0010", "无法连接安全服务器(客)");
                System.out.println("报警 server2 is down, warnResp[" + warnResp + "]");
            }
        }

        return len;

    }

    private int ReadWriteTcp(String sserver_ip, int sserver_port) {
        Socket sid = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        boolean CommSucc;
        int len0;
        try {

            sid = new Socket(sserver_ip, sserver_port);
            out = new DataOutputStream(sid.getOutputStream());
            in = new DataInputStream(sid.getInputStream());

            sid.setSoTimeout(3000);

            String sMsgLen = "" + bSendData.length;
            bData = new byte[4];
            gmsUtilAppend(bData, 0, "" + bSendData.length, 4, 'r');
            out.write(bData);
            out.write(bSendData);

            in.read(bData, 0, 4);
            len0 = (new Integer(new String(bData))).intValue();

            int k = 0;
            int j = len0;
            bRecvData = new byte[len0];
            while (true) {
                if (j <= 0)
                    break;

                len = in.read(bRecvData, k, j);
                if (len < 0)
                    break;

                k += len;
                j -= len;
            }

            sid.close();
            in.close();
            out.close();
        } catch (UnknownHostException e) {
            try {
                sid.close();
                in.close();
                out.close();
            } catch (IOException sube) {
                System.out.println("io err" + sube + "]");
                return -11;
            } catch (Exception ee) {
                System.out.println("other" + ee + "]");
                return -11;
            }
            // can not find host
            System.out.println("unkown host err" + e + "]");
            return -12;
        } catch (IOException e) {
            try {
                sid.close();
                in.close();
                out.close();
            } catch (IOException sube) {
                System.out.println("io err" + sube + "]");
                return -11;
            } catch (Exception ee) {
                System.out.println("other" + ee + "]");
                return -11;
            }
            // can not open read or write port
            System.out.println("io err" + e + "]");
            return -14;
        } catch (Exception e) {
            try {
                sid.close();
                in.close();
                out.close();
            } catch (IOException sube) {
                System.out.println("io err" + sube + "]");
                return -11;
            }
            System.out.println("other err" + e + "]");
            return -15;
        }

        return (len0);
    }

    public String getRespCode() {
        return RespCode;
    }

    /**
    *将一个字符串取byte[]后附加在另一个后面。
    *先将bData从start开始len位填充为一固定值。然后再附加byte[]。
    *@param bData  前面的byte[]
    *@param start  位移从前面的byte[]的哪一位开始附加
    *@param s  附加用来取byte[]的String
    *@param len 附加多少位byte
    *@param mode  如果是'|'，则开始填充' '，否则填充'0'；
    *如果是'|'，则从start开始顺位附加，否则从start+len-s.length处开始附加。
    */

    private void gmsUtilAppend(byte bData[], int Start, String s, int len, char mode) {

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
    *将一个byte[] 附加在另一个后面。
    *先将bData从start开始len位填充为一固定值。然后再附加byte[]。
    *@param bData  前面的byte[]
    *@param start  位移从前面的byte[]的哪一位开始附加
    *@param s  附加的byte[]
    *@param len 附加多少位byte
    *@param mode  如果是'|'，则开始填充' '，否则填充'0'；
    *如果是'|'，则从start开始顺位附加，否则从start+len-s.length处开始附加。

    */
    private void gmsUtilAppend(byte bData[], int Start, byte[] s, int len, char mode) {

        int l = s.length;

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
                bData[Start + i] = s[i];
        } else {
            for (i = 0; i < l; i++)
                bData[Start + len - l + i] = s[i];
        }

    }

    /**
    *将一个byte[]从start开始截取len位。
    *@param bData 原byte数组
    *@param start 截取开始处
    *@param len 截取位数
    *@return 截取出来的byte[]
    */
    private byte[] gmsUtilExtract(byte bData[], int start, int len) {
        int i;

        byte bTmp[] = new byte[len];

        for (i = 0; i < len; i++) {
            bTmp[i] = bData[start + i];
        }

        return (bTmp);

    }

    /**
     * Setter method for property <tt>sserver1_ip</tt>.
     * 
     * @param sserver1_ip value to be assigned to property sserver1_ip
     */
    public void setSserver1_ip(String sserver1_ip) {
        this.sserver1_ip = sserver1_ip;
    }

    /**
     * Getter method for property <tt>sserver1_ip</tt>.
     * 
     * @return property value of sserver1_ip
     */
    public String getSserver1_ip() {
        return sserver1_ip;
    }

    /**
     * Setter method for property <tt>sserver1_port</tt>.
     * 
     * @param sserver1_port value to be assigned to property sserver1_port
     */
    public void setSserver1_port(int sserver1_port) {
        this.sserver1_port = sserver1_port;
    }

    /**
     * Getter method for property <tt>sserver1_port</tt>.
     * 
     * @return property value of sserver1_port
     */
    public int getSserver1_port() {
        return sserver1_port;
    }

}
