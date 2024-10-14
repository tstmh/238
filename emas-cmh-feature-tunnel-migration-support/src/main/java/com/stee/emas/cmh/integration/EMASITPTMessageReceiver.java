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

import com.stee.emas.cmh.integration.tunnel.Itpt2TunnelHandler;
import com.stee.emas.cmh.integration.tunnel.LusItpt2TunnelHandler;
import com.stee.emas.cmh.integration.tunnel.WmcsItpt2TunnelHandler;
import com.stee.emas.common.tunnel.ItptTunnelRemoteControlDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.DimmingDto;
import com.stee.emas.common.dto.TechAlarmAckDtoList;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TrafficAlertAckDtoList;
import com.stee.emas.common.dto.VmsMsgDto;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : EMASITPT Message Receiver</p>
 * <p>This class is used by CMH to receive object from EMASITPT_CMH_Q queue
 * @author Grace
 * @since Mar 04, 2022
 */

//Added 04/03/22 GH - To implement new queues to ITPT-INTF (Copied from EMASCCSMessageReceiver.java)
public class EMASITPTMessageReceiver implements MessageListener {
	
private static final Logger logger = LoggerFactory.getLogger(EMASITPTMessageReceiver.class);
	
	private final CMHMessageHandler cmhMessageHandler;
	private final LusItpt2TunnelHandler lusItpt2TunnelHandler;
	private final WmcsItpt2TunnelHandler wmcsItpt2TunnelHandler;

	@Autowired
	public EMASITPTMessageReceiver(CMHMessageHandler cmhMessageHandler, LusItpt2TunnelHandler lusItpt2TunnelHandler, WmcsItpt2TunnelHandler wmcsItpt2TunnelHandler) {
		this.cmhMessageHandler = cmhMessageHandler;
		this.lusItpt2TunnelHandler = lusItpt2TunnelHandler;
		this.wmcsItpt2TunnelHandler = wmcsItpt2TunnelHandler;
	}

	@Override
	public void onMessage(Message pMessage) {
		if (logger.isInfoEnabled()) {
			logger.info("EMASCCSJmsListener started receiving the message.....");
		}
		try {
			if (pMessage instanceof ObjectMessage) {
				ObjectMessage objectMessage = (ObjectMessage)pMessage;
				String lMsgId = objectMessage.getJMSCorrelationID();
				if (logger.isInfoEnabled()) {
					logger.info("lMsgId received :: " + lMsgId);
				}
				if (MessageConstants.DIMMING_ID.equals(lMsgId)) {
					cmhMessageHandler.handleDimmingObj((DimmingDto)objectMessage.getObject());
				} else if (MessageConstants.VMS_ID.equals(lMsgId)) {
					cmhMessageHandler.handleVmsObj((VmsMsgDto)objectMessage.getObject());
				} else if (MessageConstants.TECH_ALARM_ID.equals(lMsgId)) {
					cmhMessageHandler.handleTechAlarmList((TechAlarmDtoList)objectMessage.getObject(), Constants.EMASCCS_SENDER, null);
				} else if (MessageConstants.TECH_ALARM_ACK_ID.equals(lMsgId)) {
					cmhMessageHandler.handleTechAlarmAckList((TechAlarmAckDtoList)objectMessage.getObject(), Constants.EMASCCS_SENDER);
				} else if (MessageConstants.TRAFFIC_ALERT_ACK_ID.equals(lMsgId)) {
					cmhMessageHandler.handleTrafficAlertAckList((TrafficAlertAckDtoList)objectMessage.getObject());
				} else if (MessageConstants.TRAVEL_TIME_ID.equals(lMsgId)) {
					cmhMessageHandler.handleTravelTimePageObj((VmsMsgDto)objectMessage.getObject());
				} else if (MessageConstants.DEL_TRAVEL_TIME_PAGE_ID.equals(lMsgId)) {
					cmhMessageHandler.handleTravelTimePageDel((String)objectMessage.getObject());
				} else if (MessageConstants.ITPT_LUS.equals(lMsgId)) {
					// Add at 2024.08.19 Handle new Tunnel Remote from ITPT2
					lusItpt2TunnelHandler.handleRemoteControl((ItptTunnelRemoteControlDto) objectMessage.getObject());
				} else if (MessageConstants.ITPT_WMCS.equals(lMsgId)) {
					wmcsItpt2TunnelHandler.handleRemoteControl((ItptTunnelRemoteControlDto) objectMessage.getObject());
				} else if (MessageConstants.ITPT_FIRE.equals(lMsgId)) {
					logger.warn("FIRE hasn't migrated yet");
				} else if (MessageConstants.ITPT_NMS.equals(lMsgId)) {
					logger.warn("NMS hasn't migrated yet");
				} else if (MessageConstants.ITPT_PMCS.equals(lMsgId)) {
					logger.warn("PMCS hasn't migrated yet");
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