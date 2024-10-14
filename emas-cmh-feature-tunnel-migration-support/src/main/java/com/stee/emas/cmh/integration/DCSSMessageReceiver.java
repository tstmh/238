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

import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.ImageSequenceDto;
import com.stee.emas.common.dto.PreviewImageDto;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TrafficAlertClearDtoList;
import com.stee.emas.common.dto.TrafficAlertDto;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : DCSS Message Receiver</p>
 * <p>This class is used by CMH to receive object from DCSS_CMH_Q queue
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Nov 29, 2012
 * @version $LastChangedRevision: 920 $
 * $LastChangedDate: 2013-05-14 11:38:59 +0800 (Tue, 14 May 2013) $
 * $LastChangedBy: scindia $
 * 
 */

public class DCSSMessageReceiver implements MessageListener {

private static Logger logger = LoggerFactory.getLogger(DCSSMessageReceiver.class);
	
	@Autowired
	CMHMessageHandler cmhMessageHandler;
	
	@Override
	public void onMessage(Message pMessage) {
		if (logger.isInfoEnabled()) {
			logger.info("DCSSJmsListener started receiving the message.....");
		}
		try {
			if (pMessage instanceof ObjectMessage) {
				ObjectMessage objectMessage = (ObjectMessage)pMessage;
				String lMsgId = objectMessage.getJMSCorrelationID();
				if (logger.isInfoEnabled()) {
					logger.info("lMsgId received :: " + lMsgId);
				}
				if (MessageConstants.TECH_ALARM_ID.equals(lMsgId)) {
					cmhMessageHandler.handleTechAlarmList((TechAlarmDtoList)objectMessage.getObject(), new String(), null);
				} else if (MessageConstants.TRAFFIC_ALERT_ID.equals(lMsgId)) {
					cmhMessageHandler.handleTrafficAlertObj((TrafficAlertDto)objectMessage.getObject());
				} else if (MessageConstants.PREVIEW_IMAGE_ID.equals(lMsgId)) {
					cmhMessageHandler.handlePreviewImageObj((PreviewImageDto)objectMessage.getObject());
	        	} else if (MessageConstants.IMAGE_SEQUENCE_ID.equals(lMsgId)) {
					cmhMessageHandler.handleImageSequenceObj((ImageSequenceDto)objectMessage.getObject());
	        	} else if (MessageConstants.TRAFFIC_ALERT_CLEAR_ID.equals(lMsgId)) {
	        		cmhMessageHandler.handleTrafficAlertClearList((TrafficAlertClearDtoList)objectMessage.getObject());
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