/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.threadPool.impl;

import org.apache.log4j.Logger;

import com.huifu.saturn.runtime.threadPool.SaturnTask;
import com.huifu.saturn.runtime.threadPool.SaturnTaskExecutor;
import com.huifu.saturn.runtime.threadPool.executor.SanturnTaskPoolExecutor;

/**
 * <h>任务执行实现类</h>
 * 
 * @author zhanghaijie
 * @version $Id: SaturnTaskExecutorImpl.java, v 0.1 2012-8-21 下午5:29:26 zhanghaijie Exp $
 */
public class SaturnTaskExecutorImpl implements SaturnTaskExecutor {

    /** Logger */
    private static final Logger     logger = Logger.getLogger(SaturnTaskExecutorImpl.class);

    /**  */
    private SanturnTaskPoolExecutor saturnTaskPoolExecutor;

    /** 
     * @see com.chinapnr.common.manager.threadPool.SaturnTaskExecutor#execute(com.chinapnr.common.manager.threadPool.SaturnTask)
     */
    public void addTask(SaturnTask task) {
        try {
            saturnTaskPoolExecutor.execute(task);
        } catch (Exception e) {
            logger.error("【任务添加失败】:" + task.toString(), e);
        }
    }

    /**
     * 
     * @param saturnTaskPoolExecutor
     */
    public void setSaturnTaskPoolExecutor(SanturnTaskPoolExecutor saturnTaskPoolExecutor) {
        this.saturnTaskPoolExecutor = saturnTaskPoolExecutor;
    }

}
