/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.integration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.stee.emas.ctetun.constants.MessageConstants;
import com.stee.emas.ctetun.dto.FelsApplicationStatusDto;
import com.stee.emas.ctetun.dto.PmcsStatusDto;

/**
 * 
 * @author Scindia
 * @since Sep 26, 2016
 * @version 1.0
 *
 */

public class PmcsMessageReceiver implements MessageListener {

private static Logger logger = LoggerFactory.getLogger("group1");
	
	@Autowired
	Group1FELSMessageHandler group1MessageHandler;

	@Override
	public void onMessage(Message pMessage) {
		if (logger.isInfoEnabled()) {
			logger.info("PmcsJmsListener started receiving the message.....");
		}
		try {
			if (pMessage instanceof ObjectMessage) {
				ObjectMessage objectMessage = (ObjectMessage)pMessage;
				String lMsgId = objectMessage.getJMSCorrelationID();
				if (logger.isInfoEnabled()) {
					logger.info("lMsgId received :: " + lMsgId);
				}
				if (MessageConstants.FELS_STATUS.equals(lMsgId)) {
					group1MessageHandler.handleFelsStatus((FelsApplicationStatusDto)objectMessage.getObject());				
				} else if (MessageConstants.PMCS_STATUS_ID.equals(lMsgId)) {
					group1MessageHandler.handlePmcsStatus((PmcsStatusDto)objectMessage.getObject());
				}
			}
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit onMessage ");
		}
	}

}