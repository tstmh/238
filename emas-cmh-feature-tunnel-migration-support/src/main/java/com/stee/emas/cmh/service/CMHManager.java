/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.service;

import java.util.List;

import com.stee.emas.common.dto.OpeFlagDto;
import com.stee.emas.common.dto.VmsPictogramConfigDto;
import com.stee.emas.common.entity.EquipConfig;
import com.stee.emas.common.entity.EquipStatus;
import com.stee.emas.common.entity.SystemParameter;

/**
 * 
 * @author Scindia
 * @since Dec 3, 2012
 * @version 1.0
 *
 */

public interface CMHManager {	

	int processAwCfelsPictogramSet(VmsPictogramConfigDto pVmsPictogramConfigDto);

	int savePictogram(VmsPictogramConfigDto pVmsPictogramConfigDto);

	int updatePictogram(VmsPictogramConfigDto pVmsPictogramConfigDto);
	
	int downloadPictogramToOtherEquipment(VmsPictogramConfigDto pVmsPictogramConfigDto);

	//void processPixelFailureBMPFileResp(PixelFailureBMPFileDto pPixelFailureBMPFileDto);

	void handleAWCfelsOpeFlag(OpeFlagDto pOpeFlagDto);
	
	SystemParameter findSystemParameterByName(String pName);
	
	String getNextEquipConfig(String pEquipId);
	
	String getPreviousEquipConfig(String pEquipId);
	
	void createAuditTrail(String pSender, String pAction, String pActionDetail);

	EquipConfig getEquipConfig(String pEquipId);
	
	boolean canProcessTrafficAlert(String pEquipId);

	List<?> getAdjacentDetectors(String pEquipId);

	EquipStatus findEquipStatusByEquipIdAndStatusCode(String pEquipId, 	String pStatusCode);

	String updateEquipStatus(EquipStatus pEquipStatus);
	
}

