/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.integration;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.stee.emas.cmh.dto.TunnelRemoteControlDto;
import com.stee.emas.cmh.dto.lus.LusSetupDimmingDto;
import com.stee.emas.cmh.dto.lus.LusSetupFlashRateDto;
import com.stee.emas.cmh.dto.wmcs.WmcsRemoteControlDto;
import com.stee.emas.common.tunnel.ItptTunnelRemoteControlDto;
import com.stee.emas.common.tunnel.TunnelRemoteDto;
import com.stee.emas.common.tunnel.lus.LusDimmingRemoteVO;
import com.stee.emas.common.tunnel.lus.LusFlashRateRemoteVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stee.emas.cmh.service.CMHManager;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.EquipStatusDto;
import com.stee.emas.common.entity.EquipStatus;
import com.stee.emas.ctetun.constants.CTETunConstants;
import com.stee.emas.ctetun.dto.FelsApplicationStatusDto;
import com.stee.emas.ctetun.dto.FireStatusDto;
import com.stee.emas.ctetun.dto.LusMsgDto;
import com.stee.emas.ctetun.dto.LusStatusDto;
import com.stee.emas.ctetun.dto.PmcsMsgDto;
import com.stee.emas.ctetun.dto.PmcsStatusDto;

/**
 * 
 * @author Scindia
 * @since Jan 25, 2016
 * @version 1.0
 *
 */

@Component("group1FELSMessageHandler")
public class Group1FELSMessageHandler {
	
	private static final Logger logger = LoggerFactory.getLogger("group1");
	
	@Autowired
	CMHMessageSender cmhMessageSender;
	@Autowired
	CMHManager cmhManager;

	public void handleLUSMWStatus(LusStatusDto pLUSStatusDto) { // ltc
		logger.info("Calling handleLUSMWStatus .....");
		logger.info("pLUSStatusDto.getEquipId() :: :: :: " + pLUSStatusDto.getEquipId());
		logger.info("pLUSStatusDto.getMwType() :: :: :: " + pLUSStatusDto.getMwType());
		logger.info("pLUSStatusDto.getStatus() :: :: :: " + pLUSStatusDto.getStatusValue());
		
		EquipStatusDto lEquipStatusDto = new EquipStatusDto(Constants.SYSTEM_ID, pLUSStatusDto.getDateTime(), pLUSStatusDto.getEquipType(), pLUSStatusDto.getEquipId(), pLUSStatusDto.getStatusCode(), pLUSStatusDto.getStatusValue());
		
		//TODO :: Scindia :: have to change to send map, if got performance issue
		/*Map lGrp1EquipStatusDto = 
		cmhMessageSender.sendAWJmsMessage(lGrp1EquipStatusDto, MessageConstants.GROUP1_EQUIP_STATUS_ID);*/
		
		logger.info("Object sent to the AW queue " + lEquipStatusDto);
		cmhMessageSender.sendAWJmsMessage("CteTunStatus", MessageConstants.CTETUN_EQUIP_STATUS_ID);
		cmhMessageSender.sendAWJmsMessage("TechnicalAlarm", MessageConstants.TECH_ALARM_ID);
	}

	public void handleLUSLinkStatus(LusStatusDto pLUSStatusDto) { //llk
		logger.info("Group1FELSMessageHandler -> Calling handleLUSLinkStatus -> Start");
		
		EquipStatusDto lEquipStatusDto = new EquipStatusDto(Constants.SYSTEM_ID, pLUSStatusDto.getDateTime(), pLUSStatusDto.getEquipType(), pLUSStatusDto.getEquipId(), pLUSStatusDto.getStatusCode(), pLUSStatusDto.getStatusValue());
		
		//TODO :: Scindia :: have to change to send map, if got performance issue
		/*Map lGrp1EquipStatusDto = 
		cmhMessageSender.sendAWJmsMessage(lGrp1EquipStatusDto, MessageConstants.GROUP1_EQUIP_STATUS_ID);*/
		
		logger.info("Object sent to the AW queue " + lEquipStatusDto);
		cmhMessageSender.sendAWJmsMessage("CteTunStatus", MessageConstants.CTETUN_EQUIP_STATUS_ID);
		cmhMessageSender.sendAWJmsMessage("TechnicalAlarm", MessageConstants.TECH_ALARM_ID);
	}
	
