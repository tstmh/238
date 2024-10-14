/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.monitor;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stee.emas.cmh.service.TechnicalAlarmManager;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.dao.ConfigDao;
import com.stee.emas.common.dao.EquipStatusDao;
import com.stee.emas.common.dao.TechnicalAlarmDao;
import com.stee.emas.common.entity.EquipStatus;
import com.stee.emas.common.entity.SystemParameter;
import com.stee.emas.common.entity.TechnicalAlarm;
import com.stee.emas.common.entity.UpsArgentStatus;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Manager class for Scheduler</p>
 * <p>This class is used by Scheduler as a manager class 
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Apr 30, 2013
 * @version 1.0
 *
 */
@Service
@Transactional
public class CmhMonitor {	

	private static Logger logger = LoggerFactory.getLogger(CmhMonitor.class);
	
	@Autowired
	EquipStatusDao equipStatusDao;
	@Autowired
	TechnicalAlarmDao technicalAlarmDao;
	@Autowired
	TechnicalAlarmManager technicalAlarmManager;
	@Autowired
	ConfigDao configDao;
	
	public List<EquipStatus> getAllEquipStatusForNms() {
		List<EquipStatus> lEquipStatusList = equipStatusDao.getAllEquipStatusForNms();
		return lEquipStatusList;
	}
	
	public EquipStatus findEquipStatusByEquipIdAndStatusCode(String pEquipId, String pStatusCode) {		
		if (pEquipId == null || pEquipId.trim().length() == 0 || pStatusCode == null || pStatusCode.trim().length() == 0) {
			return null;
		}
		EquipStatus lEquipStatus = equipStatusDao.findEquipStatusByEquipIdAndStatusCode(pEquipId, pStatusCode);		
		return lEquipStatus;
	}
	
	public String updateEquipStatus(EquipStatus pEquipStatus) {
		if (pEquipStatus == null) {
			return null;
		}
		String lId = equipStatusDao.updateEquipStatus(pEquipStatus);
		return lId;
	}
	
	public SystemParameter findSystemParameterByName(String pName) {
		if (pName == null || pName.trim().length() == 0) {
			return null;
		}
		SystemParameter lSystemParameter = configDao.findSystemParameterByName(pName);
		return lSystemParameter;
	}
	
	public TechnicalAlarm findTechnicalAlarmById(String pAlarmId) {
		TechnicalAlarm lTechnicalAlarm = technicalAlarmDao.findTechnicalAlarmById(pAlarmId);
		return lTechnicalAlarm;
	}

	public void processTechAlarmRaise(TechnicalAlarm lTechnicalAlarm) throws Exception {
		technicalAlarmManager.processTechAlarmRaise(lTechnicalAlarm);
	}

	public void processTechAlarmClearMonitor(TechnicalAlarm pTechnicalAlarm, Date pEndDate) {
		technicalAlarmManager.processTechAlarmClearMonitor(pTechnicalAlarm, pEndDate);
	}

	public List<String> getAllNetworkEquipments() {
		List<String> lEquipIdList = configDao.getAllNetworkEquipments();
		return lEquipIdList;
	}

	public List<EquipStatus> getStatusForNetworkEquipments(List<String> pEquipIdList) {
		Calendar lStartCalendar = Calendar.getInstance();
		lStartCalendar.add(Calendar.MINUTE, -5);
		Calendar lEndCalendar = Calendar.getInstance();
		logger.info("Inside Monitor :: getStatusForNetworkEquipments -> StartDate....." + lStartCalendar.getTime() + " :: EndDate....." + lEndCalendar.getTime());
		List<EquipStatus> lEquipStatusList = equipStatusDao.getStatusForNetworkEquipments(pEquipIdList, lStartCalendar.getTime(), lEndCalendar.getTime());
		return lEquipStatusList;
	}
	
	public List<String> getUpsEquipments() {
		List<String> lEquipIdList = configDao.getUpsEquipments();
		return lEquipIdList;
	}
	
	public List<UpsArgentStatus> getStatusForUps() {
		/*Calendar lStartCalendar = Calendar.getInstance();
		lStartCalendar.add(Calendar.MINUTE, -5);
		Calendar lEndCalendar = Calendar.getInstance();
		logger.info("Inside Monitor :: getStatusForUps -> StartDate....." + lStartCalendar.getTime() + " :: EndDate....." + lEndCalendar.getTime());
		List<UpsArgentStatus> lUpsArgentStatusList = equipStatusDao.getStatusForUps(lStartCalendar.getTime(), lEndCalendar.getTime());*/
		List<UpsArgentStatus> lUpsArgentStatusList = equipStatusDao.getStatusForUps();
		/*for (UpsArgentStatus lUpsArgentStatus : lUpsArgentStatusList) {
			int lStatus = checkUPSStatus(lUpsArgentStatus.getDateTime());
			lUpsArgentStatus.setStatus(lStatus);
		}*/
		return lUpsArgentStatusList; 
	}
	
	public List<UpsArgentStatus> getStatusForOtherMultipleAlarms() {
		Calendar lStartCalendar = Calendar.getInstance();
		lStartCalendar.add(Calendar.MINUTE, -5);
		Calendar lEndCalendar = Calendar.getInstance();
		logger.info("Inside Monitor :: getStatusForUps -> StartDate....." + lStartCalendar.getTime() + " :: EndDate....." + lEndCalendar.getTime());
		List<UpsArgentStatus> lUpsArgentStatusList = equipStatusDao.getStatusForOtherMultipleAlarms(lStartCalendar.getTime(), lEndCalendar.getTime());		
		return lUpsArgentStatusList; 
	}
	
	public List<UpsArgentStatus> getStatusForOtherMultipleAlarmsForInit() {		
		List<UpsArgentStatus> lUpsArgentStatusList = equipStatusDao.getStatusFromUpsArgentStatus();		
		return lUpsArgentStatusList; 
	}
	
	public int checkUPSStatus(Date lDateTime) {
		//if no change in time, means OK; if updating, means not OK
		int lStatus = 0;
		
		Calendar currTime = Calendar.getInstance();
		currTime.add(Calendar.MINUTE, -3);
		Calendar lStatusTime = Calendar.getInstance();
		lStatusTime.setTime(lDateTime);
		
		if (currTime.after(lStatusTime)) {
			lStatus = Constants.EQUIP_STATUS_NORMAL;
		} else {
			lStatus = Constants.EQUIP_STATUS_NG;
		}
		return lStatus;
	}
}