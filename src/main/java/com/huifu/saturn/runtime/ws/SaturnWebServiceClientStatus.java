/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.ws;

import java.util.Date;

/**
 * 客户端状态
 * 
 * @author zhanghaijie
 * @version $Id: SaturnWebServiceClientStatus.java, v 0.1 2012-8-22 下午2:13:41 zhanghaijie Exp $
 */
public class SaturnWebServiceClientStatus {

    /** 客户端状态 */
    private String             status;

    /** 失效时间 */
    private Date               inavaliableTime;

    public static final String AVAILABLE   = "AVAILABLE";

    public static final String INAVAILABLE = "INAVAILABLE";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getInavaliableTime() {
        return inavaliableTime;
    }

    public void setInavaliableTime(Date inavaliableTime) {
        this.inavaliableTime = inavaliableTime;
    }

}