	public void handleLUSStatus(LusStatusDto pLUSStatusDto) { //lus
		logger.info("Calling handleLUSStatus .....");
		logger.info("pLUSStatusDto.getEquipId() :: :: :: " + pLUSStatusDto.getEquipId());
		logger.info("pLUSStatusDto.getStatus() :: :: :: " + pLUSStatusDto.getStatusValue());
		
		EquipStatusDto lEquipStatusDto = new EquipStatusDto(Constants.SYSTEM_ID, pLUSStatusDto.getDateTime(), pLUSStatusDto.getEquipType(), pLUSStatusDto.getEquipId(), pLUSStatusDto.getStatusCode(), pLUSStatusDto.getStatusValue());
		
		//TODO :: Scindia :: have to change to send map, if got performance issue
		/*Map lGrp1EquipStatusDto = 
		cmhMessageSender.sendAWJmsMessage(lGrp1EquipStatusDto, MessageConstants.GROUP1_EQUIP_STATUS_ID);*/
				
		logger.info("Object sent to the AW queue " + lEquipStatusDto);
		cmhMessageSender.sendAWJmsMessage("CteTunStatus", MessageConstants.CTETUN_EQUIP_STATUS_ID);
		cmhMessageSender.sendAWJmsMessage("TechnicalAlarm", MessageConstants.TECH_ALARM_ID);
	}

	public int handleLusMsg(LusMsgDto pLusMsgDto) {
		logger.info("Calling  handleLusMsg ---> " + pLusMsgDto);
		try {
			
			//[|allus_t001|cdd|0|]
			String lEquipId = pLusMsgDto.getFelsCode() + pLusMsgDto.getEquipmentCode() + "_" + pLusMsgDto.getEquipId();
			
			String lCommand = "|" + lEquipId + "|" + pLusMsgDto.getAttrName() + "|" + pLusMsgDto.getAttrValue() + "|";
			
			logger.info("LUS Command sent to the queue " + lCommand);
			
			Date today = new Date();
		    SimpleDateFormat _date_format = new SimpleDateFormat("yyyy-MM-dd" + " HH:mm:ss");
		    String datetime_format = _date_format.format(today);
			String lCorrelationId = datetime_format + ", aw," + pLusMsgDto.getFelsCode() + ",RC";
			
			cmhMessageSender.sendGroup1JmsMessage(lCommand, lCorrelationId);
			//rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_TCIA + "," + Constants.LOG_SEND + "," + pVmsTimetableConfigDto.getExecId() + "," + pVmsTimetableConfigDto.getCmdId() + "," + pVmsTimetableConfigDto.getEquipId() + "," + Constants.LOG_TTB);
		} catch (Exception e) {
			logger.error("Error in Sending LUS Command to LUS Queue ", e);
			return 1;
		}
		return 0;
	}

	public void handleFelsStatus(FelsApplicationStatusDto pFelsApplicationStatusDto) {
		try {
			String lEquipId = "";
			if (pFelsApplicationStatusDto.getFelsCode().equals(CTETunConstants.LUS_FELS_CODE)) {
				lEquipId = CTETunConstants.LUS_EQUIP_ID;
			} else if (pFelsApplicationStatusDto.getFelsCode().equals(CTETunConstants.FIRE_FELS_CODE)) {
				lEquipId = CTETunConstants.FIRE_EQUIP_ID;
			} else if (pFelsApplicationStatusDto.getFelsCode().equals(CTETunConstants.PMCS_FELS_CODE)) {
				lEquipId = CTETunConstants.PMCS_EQUIP_ID;
			}
			EquipStatus lEquipStatus = cmhManager.findEquipStatusByEquipIdAndStatusCode(lEquipId, Constants.OPE_STATE);
			lEquipStatus.setStatus(Constants.EQUIP_STATUS_NORMAL);
			lEquipStatus.setDateTime(new Date());
			cmhManager.updateEquipStatus(lEquipStatus);
			logger.info("Equip Status for " + pFelsApplicationStatusDto.getFelsCode() + " updated successfully ....." + new Date());
		} catch (Exception e) {
			logger.error("Exception in updating EquipStatus for " + pFelsApplicationStatusDto.getFelsCode() , e);
		}
	}

	public void handleFireStatus(FireStatusDto pFireStatusDto) {
		logger.info("Group1FELSMessageHandler -> Calling handleFireStatus -> Start");
		EquipStatusDto lEquipStatusDto = new EquipStatusDto(Constants.SYSTEM_ID, pFireStatusDto.getDateTime(), pFireStatusDto.getEquipType(), pFireStatusDto.getEquipId(), pFireStatusDto.getStatusCode(), pFireStatusDto.getStatusValue());

		logger.info("Object sent to AW queue " + lEquipStatusDto);
		cmhMessageSender.sendAWJmsMessage("CteTunStatus", MessageConstants.CTETUN_EQUIP_STATUS_ID);
		logger.info("Technical Alarm sent to AW queue ...");
		cmhMessageSender.sendAWJmsMessage("TechnicalAlarm", MessageConstants.TECH_ALARM_ID);
		
	}
	
