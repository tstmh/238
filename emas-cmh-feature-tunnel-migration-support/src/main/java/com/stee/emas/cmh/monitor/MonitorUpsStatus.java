/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.stee.emas.cmh.common.ApplicationContextProvider;
import com.stee.emas.cmh.integration.CMHMessageSender;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.EquipStatusDto;
import com.stee.emas.common.dto.EquipStatusDtoList;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emas.common.entity.EquipConfig;
import com.stee.emas.common.entity.EquipStatus;
import com.stee.emas.common.entity.TechnicalAlarm;
import com.stee.emas.common.entity.UpsArgentStatus;
import com.stee.emas.common.util.CommonUtil;
import com.stee.emas.common.util.DTOConverter;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Quartz Job class for UPS Monitoring</p>
 * <p>This class is used for monitoring UPS Status
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Dec 9, 2013
 * @version 1.0
 *
 */

@DisallowConcurrentExecution
@Component("monitorUpsStatus")
public class MonitorUpsStatus implements Job {

	private static Logger logger = LoggerFactory.getLogger(MonitorUpsStatus.class);

	@Autowired
	CmhMonitor cmhMonitor;
	@Autowired
	CMHMessageSender cmhMessageSender;

	private SchedulerConfigUps schedulerConfigUps = null;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SchedulerContext schedulerContext;
		JobDataMap dataMap = null;
		try {
			schedulerContext = context.getScheduler().getContext();
			ApplicationContext applicationContext= (ApplicationContext)schedulerContext.get("CMH-Context");
			cmhMonitor = (CmhMonitor)applicationContext.getBean("cmhMonitor");
			cmhMessageSender = (CMHMessageSender)applicationContext.getBean("cmhMessageSender");
			dataMap = context.getJobDetail().getJobDataMap();
			schedulerConfigUps = (SchedulerConfigUps)dataMap.get("SCHEDULER_CONFIG_UPS");
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		monitorOtherMultipleAlarms(dataMap);
		monitorUpsStatus(dataMap);
	}

