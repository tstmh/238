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

import com.stee.emas.cmh.common.CMHKeyBean;
import com.stee.emas.cmh.dto.TunnelRemoteControlDto;
import com.stee.emas.cmh.dto.lus.LusRemoteControlDto;
import com.stee.emas.cmh.dto.lus.LusSetupDimmingDto;
import com.stee.emas.cmh.dto.lus.LusSetupFlashRateDto;
import com.stee.emas.cmh.dto.wmcs.WmcsRemoteControlDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.stee.emas.cmh.integration.CMHMessageHandler;
import com.stee.emas.cmh.integration.Group1FELSMessageHandler;
import com.stee.emas.cmh.service.CMHManager;
import com.stee.emas.cmh.ws.CTETunWebService;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.constants.NameSpaceURI;
import com.stee.emas.ctetun.dto.LusMsgDto;
import com.stee.emas.ctetun.dto.PmcsMsgDto;

/**
 * 
 * @author Scindia
 * @since Mar 11, 2016
 * @version 1.0
 *  
 */

@WebService(serviceName = "CMHTunWebService", targetNamespace = NameSpaceURI.CMH)
public class CTETunWebServiceImpl extends SpringBeanAutowiringSupport implements CTETunWebService {
	
	private static final Logger logger = LoggerFactory.getLogger(CTETunWebServiceImpl.class);
	
	private static final Logger rtLogger = LoggerFactory.getLogger("responsetime");
	
	@Autowired
	Group1FELSMessageHandler group1FELSMessageHandler;
	@Autowired
	CMHMessageHandler cmhMessageHandler;
	@Autowired
	CMHManager cmhManager;
	
