/**
 * 
 *上海汇付网络科技有限公司
 *
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.util;

/**
 * 
 * @author su.zhang
 * @version $Id: GsendUtils.java, v 0.1 2012-10-24 下午08:19:24 su.zhang Exp $
 */

import static org.apache.commons.lang.StringUtils.EMPTY;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.huifu.saturn.runtime.context.SaturnApplicationContext;

/**
 * <dl>
 *    <dt><b>Title:</b></dt>
 *    <dd>
 *      通用发送系统Gsend工具类
 *    </dd><p>
 *    <dt><b>Description:</b></dt>
 *    <dd>
 *      <p>none
 *    </dd>
 * </dl>
 *
 * @author bruce
 * @version 1.0, 2011-6-16
 * @since from 天天盈迁移
 * 
 */
public class GsendUtils {

    public static final String CHINESE_CHARSET = "GBK";
    public static final String COLON           = ":";

    private static Log         _logger         = LogFactory.getLog(GsendUtils.class);

    private GsendUtils() {
    }

    private final static String GSEND_MAIN_IP   = (String) SaturnApplicationContext
                                                    .getApplicationContext("global.gsend.server.main.ip");
    private final static int    GSEND_MAIN_PORT = Integer
                                                    .parseInt((String) SaturnApplicationContext
                                                        .getApplicationContext("global.gsend.server.main.port"));
    private final static String GSEND_BAK_IP    = (String) SaturnApplicationContext
                                                    .getApplicationContext("global.gsend.server.bak.ip");
    private final static int    GSEND_BAK_PORT  = Integer
                                                    .parseInt((String) SaturnApplicationContext
                                                        .getApplicationContext("global.gsend.server.bak.port"));

    private final static String MERID           = (String) SaturnApplicationContext
                                                    .getApplicationContext("global.gsend.merid");
    public static int           SUCCESS         = 0;                                                             //成功调用
    public static int           FAILED          = -1;                                                            //失败 

    public static String        GSEND_SUCCESS   = "00";

    public enum GsendTransStat {
        SUCCESS("S"), FAILED("F");
        private String value;

        private GsendTransStat(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static String leftPadLength(String input, int padlen) {
        Assert.notNull(input);
        String pad = EMPTY;
        try {
            pad = Integer.toString(input.getBytes(CHINESE_CHARSET).length);
        } catch (Exception e) {
            _logger.error("error! ", e);
        }
        Assert.isTrue(pad.length() <= padlen);
        return StringUtils.leftPad(pad, padlen, "0").concat(input);
    }

    /**
     * 向通用系统发送数据工具方法
     * 
     * @param sendDate  发送日期，固定8位
     * @param sysId     系统代号，‘02’ – 天天盈联机交易系统，固定2位 
     * @param sysTxnId  流水号，在一个系统内唯一标识一笔交易，2位长度+变长最大40位
     * @param transStat 交易状态该交易状态不发送给商户；用于判断多笔相同交易但状态不同，只保存成功交易。‘S’ – 成功交易 ‘F’ – 失败交易
     * @param merId     商户号 880001
     * @param ordId     订单号，2位长度+变长最大20位，最终接收系统在收到应答后必须在页面的前端（512位以内）打印RECV_ORD_ID_的字样，否则，通用发送系统认为发送失败，将会进入重发队列
     * @param url       商户的接收页面地址，3位长度+变长最大120位
     * @param postData  需要发送给商户的表单数据，4位长度+变长最大2000位
     * 
     * @return 0-成功; -1-失败  
     * 
     * @author eric
     */
    public static final int send(String sendDate, String sysId, String sysTxnId,
                                 GsendTransStat transStat, String merId, String ordId, String url,
                                 String postData) {
        try {
            if (merId == null) {
                merId = MERID;
            }
            if (DataValidator.isStrLenEqual(sendDate, 8) && DataValidator.isStrLenEqual(sysId, 2)
                && DataValidator.isStrLenLessEqual(sysTxnId, 40)
                && DataValidator.isStrLenEqual(transStat.getValue(), 1)
                && DataValidator.isStrLenEqual(merId, 6)
                && DataValidator.isStrLenLessEqual(ordId, 20)
                && DataValidator.isStrLenLessEqual(url, 120)
                && DataValidator.isStrLenLessEqual(postData, 2000)

            ) {

                String sendData = new StringBuilder(sendDate).append(sysId)
                    .append(leftPadLength(sysTxnId, 2)).append(transStat.getValue()).append(merId)
                    .append(leftPadLength(ordId, 2)).append(leftPadLength(url, 3))
                    .append(leftPadLength(postData, 4)).toString();

                _logger.info("==========> is about to send data,sysId=".concat(sysId)
                    .concat(",sysTxnId=").concat(sysTxnId).concat(COLON).concat(sendData));
                String ret = ServersUtils.writeAndReadTcp(GSEND_MAIN_IP, GSEND_MAIN_PORT, sendData);
                _logger.info("通用发送系统(主)[".concat(GSEND_MAIN_IP).concat(COLON)
                    .concat(Integer.toString(GSEND_MAIN_PORT)).concat("] return: ").concat(ret));
                if (!GSEND_SUCCESS.equals(ret)) {
                    _logger.error("与通用发送系统(主)通讯失败,sysId=".concat(sysId).concat(",sysTxnId=")
                        .concat(sysTxnId));
                    ret = ServersUtils.writeAndReadTcp(GSEND_BAK_IP, GSEND_BAK_PORT, sendData);
                    _logger.info("通用发送系统(备)[".concat(GSEND_BAK_IP).concat(COLON)
                        .concat(Integer.toString(GSEND_BAK_PORT)).concat("] return: ").concat(ret));
                    if (!GSEND_SUCCESS.equals(ret)) {
                        _logger.error("与通用发送系统(备)通讯失败,sysId=".concat(sysId).concat(",sysTxnId=")
                            .concat(sysTxnId));
                        return FAILED;
                    }
                }
                _logger.info("==========>向通用发送系统发送成功,sysId=".concat(sysId).concat(",sysTxnId=")
                    .concat(sysTxnId));
                return SUCCESS;

            } else {
                _logger.error("请求参数有错，为空或超长");
                return FAILED;
            }
        } catch (Exception e) {
            _logger.error("失败", e);
            return FAILED;
        }

    }

}
