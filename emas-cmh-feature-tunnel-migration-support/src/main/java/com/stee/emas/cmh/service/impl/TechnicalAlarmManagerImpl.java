/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stee.emas.cmh.service.TechnicalAlarmManager;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.dao.HistTechnicalAlarmDao;
import com.stee.emas.common.dao.TechnicalAlarmDao;
import com.stee.emas.common.dto.TechAlarmAckDto;
import com.stee.emas.common.entity.HistTechnicalAlarm;
import com.stee.emas.common.entity.TechnicalAlarm;
import com.stee.emas.common.util.MessageConverterUtil;

/***** Changed by Grace 18/12/19 *****/
import com.stee.emas.common.dao.ConfigDao;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emas.common.entity.SiteEquipConfig;
/*************************************/

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Manager class for Technical alarm </p>
 * <p>This class is used to handle technical alarm related functions
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Sep 24, 2012
 * @version 1.0
 *
 */
@Service("technicalAlarmManager")
@Transactional
public class TechnicalAlarmManagerImpl implements TechnicalAlarmManager {

	private static Logger logger = LoggerFactory.getLogger(TechnicalAlarmManagerImpl.class);

	@Autowired
	private TechnicalAlarmDao technicalAlarmDao;
	@Autowired
	private HistTechnicalAlarmDao histTechAlarmDao;
	/***** Changed by Grace 18/12/19 *****/
	@Autowired
	private ConfigDao configDao;
	/*************************************/	
	@Autowired
	MessageConverterUtil messageConverterUtil;

