/**
 *汇付天下有限公司
 * Copyright (c) 2006-2012 ChinaPnR,Inc.All Rights Reserved.
 */
package com.huifu.saturn.runtime.mq.sender.impl;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang.StringUtils;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.support.JmsGatewaySupport;

import com.huifu.saturn.runtime.mq.sender.MessageSender;

public class MessageSenderImpl extends JmsGatewaySupport implements MessageSender {

    private static final String PUB_SUB_DOMAIN_CONST_PRIFIX = "TP_PUB_SUB";

    public void send(final String destination, final Serializable msg) {
        if (StringUtils.isEmpty(destination) || msg == null) {
            throw new RuntimeException("消息参数异常");
        }
        if (destination.startsWith(PUB_SUB_DOMAIN_CONST_PRIFIX)) {
            this.getJmsTemplate().setPubSubDomain(true);
        } else {
            this.getJmsTemplate().setPubSubDomain(false);
        }
        this.getJmsTemplate().send(destination, new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {
                Message message = session.createObjectMessage(msg);
                return message;
            }
        });
    }

    public void send(final String destination, final String msg) {
        if (StringUtils.isEmpty(destination) || msg == null) {
            throw new RuntimeException("消息参数异常");
        }
        if (destination.startsWith(PUB_SUB_DOMAIN_CONST_PRIFIX)) {
            this.getJmsTemplate().setPubSubDomain(true);
        } else {
            this.getJmsTemplate().setPubSubDomain(false);
        }
        this.getJmsTemplate().send(destination, new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {
                Message message = session.createTextMessage(msg);
                return message;
            }
        });
    }

}