	public void handlePmcsStatus(PmcsStatusDto pPmcsStatusDto) {
		logger.info("Group1FELSMessageHandler -> Calling handlePmcsStatus -> Start");
		EquipStatusDto lEquipStatusDto = new EquipStatusDto(Constants.SYSTEM_ID, pPmcsStatusDto.getDateTime(), pPmcsStatusDto.getEquipType(), pPmcsStatusDto.getEquipId(), pPmcsStatusDto.getStatusCode(), pPmcsStatusDto.getStatusValue());

		logger.info("Object sent to AW queue " + lEquipStatusDto);
		cmhMessageSender.sendAWJmsMessage("CteTunStatus", MessageConstants.CTETUN_EQUIP_STATUS_ID);
		logger.info("Technical Alarm sent to AW queue ...");
		cmhMessageSender.sendAWJmsMessage("TechnicalAlarm", MessageConstants.TECH_ALARM_ID);
		
	}

	public int handlePmcsMsg(PmcsMsgDto pPmcsMsgDto) {
		logger.info("Calling  handlePmcsMsg ---> " + pPmcsMsgDto);
		try {
			
			//[|axjfa_f101|cds|1|]
			String lEquipId = pPmcsMsgDto.getFelsCode() + pPmcsMsgDto.getEquipmentCode() + "_" + pPmcsMsgDto.getEquipId();
			
			String lCommand = "|" + lEquipId + "|" + pPmcsMsgDto.getAttrName() + "|" + pPmcsMsgDto.getAttrValue() + "|";
			
			logger.info("PMCS Command sent to the queue " + lCommand);
			
			Date today = new Date();
		    SimpleDateFormat _date_format = new SimpleDateFormat("yyyy-MM-dd" + " HH:mm:ss");
		    String datetime_format = _date_format.format(today);
			String lCorrelationId = datetime_format + ", aw," + pPmcsMsgDto.getFelsCode() + ",RC";
			cmhMessageSender.sendPmcsJmsMessage(lCommand, lCorrelationId);
		} catch (Exception e) {
			logger.error("Error in Sending PMCS Command to LUS Queue ", e);
			return 1;
		}
		return 0;
	}

	public int handleWmcsRemoteControl(WmcsRemoteControlDto wmcsRemoteControlDto) {
		logger.info("handle handleWmcsRemoteControl ... " + wmcsRemoteControlDto);
		try {
			cmhMessageSender.sendWmcsJmsMessage(createWmcsMessageDto(wmcsRemoteControlDto), MessageConstants.TUNNEL_CTRL_ID);
		} catch (Exception e) {
			logger.error("Error occurs on handleWmcsRemoteControl", e);
			return 1;
		}
		return 0;
	}

	public void handleWmcsItptRemoteControl(ItptTunnelRemoteControlDto dto) {
        logger.info("handle handleWmcsItptRemoteControl ---> {}", dto);
		WmcsRemoteControlDto wmcsRemoteControlDto = convertToWmcs(dto);
		handleWmcsRemoteControl(wmcsRemoteControlDto);
	}

	public int handleLusRemoteControl(TunnelRemoteControlDto tunnelRemoteDto) {
		logger.info("handle handleLusRemoteControl ... " + tunnelRemoteDto);
		try {
			cmhMessageSender.sendLusJmsMessage(createLusMessageDto(tunnelRemoteDto), MessageConstants.TUNNEL_CTRL_ID);
		} catch (Exception e) {
			logger.error("Error occurs on handleLusRemoteControl", e);
			return 1;
		}
		return 0;
	}

	public int handleLusSetupDimming(LusSetupDimmingDto dto) {
		logger.info("handle handleLusSetupDimming ... " + dto);
		try {
			LusDimmingRemoteVO lusDimmingRemoteVO = new LusDimmingRemoteVO();
			setupRemoteMessageDto(lusDimmingRemoteVO, dto, "al");
			lusDimmingRemoteVO.setDimmingMode(dto.getDimmingMode());
			cmhMessageSender.sendLusJmsMessage(lusDimmingRemoteVO, MessageConstants.LUS_SETUP_DIMMING);
		} catch (Exception e) {
			logger.error("Error occurs on handleLusRemoteControl", e);
			return 1;
		}
		return 0;
	}

	public int handleLusSetupFlashRate(LusSetupFlashRateDto dto) {
		logger.info("handle handleLusSetupDimming ... " + dto);
		try {
			LusFlashRateRemoteVO vo = new LusFlashRateRemoteVO();
			setupRemoteMessageDto(vo, dto, "al");
			vo.setFlashOn(dto.getFlashOn());
			vo.setFlashOff(dto.getFlashOff());
			cmhMessageSender.sendLusJmsMessage(vo, MessageConstants.LUS_SETUP_FLASH_RATE);
		} catch (Exception e) {
			logger.error("Error occurs on handleLusRemoteControl", e);
			return 1;
		}
		return 0;
	}

