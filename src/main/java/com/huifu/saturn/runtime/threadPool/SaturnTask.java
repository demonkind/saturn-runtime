/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.threadPool;

import org.apache.log4j.Logger;

/**
 * <P>线程执行任务<P>
 * 
 * 需要继承该类，并且覆写execute()方法
 * 
 * @author zhanghaijie
 * @version $Id: SaturnTask.java, v 0.1 2012-8-21 下午5:22:14 zhanghaijie Exp $
 */
public abstract class SaturnTask implements Runnable {

    /**  */
    private static final Logger logger = Logger.getLogger(SaturnTask.class);

    /** 
     * @see java.lang.Runnable#run()
     */
    public final void run() {
        try {
            execute();
        } catch (Exception e) {
            logger.error("【任务处理失败】", e);
        }
    }

    /**
     * 业务执行方法覆写execute()
     */
    public abstract void execute();

}
