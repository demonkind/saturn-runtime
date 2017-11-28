/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.heartBeat;

import org.apache.log4j.Logger;

import com.huifu.saturn.common.utils.SaturnLoggerUtils;
import com.huifu.saturn.runtime.threadPool.SaturnTask;
import com.huifu.saturn.runtime.threadPool.SaturnTaskExecutor;

/**
 * 
 * @author zhanghaijie
 * @version $Id: SaturnHeartBeatExecutor.java, v 0.1 2012-9-4 下午04:35:09 zhanghaijie Exp $
 */
public class SaturnHeartBeatExecutor {

    private Logger             logger = Logger.getLogger(SaturnHeartBeatExecutor.class);

    private SaturnTaskExecutor saturnTaskExecutor;

    private SaturnTask         saturnHeartBeatTask;

    public void init() {
        SaturnLoggerUtils.info(logger, "【系统初始化】建立心跳任务");
        saturnTaskExecutor.addTask(saturnHeartBeatTask);
    }

    public void setSaturnTaskExecutor(SaturnTaskExecutor saturnTaskExecutor) {
        this.saturnTaskExecutor = saturnTaskExecutor;
    }

    public void setSaturnHeartBeatTask(SaturnTask saturnHeartBeatTask) {
        this.saturnHeartBeatTask = saturnHeartBeatTask;
    }

}
