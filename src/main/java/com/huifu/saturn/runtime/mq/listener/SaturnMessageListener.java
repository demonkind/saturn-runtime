/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.mq.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class SaturnMessageListener implements MessageListener {
    private final Log log = LogFactory.getLog(SaturnMessageListener.class);

    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            try {
                Object o = ((ObjectMessage) message).getObject();
                handleMessage(o);
            } catch (JMSException ex) {
                log.info("收消息异常：" + ex.getMessage());
                throw new RuntimeException(ex);
            }
        } else if (message instanceof TextMessage) {

            try {
                Object o = ((TextMessage) message).getText();
                handleMessage(o);
            } catch (JMSException ex) {
                log.info("收消息异常：" + ex.getMessage());
                throw new RuntimeException(ex);
            }

        } else {
            log.info("收消息异常：Message must be of type TextMessage");
            throw new IllegalArgumentException("Message must be of type TextMessage");
        }
    }

    protected abstract void handleMessage(Object o);
}