	@Override
	@WebMethod
	@WebResult(name = "result")
	public int AW_CFELS_LUS(@WebParam(name = "lusMsg", targetNamespace = NameSpaceURI.CMH) LusMsgDto pLusMsgDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_LUS.....EquipId ::" + pLusMsgDto.getEquipId());
		}
		int lResult = group1FELSMessageHandler.handleLusMsg(pLusMsgDto);
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_LUS.....");
		}
		return lResult;
	}
	
	@Override
	@WebMethod
	@WebResult(name = "result")
	public int AW_CFELS_PMCS(@WebParam(name = "pmcsMsg", targetNamespace = NameSpaceURI.CMH) PmcsMsgDto pPmcsMsgDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside WS -----------> AW_CFELS_PMCS.....EquipId ::" + pPmcsMsgDto.getEquipId() + "...AttrCode ->" + pPmcsMsgDto.getAttrName());
		}
		int lResult = group1FELSMessageHandler.handlePmcsMsg(pPmcsMsgDto);
		if (logger.isInfoEnabled()) {
			logger.info("Exit WS ----------> AW_CFELS_PMCS.....");
		}
		return lResult;
	}
	
	@Override
	@WebMethod
	@WebResult(name = "result")
	public int TEST_LUS_CMD(@WebParam(name = "felsCode", targetNamespace = NameSpaceURI.CMH) String pFelsCode, 
							@WebParam(name = "equipId", targetNamespace = NameSpaceURI.CMH) String pEquipId,
							@WebParam(name = "attrName", targetNamespace = NameSpaceURI.CMH) String pAttrName,
							@WebParam(name = "attrValue", targetNamespace = NameSpaceURI.CMH) String pAttrValue) {

		//[|allus_t001|cdd|0|]
		
		LusMsgDto lLusMsgDto = new LusMsgDto();
		lLusMsgDto.setFelsCode(pFelsCode);
		lLusMsgDto.setEquipId(pEquipId);
		lLusMsgDto.setAttrName(pAttrName);
		lLusMsgDto.setAttrValue(pAttrValue);
		
		int lResult = AW_CFELS_LUS(lLusMsgDto);
		
		return lResult;
	}
	
	@Override
	@WebMethod
	@WebResult(name = "result")
	public int TEST_PMCS_CMD(@WebParam(name = "felsCode", targetNamespace = NameSpaceURI.CMH) String pFelsCode,
							@WebParam(name = "equipType", targetNamespace = NameSpaceURI.CMH) String pEquipType,
							@WebParam(name = "equipId", targetNamespace = NameSpaceURI.CMH) String pEquipId,
							@WebParam(name = "attrName", targetNamespace = NameSpaceURI.CMH) String pAttrName,
							@WebParam(name = "attrValue", targetNamespace = NameSpaceURI.CMH) String pAttrValue) {

		//[|allus_t001|cdd|0|]
		
		PmcsMsgDto lPmcsMsgDto = new PmcsMsgDto();
		lPmcsMsgDto.setFelsCode(pFelsCode);
		lPmcsMsgDto.setEquipmentCode(pEquipType);
		lPmcsMsgDto.setEquipId(pEquipId);
		lPmcsMsgDto.setAttrName(pAttrName);
		lPmcsMsgDto.setAttrValue(pAttrValue);
		
		int lResult = AW_CFELS_PMCS(lPmcsMsgDto);
		
		return lResult;
	}

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int waterMistControl(@WebParam(name = "wmcsMsg", targetNamespace = NameSpaceURI.CMH)
								WmcsRemoteControlDto remoteControlDto) {
		
		logger.info("web service: waterMistControl receive " + remoteControlDto);
		saveAuditTrail(remoteControlDto, "WMCS Remote");
		
		return group1FELSMessageHandler.handleWmcsRemoteControl(remoteControlDto);
	}

	// lus
	@Override
	@WebMethod
	@WebResult(name = "result")
	public int lusRemoteControl(
			@WebParam(name = "lusMsg", targetNamespace = NameSpaceURI.CMH) LusRemoteControlDto remoteControlDto) {

		logger.info("web service: lusRemoteControl receive " + remoteControlDto);
		saveAuditTrail(remoteControlDto, "LUS Remote");
		return group1FELSMessageHandler.handleLusRemoteControl(remoteControlDto);
	}

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int lusSetupDimming(
			@WebParam(name = "lusMsg", targetNamespace = NameSpaceURI.CMH) LusSetupDimmingDto dto) {

		logger.info("web service: LusSetupDimmingDto receive " + dto);
		saveAuditTrail(dto, "LUS Remote");

		return group1FELSMessageHandler.handleLusSetupDimming(dto);
	}

	@Override
	@WebMethod
	@WebResult(name = "result")
	public int lusSetupFlashRate(
			@WebParam(name = "lusMsg", targetNamespace = NameSpaceURI.CMH) LusSetupFlashRateDto dto) {

		logger.info("web service: lusSetupFlashRate receive " + dto);
		saveAuditTrail(dto, "LUS Remote");

		return group1FELSMessageHandler.handleLusSetupFlashRate(dto);
	}

	private void saveAuditTrail(TunnelRemoteControlDto dto, String action) {
		String lExecId = dto.getExecId();
		String lCmdId = dto.getCmdId();
		String lSender = dto.getSender();

		CMHKeyBean cmhKeyBean = new CMHKeyBean(lExecId, lCmdId);
		cmhMessageHandler.getCMHHashTable().put(cmhKeyBean, lSender);

		rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_LUS + "," + Constants.LOG_SEND + ","
				+ dto.getExecId() + "," + dto.getCmdId() + "," + dto.getEquipId()
				+ "," + Constants.LOG_TCTRL);

		String lActionDetail = "Remote Control send to the Equipment " + dto.getEquipId() + "AttrCode :: "
				+ dto.getAttributeName() + "AttrValue :: " + dto.getCmdValue()
				+ " with ExecId :: " + dto.getExecId() + " CmdId :: " + dto.getCmdId();
		cmhManager.createAuditTrail(lSender, action, lActionDetail);
	}

}