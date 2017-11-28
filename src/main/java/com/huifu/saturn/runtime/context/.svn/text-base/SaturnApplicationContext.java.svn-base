/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.context;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.huifu.saturn.common.utils.SaturnLoggerUtils;

/**
 * 
 * @author zhanghaijie
 * @version $Id: SaturnApplicationContext.java, v 0.1 2012-8-29 下午2:27:49 zhanghaijie Exp $
 */
public class SaturnApplicationContext implements Serializable {

    private static final Logger        logger             = Logger
                                                              .getLogger(SaturnApplicationContext.class);

    /**  */
    private static final long          serialVersionUID   = -646506226541457053L;

    private static Map<String, Object> applicationContext = new ConcurrentHashMap<String, Object>();

    public synchronized static void setApplicationContext(String key, Object ob) {
        if (StringUtils.isBlank(key)) {
            SaturnLoggerUtils.warn(logger, null, "【全局变量】全局变量添加失败，传入Key为空");
            return;
        }
        Object existOb = applicationContext.get(key);
        if (null != existOb) {
            SaturnLoggerUtils.warn(logger, null, "【全局变量设置失败】存放对象已存在", key, ob.toString());
            return;
        }
        SaturnLoggerUtils.info(logger, "【全局变量】添加全局变量", key, ob.toString());
        applicationContext.put(key, ob);
    }

    public synchronized static void updateApplicationContext(String key, Object ob) {
        if (StringUtils.isBlank(key)) {
            SaturnLoggerUtils.warn(logger, null, "【全局变量】全局变量更新失败，传入Key为空");
            return;
        }
        SaturnLoggerUtils.info(logger, "【全局变量】更新全局变量", key, ob.toString());
        applicationContext.remove(key);
        applicationContext.put(key, ob);
    }

    public synchronized static void removeApplicationContext(String key) {
        if (StringUtils.isBlank(key)) {
            SaturnLoggerUtils.warn(logger, null, "【全局变量】全局变量删除失败，传入Key为空");
            return;
        }
        SaturnLoggerUtils.info(logger, "【全库变量】删除全局变量", key);
        applicationContext.remove(key);
    }

    public static Object getApplicationContext(String key) {
        if (StringUtils.isBlank(key)) {
            SaturnLoggerUtils.warn(logger, null, "【全局变量】全局变量获取失败，传入Key为空");
            return null;
        }
        return applicationContext.get(key);
    }

    public static void destroy() {
        applicationContext.clear();
    }
}
