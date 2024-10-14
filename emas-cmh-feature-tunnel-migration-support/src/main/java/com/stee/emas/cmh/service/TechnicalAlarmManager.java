/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.service;

import java.util.Date;
import java.util.List;

import com.stee.emas.common.dto.TechAlarmAckDto;
import com.stee.emas.common.entity.TechnicalAlarm;

/***** Changed by Grace 18/12/19 *****/
import com.stee.emas.common.dto.TechnicalAlarmDto;
/*************************************/

/**
 * 
 * @author Scindia
 * @since Sep 24, 2012
 * @version 1.0
 *
 */

public interface TechnicalAlarmManager {

	String saveTechnicalAlarm(TechnicalAlarm pTechnicalAlarm);	

	String updateTechnicalAlarm(TechnicalAlarm pTechnicalAlarm);

	String deleteTechnicalAlarm(TechnicalAlarm pTechnicalAlarm);

	boolean processTechAlarmClear(TechnicalAlarm lTechnicalAlarm, String pClearBy) /*throws Exception*/;

	boolean processTechAlarmRaise(TechnicalAlarm lTechnicalAlarm) throws Exception ;

	boolean processTechAlarmAck(TechAlarmAckDto lTechAlarmAckDto, String pSource);

	List<TechnicalAlarm> getTechnicalAlarmByEquipTypeAndAlarmCode(String pEquipType, int pAlarmCode);

	List<TechnicalAlarm> getTechAlarmByEquipTypeAndNotInAlarmCode(String pEquipType, int pAlarmCode);

	List<TechnicalAlarm> getAllTechnicalAlarm();

	TechnicalAlarm findTechnicalAlarmById(String pAlarmId);

	boolean processTechAlarmClearMonitor(TechnicalAlarm pTechnicalAlarm, Date pEndDate);

	List<TechnicalAlarm> getTechnicalAlarmByEquipId(String pEquipId);

	void saveHistTechnialAlarm(TechnicalAlarm lTechnicalAlarm, String pClearBy);

	TechnicalAlarm getTechnicalAlarmByEquipIdAndAlarmCode(String equipId, int i);

	/***** Changed by Grace 18/12/19 *****/
	List<TechnicalAlarm> getAllTPNTechnicalAlarm();

	boolean processTechAlarmClearedByTPND(TechnicalAlarm lTechnicalAlarm, String pClearBy);

	boolean processTPNDTechAlarmClear(TechnicalAlarmDto tpndDto, String pClearBy);
	/*************************************/
}

