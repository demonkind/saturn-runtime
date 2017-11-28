/**
 * 
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.exception;

/**
 * 
 * @author zhanghaijie
 * @version $Id: SaturnRemoteInvokeException.java, v 0.1 2012-9-20 下午03:34:55 zhanghaijie Exp $
 */
public class SaturnRemoteInvokeException extends RuntimeException {

    /**  */
    private static final long serialVersionUID = -1659343000539753642L;

    public SaturnRemoteInvokeException() {
        super();
    }

    public SaturnRemoteInvokeException(Throwable ex) {
        super(ex);
    }

    public SaturnRemoteInvokeException(String msg, Throwable ex) {
        super(msg, ex);
    }

}
