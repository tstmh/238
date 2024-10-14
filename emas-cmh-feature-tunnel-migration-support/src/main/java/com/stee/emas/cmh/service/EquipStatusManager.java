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

import com.stee.emas.common.dto.EquipStatusDto;
import com.stee.emas.common.dto.EquipStatusDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emas.common.entity.EquipStatus;

/**
 * 
 * @author Scindia
 * @since Oct 16, 2012
 * @version 1.0
 *
 */

public interface EquipStatusManager {

	String saveEquipStatus(EquipStatus pEquipStatus);

	String updateEquipStatus(EquipStatus pEquipStatus);

	String deleteEquipStatus(EquipStatus pEquipStatus);

	EquipStatus findEquipStatusById(String pId);

	EquipStatus findEquipStatusByEquipIdAndStatusCode(String pEquipId, String pStatusCode);

	List<EquipStatus> getAllEquipStatus();

	EquipStatusDto generateEquipStatusForClearStatus(TechnicalAlarmDto lTechAlarmDto) throws Exception;

	EquipStatusDto generateEquipStatusForAlarmRaised(TechnicalAlarmDto lTechAlarmDto) throws Exception;

	List<EquipStatusDto> processEquipStatus(EquipStatusDtoList pEquipStatusDtoList);

	/***** Changed by Grace 18/12/19 *****/
	EquipStatusDto generateTPNDEquipStatusForClearStatus(TechnicalAlarmDto lTechAlarmDto) throws Exception;

	EquipStatusDto generateTPNDEquipStatusForAlarmRaised(TechnicalAlarmDto lTechAlarmDto) throws Exception;
	/*************************************/
}

