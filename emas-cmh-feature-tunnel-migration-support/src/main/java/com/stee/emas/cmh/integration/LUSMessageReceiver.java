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

import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.CmdRespDto;
import com.stee.emas.common.dto.EquipStatusDtoList;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.tunnel.MeasureDetailDtoList;

/**
 * @author Grace
 * @since Jun 22, 2022
 */

public class LUSMessageReceiver implements MessageListener {
	
private static final Logger logger = LoggerFactory.getLogger(LUSMessageReceiver.class);
	
	@Autowired
	CMHMessageHandler cmhMessageHandler;
	@Autowired
	TunnelMessageHandler tunnelMessageHandler;
	
	@Override
	public void onMessage(Message pMessage) {
		if (logger.isInfoEnabled()) {
			logger.info("LUSMessageReceiver started receiving the message.....");
		}
		try {
			if (pMessage instanceof ObjectMessage) {
				ObjectMessage objectMessage = (ObjectMessage)pMessage;
				String lMsgId = objectMessage.getJMSCorrelationID();
				if (logger.isInfoEnabled()) {
					logger.info("lMsgId received :: " + lMsgId);
				}
				if (MessageConstants.TECH_ALARM_ID.equals(lMsgId)) {
					cmhMessageHandler.handleTechAlarmList((TechAlarmDtoList)objectMessage.getObject(), Constants.TUNNEL_SENDER, null);
				} else if (MessageConstants.CMD_RESP_ID.equals(lMsgId)) {
					cmhMessageHandler.handleLusCmdRespObj((CmdRespDto)objectMessage.getObject());
				} else if (MessageConstants.EQUIP_STATUS_ID.equals(lMsgId)) {
					tunnelMessageHandler.handleLUSStatus((EquipStatusDtoList)objectMessage.getObject());
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