	@Override
	public String saveTechnicalAlarm(TechnicalAlarm pTechnicalAlarm) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> saveTechnicalAlarm :: AlarmId :: " + pTechnicalAlarm.getAlarmId());
		}
		if (pTechnicalAlarm == null) {
			return null;
		}
		TechnicalAlarm lTechnicalAlarmDB = technicalAlarmDao.findTechnicalAlarmById(pTechnicalAlarm.getAlarmId());
		if (lTechnicalAlarmDB == null) {
			logger.info("Technical Alarm already raised for the the alarmId " + pTechnicalAlarm.getAlarmId());
			return null;
		}
		String lId = technicalAlarmDao.saveTechnicalAlarm(pTechnicalAlarm);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> saveTechnicalAlarm :: AlarmId :: " + pTechnicalAlarm.getAlarmId());
		}
		return lId;
	}

	@Override
	public String updateTechnicalAlarm(TechnicalAlarm pTechnicalAlarm) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> updateTechnicalAlarm :: AlarmId :: " + pTechnicalAlarm.getAlarmId());
		}
		String lId = technicalAlarmDao.updateTechnicalAlarm(pTechnicalAlarm);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> updateTechnicalAlarm :: AlarmId :: " + pTechnicalAlarm.getAlarmId());
		}
		return lId;
	}

	@Override
	public String deleteTechnicalAlarm(TechnicalAlarm pTechnicalAlarm) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> deleteTechnicalAlarm :: AlarmId :: " + pTechnicalAlarm.getAlarmId());
		}
		String lId = technicalAlarmDao.deleteTechnicalAlarm(pTechnicalAlarm);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> deleteTechnicalAlarm :: AlarmId :: " + pTechnicalAlarm.getAlarmId());
		}
		return lId;
	}

	@Override
	public boolean processTechAlarmClearMonitor(TechnicalAlarm pTechnicalAlarm, Date pEndDate) {
		logger.debug("Inside Manager :: processTechAlarmClearMonitor -> Start.....");
		try {
			if (pTechnicalAlarm != null) {
				HistTechnicalAlarm lHistTechnicalAlarm = messageConverterUtil.generateHistTechAlarmEntity(pTechnicalAlarm, pEndDate, null);
				histTechAlarmDao.saveHistTechnicalAlarm(lHistTechnicalAlarm);
				technicalAlarmDao.deleteTechnicalAlarm(pTechnicalAlarm);
			}
		} catch (Exception e) {
			logger.error("Error in processing TechnicalAlarm Clear.....", e);
			return false;
		}
		logger.debug("Exit Manager :: processTechAlarmClearMonitor.....");
		return true;
	}

	public boolean processTechAlarmClear(TechnicalAlarm pTechnicalAlarmJms, String pClearBy) /*throws Exception*/ {
		logger.debug("Inside Manager :: processTechAlarmClear -> Start.....");
		TechnicalAlarm lTechnicalAlarmDB = technicalAlarmDao.findTechnicalAlarmById(pTechnicalAlarmJms.getAlarmId());
		if (lTechnicalAlarmDB != null) {
			/*try {
				HistTechnicalAlarm lHistTechnicalAlarm = messageConverterUtil.generateHistTechAlarmEntity(lTechnicalAlarmDB, pTechnicalAlarmJms.getStartDate(), pClearBy);
				histTechAlarmDao.saveHistTechnicalAlarm(lHistTechnicalAlarm);
			} catch (Exception e) {
				logger.error("Error in deleting HistTechnicalAlarm.....", e);
			}*/
			try {
				technicalAlarmDao.deleteTechnicalAlarm(lTechnicalAlarmDB);
				logger.info("Technical Alarm deleted for alarmId " + lTechnicalAlarmDB.getAlarmId());
			} catch (Exception e) {
				logger.error("Error in deleting TechnicalAlarm.....", e);
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	/***** Changed by Grace 18/12/19 *****/
	public boolean processTechAlarmClearedByTPND(TechnicalAlarm lTechnicalAlarm, String pClearBy) {
		logger.debug("Inside Manager :: processTechAlarmClearedByTPND -> Start.....");
		// Check again in case device's technical alarm is cleared by TPND. Check for device related TPND devices technical alarms instead
		boolean result = false;

		//SiteEquipConfig siteEquipConfig = configDao.findSiteEquipConfigById(lTechnicalAlarm.getEquipConfig().getEquipId());
		SiteEquipConfig siteEquipConfig = configDao.findSiteEquipConfigById(lTechnicalAlarm.getEquipId());		

		if (siteEquipConfig != null) {
			String relatedMDB = null;
			String relatedTelco = null;
			String relatedPair = null;
			
			relatedMDB = siteEquipConfig.getPowerGridAccountNo();
			
			if (relatedMDB != null) {
				relatedTelco = siteEquipConfig.getSingtelCircuitNo();
				if (relatedTelco != null){
					relatedPair = "tpn_"+relatedMDB.substring(4)+"_"+relatedTelco.substring(8);
				}
			}

			TechnicalAlarm mdbTechnicalAlarmDB = null;
			TechnicalAlarm telcoTechnicalAlarmDB = null;
			TechnicalAlarm pairTechnicalAlarmDB = null;

			if (relatedMDB != null) {
				mdbTechnicalAlarmDB = technicalAlarmDao.findTechnicalAlarmById("emas_"+relatedMDB+"_1");
			}
			if (relatedTelco != null) {
				telcoTechnicalAlarmDB = technicalAlarmDao.findTechnicalAlarmById("emas_"+relatedTelco+"_1");
			}
			if (relatedPair != null) {
				pairTechnicalAlarmDB = technicalAlarmDao.findTechnicalAlarmById("emas_"+relatedPair+"_1");
			}

			if ((mdbTechnicalAlarmDB != null) ||
					(telcoTechnicalAlarmDB != null) ||
					(pairTechnicalAlarmDB != null)) {
				result = true;
			} else {
				return false;
			}			
		}

		logger.debug("Exit Manager :: processTechAlarmClearedByTPND.....");
		return result;
	}

	public boolean processTPNDTechAlarmClear(TechnicalAlarmDto tpndDto, String pClearBy) /*throws Exception*/ {
		logger.debug("Inside Manager :: processTPNDTechAlarmClear -> Start.....");
		TechnicalAlarm lTechnicalAlarmDB = technicalAlarmDao.findTechnicalAlarmById(tpndDto.getAlarmId());
		if (lTechnicalAlarmDB != null) {
			/*try {
				HistTechnicalAlarm lHistTechnicalAlarm = messageConverterUtil.generateHistTechAlarmEntity(lTechnicalAlarmDB, pTechnicalAlarmJms.getStartDate(), pClearBy);
				histTechAlarmDao.saveHistTechnicalAlarm(lHistTechnicalAlarm);
			} catch (Exception e) {
				logger.error("Error in deleting HistTechnicalAlarm.....", e);
			}*/
			try {
				technicalAlarmDao.deleteTechnicalAlarm(lTechnicalAlarmDB);
				logger.info("Technical Alarm deleted for alarmId " + lTechnicalAlarmDB.getAlarmId());
			} catch (Exception e) {
				logger.error("Error in deleting TechnicalAlarm.....", e);
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	/*************************************/

	public void saveHistTechnialAlarm(TechnicalAlarm pTechnicalAlarmJms, String pClearBy) {
		TechnicalAlarm lTechnicalAlarmDB = technicalAlarmDao.findTechnicalAlarmById(pTechnicalAlarmJms.getAlarmId());
		if (lTechnicalAlarmDB != null) {
			try {
				HistTechnicalAlarm lHistTechnicalAlarm = messageConverterUtil.generateHistTechAlarmEntity(lTechnicalAlarmDB, pTechnicalAlarmJms.getStartDate(), pClearBy);
				histTechAlarmDao.saveHistTechnicalAlarm(lHistTechnicalAlarm);
				logger.info("saveHistTechnialAlarm :: HistTechnicalAlarm saved, " + pTechnicalAlarmJms.getAlarmId());
			} catch (Exception e) {
				logger.error("Error in saving HistTechnicalAlarm.....", e);
			}			
		} else {
			logger.info("saveHistTechnialAlarm :: Unable to save HistTechnicalAlarm, " + pTechnicalAlarmJms.getAlarmId() + " not found in technical alarm database");			
		}
		/***** Changed by Grace 18/12/19 	- commented out on 10/02/22 GH due to code review	
		if (pClearBy == null) {
			TechnicalAlarm lTechnicalAlarmDB = technicalAlarmDao.findTechnicalAlarmById(pTechnicalAlarmJms.getAlarmId());
			if (lTechnicalAlarmDB != null) {
				try {
					HistTechnicalAlarm lHistTechnicalAlarm = messageConverterUtil.generateHistTechAlarmEntity(lTechnicalAlarmDB, pTechnicalAlarmJms.getStartDate(), pClearBy);
					histTechAlarmDao.saveHistTechnicalAlarm(lHistTechnicalAlarm);
				} catch (Exception e) {
					logger.error("Error in saving HistTechnicalAlarm.....", e);
				}			
			}
		} 
		else if (!pClearBy.equals(TPNDConstants.TPND)) {
			TechnicalAlarm lTechnicalAlarmDB = technicalAlarmDao.findTechnicalAlarmById(pTechnicalAlarmJms.getAlarmId());
			if (lTechnicalAlarmDB != null) {
				try {
					HistTechnicalAlarm lHistTechnicalAlarm = messageConverterUtil.generateHistTechAlarmEntity(lTechnicalAlarmDB, pTechnicalAlarmJms.getStartDate(), pClearBy);
					histTechAlarmDao.saveHistTechnicalAlarm(lHistTechnicalAlarm);
				} catch (Exception e) {
					logger.error("Error in saving HistTechnicalAlarm.....", e);
				}			
			} 
		}
		else {
			// Processing for alarms cleared by TPND
			try {
				HistTechnicalAlarm lHistTechnicalAlarm = messageConverterUtil.generateHistTechAlarmEntity(pTechnicalAlarmJms, pTechnicalAlarmJms.getStartDate(), pClearBy);
				histTechAlarmDao.saveHistTechnicalAlarm(lHistTechnicalAlarm);
			} catch (Exception e) {
				logger.error("Error in saving HistTechnicalAlarm.....", e);
			}			
		}
		/*************************************/
	}	

	@Override
	public boolean processTechAlarmRaise(TechnicalAlarm pTechnicalAlarm) throws Exception {
		logger.debug("Inside Manager :: updateTechAlarmRaise -> Start.....");
		/***** Changed by Grace 18/12/19 *****/
		if (pTechnicalAlarm == null ) {
			logger.debug("pTechnicalAlarm is null");
			return false;
		}
		/*************************************/
		boolean lSendToQueue = true;
		TechnicalAlarm lTechnicalAlarmDB = technicalAlarmDao.findTechnicalAlarmById(pTechnicalAlarm.getAlarmId());
		if (lTechnicalAlarmDB == null) {
			String lId = technicalAlarmDao.saveTechnicalAlarm(pTechnicalAlarm);
			if (lId == null) {
				lSendToQueue = false;
			}
			logger.info("Technical Alarm created for alarmId " + pTechnicalAlarm.getAlarmId());
			lSendToQueue = true;
		} else {
			logger.info("Technical Alarm already raised for the the alarmId " + pTechnicalAlarm.getAlarmId());
			lSendToQueue = false;
		}
		logger.debug("Exit Manager :: updateTechAlarmRaise -> Start.....");
		return lSendToQueue;
	}

	@Override
	public boolean processTechAlarmAck(TechAlarmAckDto pTechAlarmAckDto, String pSource) {
		logger.debug("Inside Manager :: processTechAlarmAck -> pSource ::"+pSource);
		boolean lIsCurrentTechAlarm = false;
		if (pSource.startsWith(Constants.AW_SENDER)) {
			TechnicalAlarm lTechnicalAlarmDB = technicalAlarmDao.findTechnicalAlarmById(pTechAlarmAckDto.getAlarmId());
			if (lTechnicalAlarmDB != null && lTechnicalAlarmDB.getAckDate() == null) {
				lTechnicalAlarmDB.setAckBy(pTechAlarmAckDto.getAckBy());
				lTechnicalAlarmDB.setAckDate(pTechAlarmAckDto.getAckDate());
				lTechnicalAlarmDB.setStatus(Constants.ALARM_ACK);
				technicalAlarmDao.updateTechnicalAlarm(lTechnicalAlarmDB);
				logger.info("TechnicalAlarm acknowledged successfully for AlarmId " + pTechAlarmAckDto.getAlarmId());
				lIsCurrentTechAlarm = true;
			}
		} else if (pSource.equals(Constants.EMASCCS_SENDER)) {
			TechnicalAlarm lTechnicalAlarmDB = technicalAlarmDao.findTechnicalAlarmById(pTechAlarmAckDto.getAlarmId());
			if (lTechnicalAlarmDB != null && lTechnicalAlarmDB.getAckDate() == null) {
				lTechnicalAlarmDB.setAckBy(pTechAlarmAckDto.getAckBy());
				lTechnicalAlarmDB.setAckDate(pTechAlarmAckDto.getAckDate());
				lTechnicalAlarmDB.setStatus(Constants.ALARM_ACK);
				technicalAlarmDao.updateTechnicalAlarm(lTechnicalAlarmDB);
				logger.info("TechnicalAlarm acknowledged successfully for AlarmId " + pTechAlarmAckDto.getAlarmId());
				lIsCurrentTechAlarm = true;
			} else {
				HistTechnicalAlarm lHistTechAlarm = histTechAlarmDao.findLatestHistTechAlarmByAlarmId(pTechAlarmAckDto.getAlarmId());
				if (lHistTechAlarm != null && lHistTechAlarm.getAckDate() == null) {
					lHistTechAlarm.setAckBy(pTechAlarmAckDto.getAckBy());
					lHistTechAlarm.setAckDate(pTechAlarmAckDto.getAckDate());
					histTechAlarmDao.updateHistTechnicalAlarm(lHistTechAlarm);
					logger.info("HistTechnicalAlarm acknowledged successfully for AlarmId " + pTechAlarmAckDto.getAlarmId());
					lIsCurrentTechAlarm = false;
				}
			}
		}
		logger.debug("Exit Manager :: processTechAlarmAck.....lIsCurrentTechAlarm::"+lIsCurrentTechAlarm);
		return lIsCurrentTechAlarm;
	}

	@Override
	public List<TechnicalAlarm> getTechnicalAlarmByEquipTypeAndAlarmCode(String pEquipType, int pAlarmCode) {
		logger.debug("Inside Manager :: getTechnicalAlarmByEquipTypeAndAlarmCode -> EquipType :: " + pEquipType + ".....AlarmCode :: " + pAlarmCode);
		if (pEquipType == null || pEquipType.trim().length() == 0) {
			logger.info("EquipType is null or empty ..... ");
			return null;
		}
		List<TechnicalAlarm> lTechnicalAlarmList = technicalAlarmDao.getTechnicalAlarmByEquipTypeAndAlarmCode(pEquipType, pAlarmCode);
		if (lTechnicalAlarmList != null) {
			if (logger.isInfoEnabled()) {
				logger.info("TechnicalAlarmList Size :: " + lTechnicalAlarmList.size());
			}
		}
		logger.debug("Exit Manager :: getTechnicalAlarmByEquipTypeAndAlarmCode -> EquipType :: " + pEquipType + ".....AlarmCode :: " + pAlarmCode);
		return lTechnicalAlarmList;
	}

	@Override
	public List<TechnicalAlarm> getTechAlarmByEquipTypeAndNotInAlarmCode(String pEquipType, int pAlarmCode) {
		logger.debug("Inside Manager :: getTechAlarmByEquipTypeAndNotInAlarmCode -> EquipType :: " + pEquipType + ".....AlarmCode :: " + pAlarmCode);
		if (pEquipType == null || pEquipType.trim().length() == 0) {
			logger.info("EquipType is null or empty ..... ");
			return null;
		}
		List<TechnicalAlarm> lTechnicalAlarmList = technicalAlarmDao.getTechAlarmByEquipTypeAndNotInAlarmCode(pEquipType, pAlarmCode);
		if (lTechnicalAlarmList != null) {
			if (logger.isInfoEnabled()) {
				logger.info("TechnicalAlarmList Size :: " + lTechnicalAlarmList.size());
			}
		}
		logger.debug("Exit Manager :: getTechAlarmByEquipTypeAndNotInAlarmCode -> EquipType :: " + pEquipType + ".....AlarmCode :: " + pAlarmCode);
		return lTechnicalAlarmList;
	}

	@Override
	public List<TechnicalAlarm> getAllTechnicalAlarm() {
		List<TechnicalAlarm> lTechnicalAlarmList = technicalAlarmDao.getAllTechnicalAlarm();
		if (lTechnicalAlarmList != null) {
			if (logger.isInfoEnabled()) {
				logger.info("TechnicalAlarmList Size :: " + lTechnicalAlarmList.size());
			}
		}
		return lTechnicalAlarmList;
	}

	@Override
	public TechnicalAlarm findTechnicalAlarmById(String pAlarmId) {
		TechnicalAlarm lTechnicalAlarm = technicalAlarmDao.findTechnicalAlarmById(pAlarmId);
		return lTechnicalAlarm;
	}

	@Override
	public List<TechnicalAlarm> getTechnicalAlarmByEquipId(String pEquipId) {
		logger.debug("Inside Manager :: getTechnicalAlarmByEquipId -> EquipId :: " + pEquipId);
		if (pEquipId == null || pEquipId.trim().length() == 0) {
			logger.info("EquipId is null or empty ..... ");
			return null;
		}
		List<TechnicalAlarm> lTechnicalAlarmList = technicalAlarmDao.getTechnicalAlarmByEquipId(pEquipId);
		if (lTechnicalAlarmList != null) {
			if (logger.isInfoEnabled()) {
				logger.info("TechnicalAlarmList Size :: " + lTechnicalAlarmList.size());
			}
		}
		logger.debug("Exit Manager :: getTechnicalAlarmByEquipId -> EquipId :: " + pEquipId);
		return lTechnicalAlarmList;
	}

	@Override
	public TechnicalAlarm getTechnicalAlarmByEquipIdAndAlarmCode(String pEquipId, int pAlarmCode) {
		logger.debug("Inside Manager :: getTechnicalAlarmByEquipId -> EquipId :: " + pEquipId);
		if (pEquipId == null || pEquipId.trim().length() == 0) {
			logger.info("EquipId is null or empty ..... ");
			return null;
		}
		TechnicalAlarm lTechnicalAlarm = technicalAlarmDao.getTechnicalAlarmByEquipIdAndAlarmCode(pEquipId, pAlarmCode);
		logger.debug("Exit Manager :: getTechnicalAlarmByEquipId -> EquipId :: " + pEquipId);
		return lTechnicalAlarm;
	}

	/***** Changed by Grace 18/12/19 *****/	
	@Override
	public List<TechnicalAlarm> getAllTPNTechnicalAlarm() {
		List<TechnicalAlarm> lTechnicalAlarmList = technicalAlarmDao.getAllTPNTechnicalAlarm();
		if (lTechnicalAlarmList != null) {
			if (logger.isInfoEnabled()) {
				logger.info("getAllTPNTechnicalAlarm :: TechnicalAlarmList Size :: " + lTechnicalAlarmList.size());
			}
		}
		return lTechnicalAlarmList;
	}
	/*************************************/

}