	public void handleLusItpt2RemoteControl(ItptTunnelRemoteControlDto itptTunnelRemoteControlDto) {
		String command = itptTunnelRemoteControlDto.getAttributeName();
		if ("cdd".equals(command)) {
			TunnelRemoteControlDto tunnelRemoteControlDto = convertToTunnelRemote(itptTunnelRemoteControlDto);
			handleLusRemoteControl(tunnelRemoteControlDto);
		} else if ("cdm".equals(command)) {
			LusSetupDimmingDto dto = new LusSetupDimmingDto();
			dto.setEquipId(itptTunnelRemoteControlDto.getEquipId());
			dto.setEquipType(itptTunnelRemoteControlDto.getEquipType());
			dto.setAttributeName(itptTunnelRemoteControlDto.getAttributeName());
			dto.setCmdValue(itptTunnelRemoteControlDto.getCmdValue());
			dto.setSender(itptTunnelRemoteControlDto.getSender());
			dto.setExecId(itptTunnelRemoteControlDto.getExecId());
			dto.setCmdId(itptTunnelRemoteControlDto.getCmdId());
			dto.setDimmingMode('A');
			handleLusSetupDimming(dto);
		}
	}

	public static TunnelRemoteControlDto convertToTunnelRemote(ItptTunnelRemoteControlDto itptTunnelRemoteControlDto) {
		TunnelRemoteControlDto tunnelDto = new TunnelRemoteControlDto();
		tunnelDto.setEquipId(itptTunnelRemoteControlDto.getEquipId());
		tunnelDto.setEquipType(itptTunnelRemoteControlDto.getEquipType());
		tunnelDto.setAttributeName(itptTunnelRemoteControlDto.getAttributeName());
		tunnelDto.setCmdValue(itptTunnelRemoteControlDto.getCmdValue());
		tunnelDto.setSender(itptTunnelRemoteControlDto.getSender());
		tunnelDto.setExecId(itptTunnelRemoteControlDto.getExecId());
		tunnelDto.setCmdId(itptTunnelRemoteControlDto.getCmdId());
		return tunnelDto;
	}

	public static WmcsRemoteControlDto convertToWmcs(ItptTunnelRemoteControlDto itptTunnelRemoteControlDto) {
		WmcsRemoteControlDto tunnelDto = new WmcsRemoteControlDto();
		tunnelDto.setEquipId(itptTunnelRemoteControlDto.getEquipId());
		tunnelDto.setEquipType(itptTunnelRemoteControlDto.getEquipType());
		tunnelDto.setAttributeName(itptTunnelRemoteControlDto.getAttributeName());
		tunnelDto.setCmdValue(itptTunnelRemoteControlDto.getCmdValue());
		tunnelDto.setSender(itptTunnelRemoteControlDto.getSender());
		tunnelDto.setExecId(itptTunnelRemoteControlDto.getExecId());
		tunnelDto.setCmdId(itptTunnelRemoteControlDto.getCmdId());
		return tunnelDto;
	}

	private TunnelRemoteDto createLusMessageDto(TunnelRemoteControlDto dto) {
		return createRemoteMessageDto(dto, "al");
	}

	private TunnelRemoteDto createWmcsMessageDto(TunnelRemoteControlDto dto) {
		return createRemoteMessageDto(dto, "wm");
	}

	private TunnelRemoteDto createRemoteMessageDto(TunnelRemoteControlDto dto, String felsCode) {
		TunnelRemoteDto tunnelRemoteDto = new TunnelRemoteDto();
		setupRemoteMessageDto(tunnelRemoteDto, dto, felsCode);
		return tunnelRemoteDto;
	}

	private void setupRemoteMessageDto(TunnelRemoteDto tunnelRemoteDto, TunnelRemoteControlDto dto, String felsCode) {
		tunnelRemoteDto.setFelsCode(felsCode);
		tunnelRemoteDto.setSender(dto.getSender());
		tunnelRemoteDto.setEquipId(dto.getEquipId());
		tunnelRemoteDto.setEquipType(dto.getEquipType());
		tunnelRemoteDto.setAttributeId(dto.getAttributeName());
		tunnelRemoteDto.setCmdValue(dto.getCmdValue());
		tunnelRemoteDto.setCmdId(dto.getCmdId());
		tunnelRemoteDto.setExecId(dto.getExecId());
		tunnelRemoteDto.setDateTime(new Date());
	}

}