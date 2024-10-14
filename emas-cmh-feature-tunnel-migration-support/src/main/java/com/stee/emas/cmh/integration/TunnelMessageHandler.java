/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.EquipStatusDtoList;
import com.stee.emas.common.tunnel.MeasureDetailDtoList;

/**
 * @author Grace
 * @since Jun 22, 2022
 */

@Component("tunnelMessageHandler")
public class TunnelMessageHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(TunnelMessageHandler.class);
	
	@Autowired
	CMHMessageSender cmhMessageSender;

	public void handleWMSSStatus(EquipStatusDtoList pEquipStatusDtoList) {
		logger.info("Calling handleWMSSStatus .....");
		
		logger.info("Object sent to the AW queue EquipStatusDtoList");
		cmhMessageSender.sendAWJmsMessage("CteTunStatus", MessageConstants.CTETUN_EQUIP_STATUS_ID);
		logger.info("Object sent to the EMASITPT queue EquipStatusDtoList");
		cmhMessageSender.sendEmasItptJmsMessage(pEquipStatusDtoList, MessageConstants.TUNNEL_STATUS);		
	}

	public void handleWMCSStatus(EquipStatusDtoList pEquipStatusDtoList) {
		logger.info("Calling handleWMCSStatus .....");
		
		logger.info("Object sent to the AW queue EquipStatusDtoList");
		cmhMessageSender.sendAWJmsMessage("CteTunStatus", MessageConstants.CTETUN_EQUIP_STATUS_ID);
		logger.info("Object sent to the EMASITPT queue EquipStatusDtoList");
		cmhMessageSender.sendEmasItptJmsMessage(pEquipStatusDtoList, MessageConstants.TUNNEL_STATUS);		
	}

	public void handleWMCSMeasure(MeasureDetailDtoList pMeasureDetailDtoList) {
		logger.info("Calling handleWMCSStatus .....");
		
		logger.info("Object sent to the AW queue MeasureDetailDtoList");
		cmhMessageSender.sendAWJmsMessage("CteTunMeasure", MessageConstants.TUNNEL_STATUS);
		logger.info("Object sent to the EMASITPT queue MeasureDetailDtoList");
		cmhMessageSender.sendEmasItptJmsMessage(pMeasureDetailDtoList, MessageConstants.TUNNEL_STATUS);
	}
	
	public void handleLUSStatus(EquipStatusDtoList pEquipStatusDtoList) {
		logger.info("Calling handleLUSStatus .....");
		
		logger.info("Object sent to the AW queue EquipStatusDtoList");
		cmhMessageSender.sendAWJmsMessage("CteTunStatus", MessageConstants.CTETUN_EQUIP_STATUS_ID);
		logger.info("Object sent to the EMASITPT queue EquipStatusDtoList");
		cmhMessageSender.sendEmasItptJmsMessage(pEquipStatusDtoList, MessageConstants.TUNNEL_STATUS);		
	}
}