	public void monitorOtherMultipleAlarms(JobDataMap pDataMap) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside MonitorUpsStatus :: process monitorOtherMultipleAlarms .....");
		}
		Map<String, Integer> upsMap	= schedulerConfigUps.getUpsMap();//EquipId_AlarmCode, Status
		logger.debug("upsMap :: " + upsMap);

		List<UpsArgentStatus> lUpsArgentStatusList = cmhMonitor.getStatusForOtherMultipleAlarms();
		logger.info("lUpsArgentStatusList :: " + lUpsArgentStatusList.size());

		for (UpsArgentStatus lUpsArgentStatus : lUpsArgentStatusList) {
			String lEquipId = lUpsArgentStatus.getEquipId();
			//Map<Integer, Integer> upsStateMap = new TreeMap<Integer, Integer>();
			/*Map<Integer, Integer> upsStateMap = upsMap.get(lEquipId);
			logger.info("UPS State Map :: " + upsStateMap);
			Integer lPreviousStatus = -1;
			if (upsStateMap != null) {				
				lPreviousStatus = upsStateMap.get(lUpsArgentStatus.getAlarmCode());
				if (lPreviousStatus == null) {
					lPreviousStatus = -1;
				}
			}*/
			Integer lPreviousStatus = upsMap.get(lEquipId+"_"+lUpsArgentStatus.getAlarmCode());
			if (lPreviousStatus == null) {
				lPreviousStatus = -1;
			}
			int lCurrentStatus = lUpsArgentStatus.getStatus();
			logger.debug("EquipId :: " + lEquipId);
			logger.debug("lPreviousStatus :: " + lPreviousStatus);
			logger.debug("lCurrentStatus :: " + lCurrentStatus);
			if (lPreviousStatus != lCurrentStatus) {
				if (lPreviousStatus != -1 && lCurrentStatus == Constants.EQUIP_STATUS_NORMAL) {
					logger.info("Status Changed from 0 to 2 for EquipId :: " + lEquipId);
					//ALARM_CLEAR
					try {
						processTechnicalAlarmClear(lUpsArgentStatus);
					} catch (Exception e) {
						logger.error("Error in processing Nms Technical Alarm Cleared.....",e);
					}
					/*upsStateMap.put(lUpsArgentStatus.getAlarmCode(), Constants.EQUIP_STATUS_NORMAL);
					upsMap.put(lEquipId, upsStateMap);*/
					upsMap.put(lEquipId+"_"+lUpsArgentStatus.getAlarmCode(), Constants.EQUIP_STATUS_NORMAL);
					schedulerConfigUps.setUpsMap(upsMap);
				} else if (lCurrentStatus == Constants.EQUIP_STATUS_NG) {
					logger.info("Status Changed from 2 to 0 for EquipId :: " + lEquipId);
					//ALARM_RAISE
					try {
						processTechnicalAlarmRaise(lUpsArgentStatus);
					} catch (Exception e) {
						logger.error("Error in processing Nms Technical Alarm Raised.....",e);
					}
					/*upsStateMap.put(lUpsArgentStatus.getAlarmCode(), Constants.EQUIP_STATUS_NG);
					upsMap.put(lEquipId, upsStateMap);*/
					upsMap.put(lEquipId+"_"+lUpsArgentStatus.getAlarmCode(), Constants.EQUIP_STATUS_NG);
					schedulerConfigUps.setUpsMap(upsMap);
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit MonitorUpsStatus :: process monitorOtherMultipleAlarms .....");
		}
	}

	public void monitorUpsStatus(JobDataMap pDataMap) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside MonitorUpsStatus :: process monitorUpsStatus .....");
		}		
		//Map<String, Map<Integer, Integer>> upsMap = schedulerConfigUps.getUpsMap();
		Map<String, Integer> upsMap	= schedulerConfigUps.getUpsMap();//EquipId_AlarmCode, Status
		logger.debug("upsMap :: " + upsMap);

		List<UpsArgentStatus> lUpsArgentStatusList = cmhMonitor.getStatusForUps();
		logger.info("lUpsArgentStatusList :: " + lUpsArgentStatusList.size());

		for (UpsArgentStatus lUpsArgentStatus : lUpsArgentStatusList) {
			String lEquipId = lUpsArgentStatus.getEquipId();			
			/*Map<Integer, Integer> upsStateMap = upsMap.get(lEquipId);
			logger.info("UPS State Map :: " + upsStateMap);
			Integer lPreviousStatus = -1;
			if (upsStateMap != null) {				
				lPreviousStatus = upsStateMap.get(lUpsArgentStatus.getAlarmCode());
				if (lPreviousStatus == null) {
					lPreviousStatus = -1;
				}
			}*/

			Integer lPreviousStatus = upsMap.get(lEquipId+"_"+lUpsArgentStatus.getAlarmCode());
			if (lPreviousStatus == null) {
				lPreviousStatus = -1;
			}

			int lCurrentStatus = lUpsArgentStatus.getStatus();
			logger.debug("EquipId :: " + lEquipId);
			logger.debug("lPreviousStatus :: " + lPreviousStatus);
			logger.debug("lCurrentStatus :: " + lCurrentStatus);
			if (lPreviousStatus != lCurrentStatus) {
				if (lPreviousStatus != -1 && lCurrentStatus == Constants.EQUIP_STATUS_NORMAL) {
					logger.info("Status Changed from 0 to 2 for EquipId :: " + lEquipId);
					//ALARM_CLEAR
					try {
						lUpsArgentStatus.setDateTime(new Date());
						processTechnicalAlarmClear(lUpsArgentStatus);
					} catch (Exception e) {
						logger.error("Error in processing Nms Technical Alarm Cleared.....",e);
					}
					upsMap.put(lEquipId+"_"+lUpsArgentStatus.getAlarmCode(), Constants.EQUIP_STATUS_NORMAL);
					/*upsStateMap.put(lUpsArgentStatus.getAlarmCode(), Constants.EQUIP_STATUS_NORMAL);
					upsMap.put(lEquipId, upsStateMap);*/
					schedulerConfigUps.setUpsMap(upsMap);
				} else if (lCurrentStatus == Constants.EQUIP_STATUS_NG) {
					logger.info("Status Changed from 2 to 0 for EquipId :: " + lEquipId);
					//ALARM_RAISE
					try {
						processTechnicalAlarmRaise(lUpsArgentStatus);
					} catch (Exception e) {
						logger.error("Error in processing Nms Technical Alarm Raised.....",e);
					}
					upsMap.put(lEquipId+"_"+lUpsArgentStatus.getAlarmCode(), Constants.EQUIP_STATUS_NG);					
					//upsStateMap.put(lUpsArgentStatus.getAlarmCode(), Constants.EQUIP_STATUS_NG);
					//upsMap.put(lEquipId, upsStateMap);
					logger.info("upsMap after setting :: :: :: :: :: " + upsMap);
					schedulerConfigUps.setUpsMap(upsMap);
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit MonitorUpsStatus :: process monitorUpsStatus .....");
		}
	}

	public void processTechnicalAlarmRaise(UpsArgentStatus pUpsArgentStatus) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("Inside MonitorUpsStatus :: Calling processTechnicalAlarmRaise .....");
		}
		EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(pUpsArgentStatus.getEquipId(), Constants.OPE_STATE);
		EquipConfig lEquipConfig = lEquipStatus.getEquipConfig();

		//GENERATE TECHNICAL ALARM RAISED
		TechnicalAlarmDto lTechAlarmDto = new TechnicalAlarmDto();
		String lAlarmId = CommonUtil.generateAlarmId(Constants.SYSTEM_ID, pUpsArgentStatus.getEquipId(), pUpsArgentStatus.getAlarmCode());
		lTechAlarmDto.setAlarmId(lAlarmId);
		lTechAlarmDto.setSystemId(Constants.SYSTEM_ID);
		lTechAlarmDto.setEquipId(pUpsArgentStatus.getEquipId());
		lTechAlarmDto.setEquipType(lEquipConfig.getEquipType());
		lTechAlarmDto.setAlarmCode(pUpsArgentStatus.getAlarmCode());
		lTechAlarmDto.setStartDate(pUpsArgentStatus.getDateTime());
		lTechAlarmDto.setStatus(Constants.ALARM_RAISED);
		logger.info("Technical Alarm Raised for " + lAlarmId);

		TechnicalAlarm lTechnicalAlarm = DTOConverter.convert(TechnicalAlarm.class, lTechAlarmDto);
		/***** Changed by Grace 18/12/19 *****/
		//lTechnicalAlarm.setEquipConfig(lEquipConfig);
		lTechnicalAlarm.setEquipId(pUpsArgentStatus.getEquipId());
		/*************************************/
		cmhMonitor.processTechAlarmRaise(lTechnicalAlarm);

		//SENDING TECHNICAL ALARM TO QUEUE EMASCCS & AW
		List<TechnicalAlarmDto> lAlarmList = new ArrayList<TechnicalAlarmDto>();
		lAlarmList.add(lTechAlarmDto);
		TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
		lTechAlarmDtoList.setDtoList(lAlarmList);
		logger.info("Object sent to Queue " + lTechAlarmDtoList);
		cmhMessageSender.sendAWJmsMessage("TechnicalAlarm", MessageConstants.TECH_ALARM_ID);
		cmhMessageSender.sendEmasCcsJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
		// Added 04/03/22 GH - To implement new queues to ITPT-INTF
		cmhMessageSender.sendEmasItptJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);

		WebApplicationContext springContext = (WebApplicationContext)ApplicationContextProvider.getApplicationContext();
		String lOpeFlag = (String)springContext.getServletContext().getAttribute("OPE_FLAG");
		logger.info("lOpeFlag ....." + lOpeFlag);
		boolean lUpdateStatus = false;
		if (lOpeFlag.equals(Constants.DISABLE_OPE_FLAG) && (pUpsArgentStatus.getAlarmCode() == Constants.LINK_DOWN)) {
			lUpdateStatus = true;
		} else if (lOpeFlag.equals(Constants.ENABLE_OPE_FLAG)) {
			lUpdateStatus = true;
		}
		logger.info("lUpdateStatus ....." + lUpdateStatus);
		if (lUpdateStatus) {
			lEquipStatus.setStatus(Constants.EQUIP_STATUS_NG);
			lEquipStatus.setDateTime(pUpsArgentStatus.getDateTime());
			cmhMonitor.updateEquipStatus(lEquipStatus);
			// SENDING EQUIP_STATUS_LIST TO AW & EMASCCS QUEUE
			EquipStatusDto lEquipStatusDto = DTOConverter.convert(EquipStatusDto.class, lEquipStatus);
			lEquipStatusDto.setEquipId(lEquipConfig.getEquipId());

			List<EquipStatusDto> lEquipStatusList = new ArrayList<EquipStatusDto>();
			lEquipStatusList.add(lEquipStatusDto);
			EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList(lEquipStatusList);
			logger.info("Object sent to Queue " + lEquipStatusDtoList);
			cmhMessageSender.sendAWJmsMessage("EquipStatus", MessageConstants.EQUIP_STATUS_ID);
			cmhMessageSender.sendEmasCcsJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit MonitorUpsStatus :: Calling processTechnicalAlarmRaise .....");
		}
	}

	public void processTechnicalAlarmClear(UpsArgentStatus pUpsArgentStatus) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside MonitorUpsStatus :: Calling processTechnicalAlarmClear .....");
		}
		EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(pUpsArgentStatus.getEquipId(), Constants.OPE_STATE);
		//EquipConfig lEquipConfig = lEquipStatus.getEquipConfig();

		//GENERATE TECHNICAL ALARM RAISED
		String lAlarmId = CommonUtil.generateAlarmId(Constants.SYSTEM_ID, pUpsArgentStatus.getEquipId(), pUpsArgentStatus.getAlarmCode());
		TechnicalAlarm lTechnicalAlarmDB = cmhMonitor.findTechnicalAlarmById(lAlarmId);
		logger.info("Technical Alarm Cleared for " + lAlarmId);
		if (lTechnicalAlarmDB != null) {
			/***** Changed by Grace 18/12/19 *****/
			//String lEquipId = lTechnicalAlarmDB.getEquipConfig().getEquipId();
			String lEquipId = lTechnicalAlarmDB.getEquipId();
			/*************************************/
			cmhMonitor.processTechAlarmClearMonitor(lTechnicalAlarmDB, pUpsArgentStatus.getDateTime());

			TechnicalAlarmDto lTechnicalAlarmDto = DTOConverter.convert(TechnicalAlarmDto.class, lTechnicalAlarmDB);
			lTechnicalAlarmDto.setEquipId(lEquipId);
			lTechnicalAlarmDto.setStartDate(pUpsArgentStatus.getDateTime());
			lTechnicalAlarmDto.setStatus(Constants.ALARM_CLEARED);

			//SENDING TECH_ALARM_LIST TO AW & EMASCCS
			List<TechnicalAlarmDto> lAlarmList = new ArrayList<TechnicalAlarmDto>();
			lAlarmList.add(lTechnicalAlarmDto);
			TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
			lTechAlarmDtoList.setDtoList(lAlarmList);

			logger.info("Object sent to Queue " + lTechAlarmDtoList);
			cmhMessageSender.sendAWJmsMessage("TechnicalAlarm", MessageConstants.TECH_ALARM_ID);
			cmhMessageSender.sendEmasCcsJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);

			WebApplicationContext springContext = (WebApplicationContext)ApplicationContextProvider.getApplicationContext();
			String lOpeFlag = (String)springContext.getServletContext().getAttribute("OPE_FLAG");
			boolean lUpdateStatus = false;
			if (lOpeFlag.equals(Constants.DISABLE_OPE_FLAG) && (pUpsArgentStatus.getAlarmCode() == Constants.LINK_DOWN)) {
				lUpdateStatus = true;
			} else if (lOpeFlag.equals(Constants.ENABLE_OPE_FLAG)) {
				lUpdateStatus = true;
			}
			if (lUpdateStatus) {
				lEquipStatus.setStatus(Constants.EQUIP_STATUS_NORMAL);
				lEquipStatus.setDateTime(pUpsArgentStatus.getDateTime());
				cmhMonitor.updateEquipStatus(lEquipStatus);
				// SENDING EQUIP_STATUS_LIST TO AW & EMASCCS QUEUE
				EquipStatusDto lEquipStatusDto = DTOConverter.convert(EquipStatusDto.class, lEquipStatus);
				lEquipStatusDto.setEquipId(lEquipId);

				List<EquipStatusDto> lEquipStatusList = new ArrayList<EquipStatusDto>();
				lEquipStatusList.add(lEquipStatusDto);
				EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList(lEquipStatusList);
				logger.info("Object sent to Queue " + lEquipStatusDtoList);
				cmhMessageSender.sendAWJmsMessage("EquipStatus", MessageConstants.EQUIP_STATUS_ID);
				cmhMessageSender.sendEmasCcsJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
				// Added 04/03/22 GH - To implement new queues to ITPT-INTF
				cmhMessageSender.sendEmasItptJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit MonitorUpsStatus :: Calling processTechnicalAlarmClear .....");
		}
	}
}