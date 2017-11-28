/**
 * 
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.configuration;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.huifu.common.utils.xml.configCenter.SaturnConfigCenterUtil;
import com.huifu.saturn.common.constants.constants.SaturnCommonConstants;
import com.huifu.saturn.common.utils.SaturnLoggerUtils;
import com.huifu.saturn.runtime.context.SaturnApplicationContext;
import com.huifu.saturn.runtime.mq.listener.SaturnMessageListener;

/**
 * 
 * @author zhanghaijie
 * @version $Id: ConfigInfoServiceListener.java, v 0.1 2012-10-16 上午11:09:22 zhanghaijie Exp $
 */
public class ConfigInfoServiceListener extends SaturnMessageListener {

    private static final Logger logger = Logger.getLogger(ConfigInfoServiceListener.class);

    /** 
     * @see com.huifu.saturn.runtime.mq.listener.SaturnMessageListener#handleMessage(java.lang.Object)
     */
    @Override
    protected void handleMessage(Object message) {
        if (null == message) {
            SaturnLoggerUtils.warn(logger, null, "【配置信息】配置推送信息为空");
            return;
        }

        SaturnLoggerUtils.info(logger, "【配置信息】配置信息刷新", message.toString());
        String xml = message.toString();

        if (StringUtils.isBlank(xml)) {
            SaturnLoggerUtils.warn(logger, null, "【配置信息】配置信息刷新，转换字符串为空");
            return;
        }

        //刷新全局
        configLocalVarialable(xml);
    }

    /**
     * 
     * @param xml
     */
    private synchronized void configLocalVarialable(String xml) {
        SaturnLoggerUtils.info(logger, "【配置信息】配置信息更新", xml);

        try {
            String applicationName = SaturnConfigCenterUtil.getSystemName(xml);
            String env = SaturnConfigCenterUtil.getEnvironment(xml);

            if (StringUtils.equals(env, (String) SaturnApplicationContext
                .getApplicationContext(SaturnCommonConstants.ENVIRONMENT))
                && (StringUtils.equals(applicationName, (String) SaturnApplicationContext
                    .getApplicationContext(SaturnCommonConstants.APPLICATION_NAME)) || StringUtils
                    .equals(applicationName, SaturnCommonConstants.GLOBAL_CONFIG))) {
                Map<String, String> configurations = SaturnConfigCenterUtil.getConfiguration(xml);
                Iterator<String> keys = configurations.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    SaturnApplicationContext.updateApplicationContext(key, configurations.get(key));
                }
                return;
            }
            SaturnLoggerUtils.info(logger, "【配置信息】配置信息舍弃，目标系统:", applicationName, "环境:", env);
        } catch (Exception e) {
            SaturnLoggerUtils.error(logger, e, "【配置信息】配置信息广播内容异常", xml);
        }

    }
}
