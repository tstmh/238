/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.ws.impl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.stee.emas.cmh.integration.CMHMessageHandler;
import com.stee.emas.cmh.service.CMHManager;
import com.stee.emas.cmh.ws.CMHWebService;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.constants.NameSpaceURI;
import com.stee.emas.common.dto.DimmingDto;
import com.stee.emas.common.dto.FanOpeModeDto;
import com.stee.emas.common.dto.FlashingTimeDto;
import com.stee.emas.common.dto.OpeFlagDto;
import com.stee.emas.common.dto.ResetDto;
import com.stee.emas.common.dto.TechAlarmAckDtoList;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TrafficAlertAckDtoList;
import com.stee.emas.common.dto.VmsMsgDto;
import com.stee.emas.common.dto.VmsPictogramConfigDto;
import com.stee.emas.common.dto.VmsTimetableConfigDto;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : WebService for AW </p>
 * <p>This class is used by AW to send message to other interfaces
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Dec 3, 2012
 * @version 1.0
 *
 */

@WebService(serviceName = "CMHWebService", targetNamespace = NameSpaceURI.CMH)
public class CMHWebServiceImpl extends SpringBeanAutowiringSupport implements CMHWebService {
	
	private static Logger logger = LoggerFactory.getLogger(CMHWebServiceImpl.class);
	
