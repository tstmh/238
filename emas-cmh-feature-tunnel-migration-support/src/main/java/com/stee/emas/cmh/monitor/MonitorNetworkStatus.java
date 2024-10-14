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
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

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
import com.stee.emas.common.util.CommonUtil;
import com.stee.emas.common.util.DTOConverter;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Quartz Job class for Network Monitoring</p>
 * <p>This class is used for monitoring Network Status by Argent
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since May 3, 2013
 * @version 1.0
 *
 */

//ces_01, ces_02, ces_03, mes_01, mes_02, mes_03, mes_04, mes_05, dbs_01, dbs_02, bks_01, nms_01, vtl_01, fcw_01, dmc_01, dmc_02, isw_01, isw_02, asw_01, asw_02, bkw_01, eru_01
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MonitorNetworkStatus implements Job {

	private static Logger logger = LoggerFactory.getLogger(MonitorNetworkStatus.class);

	@Autowired
	CmhMonitor cmhMonitor;
	@Autowired
	CMHMessageSender cmhMessageSender;

	private SchedulerConfigNetwork schedulerConfig = null;
	List<String> equipIdList = null;

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
			schedulerConfig = (SchedulerConfigNetwork)dataMap.get("SCHEDULER_CONFIG_NETWORK");
			equipIdList = schedulerConfig.getEquipIdList();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		monitorNetwork(dataMap);
	}

	private void monitorNetwork(JobDataMap pDataMap) {

		Map<String, Integer> pingStateMap	= schedulerConfig.getPingStateMap();
		List<EquipStatus> lEquipStatusList = cmhMonitor.getStatusForNetworkEquipments(equipIdList);
		logger.debug("lEquipStatusList :: " + lEquipStatusList.size());
		for (EquipStatus lEquipStatus : lEquipStatusList) {
			Integer lPreviousStatus = pingStateMap.get(lEquipStatus.getEquipConfig().getEquipId());
			if (lPreviousStatus == null) {
				lPreviousStatus = -1;
			}
			int lCurrentStatus = lEquipStatus.getStatus();

			logger.debug("EquipId :: " + lEquipStatus.getEquipConfig().getEquipId());
			logger.debug("lPreviousStatus :: " + lPreviousStatus);
			logger.debug("lCurrentStatus :: " + lCurrentStatus);
			if (lPreviousStatus != lCurrentStatus) {
				if (lCurrentStatus == Constants.EQUIP_STATUS_NORMAL) {
					logger.info("Status Changed from 0 to 2 for EquipId :: " + lEquipStatus.getEquipConfig().getEquipId());
					//ALARM_CLEAR
					try {
						generateTechnicalAlarmClear(lEquipStatus, lEquipStatus.getDateTime());
					} catch (Exception e) {
						logger.error("Error in processing Nms Technical Alarm Cleared.....",e);
					}
					pingStateMap.put(lEquipStatus.getEquipConfig().getEquipId(), Constants.EQUIP_STATUS_NORMAL);
					schedulerConfig.setPingStateMap(pingStateMap);
				} else if (lCurrentStatus == Constants.EQUIP_STATUS_NG) {
					logger.info("Status Changed from 2 to 0 for EquipId :: " + lEquipStatus.getEquipConfig().getEquipId());
					//ALARM_RAISE
					try {
						processTechnicalAlarmRaise(lEquipStatus, lEquipStatus.getDateTime());
					} catch (Exception e) {
						logger.error("Error in processing Nms Technical Alarm Raised.....",e);
					}
					pingStateMap.put(lEquipStatus.getEquipConfig().getEquipId(), Constants.EQUIP_STATUS_NG);
					schedulerConfig.setPingStateMap(pingStateMap);
				}
			}
		}
	}

	public void generateTechnicalAlarmClear(EquipStatus pEquipStatus, Date pCurrentDate) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside MonitorNetworkStatus :: Calling processTechnicalAlarmClear .....");
		}
		String lAlarmId = CommonUtil.generateAlarmId(Constants.SYSTEM_ID, pEquipStatus.getEquipConfig().getEquipId(), Constants.LINK_DOWN);

		TechnicalAlarm lTechnicalAlarmDB = cmhMonitor.findTechnicalAlarmById(lAlarmId);
		logger.info("Technical Alarm Cleared for " + lAlarmId);
		if (lTechnicalAlarmDB != null) {
			/***** Changed by Grace 18/12/19 *****/
			//String lEquipId = lTechnicalAlarmDB.getEquipConfig().getEquipId();
			String lEquipId = lTechnicalAlarmDB.getEquipId();
			/*************************************/
			cmhMonitor.processTechAlarmClearMonitor(lTechnicalAlarmDB, pCurrentDate);

			TechnicalAlarmDto lTechnicalAlarmDto = DTOConverter.convert(TechnicalAlarmDto.class, lTechnicalAlarmDB);
			lTechnicalAlarmDto.setEquipId(lEquipId);
			lTechnicalAlarmDto.setStartDate(pCurrentDate);
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

			// SENDING EQUIP_STATUS_LIST TO AW & EMASCCS QUEUE
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(lEquipId, Constants.OPE_STATE);
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
		if (logger.isInfoEnabled()) {
			logger.info("Exit MonitorNetworkStatus :: Calling processTechnicalAlarmClear .....");
		}
	}

	public void processTechnicalAlarmRaise(EquipStatus pEquipStatus, Date pCurrentDate) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("Inside MonitorNetworkStatus :: Calling processTechnicalAlarmRaise .....");
		}
		EquipConfig lEquipConfig = pEquipStatus.getEquipConfig();

		//GENERATE TECHNICAL ALARM RAISED
		TechnicalAlarmDto lTechAlarmDto = new TechnicalAlarmDto();
		String lAlarmId = CommonUtil.generateAlarmId(Constants.SYSTEM_ID, lEquipConfig.getEquipId(), Constants.LINK_DOWN);
		lTechAlarmDto.setAlarmId(lAlarmId);
		lTechAlarmDto.setSystemId(Constants.SYSTEM_ID);
		lTechAlarmDto.setEquipId(lEquipConfig.getEquipId());
		lTechAlarmDto.setEquipType(lEquipConfig.getEquipType());
		lTechAlarmDto.setAlarmCode(Constants.LINK_DOWN);
		lTechAlarmDto.setStartDate(pCurrentDate);
		lTechAlarmDto.setStatus(Constants.ALARM_RAISED);
		logger.info("Technical Alarm Raised for " + lAlarmId);

		TechnicalAlarm lTechnicalAlarm = DTOConverter.convert(TechnicalAlarm.class, lTechAlarmDto);
		/***** Changed by Grace 18/12/19 *****/
		//lTechnicalAlarm.setEquipConfig(lEquipConfig);
		lTechnicalAlarm.setEquipId(lEquipConfig.getEquipId());
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

		// SENDING EQUIP_STATUS_LIST TO AW & EMASCCS QUEUE
		EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(lEquipConfig.getEquipId(), Constants.OPE_STATE);
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

		if (logger.isInfoEnabled()) {
			logger.info("Exit MonitorNetworkStatus :: Calling processTechnicalAlarmRaise .....");
		}
	}
}