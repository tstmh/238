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
import com.stee.emas.common.dto.CmdRespDto;
import com.stee.emas.common.dto.EquipStatusDtoList;
import com.stee.emas.common.dto.PictogramDto;
import com.stee.emas.common.dto.PixelFailureBMPFileDto;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.VmsMsgDto;
import com.stee.emas.common.dto.VmsMsgDtoList;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : MFELS Message Receiver</p>
 * <p>This class is used by CMH to receive object from MFELS_CMH_Q queue
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Nov 29, 2012
 * @version $LastChangedRevision: 920 $
 * $LastChangedDate: 2013-05-14 11:38:59 +0800 (Tue, 14 May 2013) $
 * $LastChangedBy: scindia $
 * 
 */

public class MFELSMessageReceiver implements MessageListener {
	
	private static Logger logger = LoggerFactory.getLogger(MFELSMessageReceiver.class);
	
	@Autowired
	CMHMessageHandler cmhMessageHandler;

	@Override
	public void onMessage(Message pMessage) {
		if (logger.isInfoEnabled()) {
	    	logger.info("MFELSJmsListener started receiving the message.....");
		}
		try {
			if (pMessage instanceof ObjectMessage) {
				ObjectMessage objectMessage = (ObjectMessage)pMessage;
				String lMsgId = objectMessage.getJMSCorrelationID();
				if (logger.isInfoEnabled()) {
					logger.info("lMsgId received :: " + lMsgId);
				}
				if (MessageConstants.CMD_RESP_ID.equals(lMsgId)) {
					cmhMessageHandler.handleCmdRespObj((CmdRespDto)objectMessage.getObject());
				} else if (MessageConstants.EQUIP_STATUS_ID.equals(lMsgId)) {
					cmhMessageHandler.handleEquipStatusList((EquipStatusDtoList)objectMessage.getObject());
				} else if (MessageConstants.VMS_ID.equals(lMsgId)) {
					cmhMessageHandler.handleVmsObjResp((VmsMsgDto)objectMessage.getObject());
				} else if (MessageConstants.PIXEL_FAIL_BMP_FILE_RESP_ID.equals(lMsgId)) {
					cmhMessageHandler.handlePixelFailureBMPFileResp((PixelFailureBMPFileDto)objectMessage.getObject());
				} else if (MessageConstants.TECH_ALARM_ID.equals(lMsgId)) {
					cmhMessageHandler.handleTechAlarmList((TechAlarmDtoList)objectMessage.getObject(), new String(), null);
				} else if (MessageConstants.UPLOAD_PICTOGRAM_RESPONSE.endsWith(lMsgId)) {
					cmhMessageHandler.handleUploadPictogramResp((PictogramDto)objectMessage.getObject());
				}  else if (MessageConstants.VMS_LIST_ID.equals(lMsgId)) {
					cmhMessageHandler.handleVmsList((VmsMsgDtoList)objectMessage.getObject());
				}
			}
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
		}/* catch (Exception e) {
			logger.error(e.getMessage(), e);
		}*/
		if (logger.isDebugEnabled()) {
			logger.debug("Exit onMessage ");
		}
	}
}