	@Autowired
	CMHManager cmhManager;
	@Autowired
	CMHMessageHandler cmhMessageHandler;	

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int AW_CFELS_PictogramSet(@WebParam(name = "vmsPictogramConfig", targetNamespace = NameSpaceURI.CMH) VmsPictogramConfigDto pVmsPictogramConfigDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_PictogramSet.....PictogramId ::" + pVmsPictogramConfigDto.getPictogramId());
		}
		int lResult = cmhManager.processAwCfelsPictogramSet(pVmsPictogramConfigDto);	
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_PictogramSet.....");
		}
		return lResult;
	}
	

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int AW_CFELS_VMSTimetable(@WebParam(name = "vmsTimetableConfig", targetNamespace = NameSpaceURI.CMH) VmsTimetableConfigDto pVmsTimetableConfigDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_VMSTimetable.....EquipId ::" + pVmsTimetableConfigDto.getEquipId());
		}
		int lResult = cmhMessageHandler.handleTimetableObj(pVmsTimetableConfigDto);	
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_VMSTimetable.....");
		}
		return lResult;
	}

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int AW_CFELS_Dimming(@WebParam(name = "dimming", targetNamespace = NameSpaceURI.CMH) DimmingDto pDimmingDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_Dimming.....EquipId ::" + pDimmingDto.getEquipId());
		}
		int lResult = cmhMessageHandler.handleDimmingObj(pDimmingDto);
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_Dimming.....");
		}
		return lResult;
	}

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int AW_CFELS_VMS(@WebParam(name = "vmsMsg", targetNamespace = NameSpaceURI.CMH) VmsMsgDto pVmsMsgDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_VMS.....EquipId ::" + pVmsMsgDto.getEquipId());
		}
		int lResult = cmhMessageHandler.handleVmsObj(pVmsMsgDto);
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_VMS.....");
		}
		return lResult;
	}

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int AW_CFELS_Reset(@WebParam(name = "reset", targetNamespace = NameSpaceURI.CMH) ResetDto pResetDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_Reset.....ExecId ::" + pResetDto.getExecId() + ".....CmdId ::" + pResetDto.getCmdId()
					+".....Sender ::" + pResetDto.getSender() + ".....EquipId ::" + pResetDto.getEquipId());
		}
		int lResult = cmhMessageHandler.handleResetObj(pResetDto);	
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_Reset.....");
		}
		return lResult;
	}

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int AW_CFELS_FanOpeMode(@WebParam(name = "fanOpeMode", targetNamespace = NameSpaceURI.CMH) FanOpeModeDto pFanOpeModeDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_FanOpeMode.....ExecId ::" + pFanOpeModeDto.getExecId() + ".....CmdId ::" + pFanOpeModeDto.getCmdId() 
					+".....Sender ::" + pFanOpeModeDto.getSender() + ".....EquipId ::" + pFanOpeModeDto.getEquipId() + ".....opeMode ::" + pFanOpeModeDto.getOpeMode());
		}
		int lResult = cmhMessageHandler.handleFanOpeModeObj(pFanOpeModeDto);	
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_FanOpeMode.....");
		}
		return lResult;
	}	

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int AW_CFELS_TechAlarmAck(@WebParam(name = "techAlarmAckDtoList", targetNamespace = NameSpaceURI.CMH) TechAlarmAckDtoList pTechAlarmAckDtoList) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_TechAlarmAck.....TechAlarmAckDtoList size ::" + pTechAlarmAckDtoList.getDtoList().size());
		}
		int lResult = 0;
		try {
			lResult = cmhMessageHandler.handleTechAlarmAckList(pTechAlarmAckDtoList, Constants.AW_SENDER);			
		} catch (Exception e) {
			logger.error("Error in Technical Alarm Ack.....", e);
			lResult = 1;
		}			
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_TechAlarmAck.....");
		}
		return lResult;
	}

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int AW_CFELS_TrafficAlertAck(@WebParam(name = "trafficAlertAckList", targetNamespace = NameSpaceURI.CMH) TrafficAlertAckDtoList pTrafficAlertAckDtoList) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_TrafficAlertAck.....TrafficAlertAckDto size ::" + pTrafficAlertAckDtoList.getDtoList().size());
		}
		int lResult = 0;
		try {
			lResult = cmhMessageHandler.handleTrafficAlertAckList(pTrafficAlertAckDtoList);			
		} catch (Exception e) {
			logger.error("Error in Traffic Alert Ack.....", e);
			lResult = 1;
		}			
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_TrafficAlertAck.....");
		}
		return lResult;
	}

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int AW_CFELS_TechAlarmClear(@WebParam(name = "technicalAlarmList", targetNamespace = NameSpaceURI.CMH) TechAlarmDtoList pTechAlarmDtoList,
									   @WebParam(name = "clearBy", targetNamespace = NameSpaceURI.CMH) String pClearBy) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_TechAlarmClear.....TechnicalAlarmDtoList size ::" + pTechAlarmDtoList.getDtoList().size());
		}
		try {
			cmhMessageHandler.handleTechAlarmList(pTechAlarmDtoList, Constants.AW_SENDER, pClearBy);			
		} catch (Exception e) {
			logger.error("Error in Technical Alarm Clear.....", e);
			return 1;
		}			
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_TechAlarmClear.....");
		}
		return 0;
	}

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int AW_CFELS_PixelFailureBMPFile(@WebParam(name = "equipId", targetNamespace = NameSpaceURI.CMH) String pEquipId,
											@WebParam(name = "sender", targetNamespace = NameSpaceURI.CMH) String pSender) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_PixelFailureBMPFile.....EquipId ::" + pEquipId +".....Sender ::" + pSender);
		}
		try {
			cmhMessageHandler.handlePixelFailureBMPFile(pEquipId, pSender);
		} catch (Exception e) {
			logger.error("Error in PixelFailureBMPFile.....", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_PixelFailureBMPFile.....");
		}
		return 0;
	}


	@Override
	public int AW_CFELS_FlashingTime(FlashingTimeDto pFlashingTimeDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_FlashingTime.....ExecId ::" + pFlashingTimeDto.getExecId() + ".....CmdId ::" + pFlashingTimeDto.getCmdId()
					+".....Sender ::" + pFlashingTimeDto.getSender() + ".....EquipId ::" + pFlashingTimeDto.getEquipId() + ".....onTime :: " + pFlashingTimeDto.getOnTime() + ".....offTime :: " + pFlashingTimeDto.getOffTime());
		}
		try {
			cmhMessageHandler.handleFlashingTime(pFlashingTimeDto);
		} catch (Exception e) {
			logger.error("Error in FlashingTime.....", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS -----------> AW_CFELS_FlashingTime.....");
		}
		return 0;
	}


	@Override
	@WebMethod
	@WebResult(name="result")
	public int AW_CFELS_OPE_FLAG(@WebParam(name = "opeFlagDto", targetNamespace = NameSpaceURI.CMH) OpeFlagDto pOpeFlagDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_OPE_FLAG.....UserId ::" + pOpeFlagDto.getUserId() + ".....Flag ::" + pOpeFlagDto.getFlag());
		}
		try {
			cmhManager.handleAWCfelsOpeFlag(pOpeFlagDto);
		} catch (Exception e) {
			logger.error("Error in AwCfelsOpeFlag.....", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_OPE_FLAG.....");
		}
		return 0;
	}
	
	@Override
	@WebMethod
	@WebResult(name="result")
	public int AW_CFELS_UploadPictogram(@WebParam(name = "equipId", targetNamespace = NameSpaceURI.CMH) String pEquipId, 
										@WebParam(name = "pictogramId", targetNamespace = NameSpaceURI.CMH) String pPictogramId,
										@WebParam(name = "sender", targetNamespace = NameSpaceURI.CMH) String pSender) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_UploadPictogram.....equipId ::" + pEquipId + ".....pictogramId ::" + pPictogramId + ".....Sender :: " + pSender);
		}
		try {
			cmhMessageHandler.handleAWCfelsUploadPictogram(pEquipId, pPictogramId, pSender);
		} catch (Exception e) {
			logger.error("Error in AwCfelsUploadPictogram.....", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_UploadPictogram.....");
		}
		return 0;
	}
}