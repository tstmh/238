/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.monitor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.springframework.stereotype.Component;

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
 * <p>Description : Quartz Job class for Interface Monitoring</p>
 * <p>This class is used for monitoring Interface Status
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Apr 30, 2013
 * @version 1.0
 *
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component("monitorIntfStatus")
public class MonitorIntfStatus implements Job {

	private static Logger logger = LoggerFactory.getLogger(MonitorIntfStatus.class);

	private SchedulerConfig schedulerConfig = null;

	@Autowired
	CmhMonitor cmhMonitor;
	@Autowired
	CMHMessageSender cmhMessageSender;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SchedulerContext schedulerContext;
		JobDataMap dataMap = null;
		try {
			schedulerContext = context.getScheduler().getContext();
			ApplicationContext applicationContext = (ApplicationContext) schedulerContext.get("CMH-Context");
			cmhMonitor = (CmhMonitor) applicationContext.getBean("cmhMonitor");
			cmhMessageSender = (CMHMessageSender) applicationContext.getBean("cmhMessageSender");
			dataMap = context.getJobDetail().getJobDataMap();
			schedulerConfig = (SchedulerConfig) dataMap.get("SCHEDULER_CONFIG");
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		monitor();
	}

	public void monitor() {
		monitorTicssStatus();
		monitorScssStatus();
		monitorDcssStatus();
		monitorWcssStatus();
		monitorNmsStatus();
		monitorNwt1Status();
		monitorNwt2Status();
		monitorIdssStatus();
		monitorEmasccsStatus();
		//monitorEmasitptStatus(); // Added 07/03/22 GH
		//monitorEmastctsStatus(); // Added 07/03/22 GH
		//monitorLusStatus();
	}

	private void monitorLusStatus() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR LUS INTERFACE STATUS .....");
		}
		Date lCurrentDate = new Date();
		int pingInterval = schedulerConfig.getPingInterval();
		int retryCount = schedulerConfig.getRetryCount();		
		boolean lusAlarmRaised = schedulerConfig.getLusAlarmRaised();
		int lusRetryCount = schedulerConfig.getLusRetryCount();

		logger.debug("retryCount :: " + retryCount);
		logger.debug("lusAlarmRaised :: " + lusAlarmRaised);
		logger.debug("lusRetryCount :: " + lusRetryCount);
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.LUS_EQUIP_ID, Constants.OPE_STATE);
			int lLusStatus = lEquipStatus.getStatus();
			Date lLusStatusTime = lEquipStatus.getDateTime();

			if (lusAlarmRaised) {
				if (lLusStatus == Constants.EQUIP_STATUS_NORMAL) {
					//GENERATE TECHNICAL ALARM CLEARED
					processTechnicalAlarmClear(lEquipStatus, lCurrentDate);
					schedulerConfig.setLusAlarmRaised(false);
					logger.info("LUS Alarm Raised....." + lusAlarmRaised);
				}
			} else {
				long dateTimeDiff = lCurrentDate.getTime() - lLusStatusTime.getTime();
				long waitTimeInterval = 3 * (pingInterval*1000);
				if (dateTimeDiff >=  waitTimeInterval) {
					logger.info("Time Diff for LUS Intf ....." + dateTimeDiff);
					logger.info("lusRetryCount :: " + lusRetryCount + " retryCount :: " + retryCount);
					if (lusRetryCount >= retryCount) {
						schedulerConfig.setLusAlarmRaised(true);
						schedulerConfig.setLusRetryCount(0);
						//GENERATE TECHNICAL ALARM RAISE
						processTechnicalAlarmRaise(lEquipStatus, lCurrentDate);
						logger.info("IDSS Alarm Raised....." + lusAlarmRaised);
					} else {
						schedulerConfig.setLusRetryCount(++lusRetryCount);
					}
				} else {
					schedulerConfig.setLusRetryCount(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error in updating IDSS Technical Alarm Raised/Cleared.....", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorIntfStatus :: monitorIdssStatus .....");
		}
	}

	private void monitorTicssStatus() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR MFELS INTERFACE STATUS .....");
		}
		Date lCurrentDate = new Date();
		int pingInterval = schedulerConfig.getPingInterval();
		int retryCount = schedulerConfig.getRetryCount();		
		boolean ticssAlarmRaised = schedulerConfig.getTicssAlarmRaised();
		int ticssRetryCount = schedulerConfig.getTicssRetryCount();

		logger.debug("retryCount :: " + retryCount);
		logger.debug("ticssAlarmRaised :: " + ticssAlarmRaised);
		logger.debug("ticssRetryCount :: " + ticssRetryCount);
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.TICSS_EQUIP_ID, Constants.OPE_STATE);
			int lTicssStatus = lEquipStatus.getStatus();
			Date lTicssStatusTime = lEquipStatus.getDateTime();

			if (ticssAlarmRaised) {
				if (lTicssStatus == Constants.EQUIP_STATUS_NORMAL) {
					//GENERATE TECHNICAL ALARM CLEARED
					processTechnicalAlarmClear(lEquipStatus, lCurrentDate);
					schedulerConfig.setTicssAlarmRaised(false);
					logger.info("TICSS Alarm Raised....." + ticssAlarmRaised);
				}
			} else if (!ticssAlarmRaised) {
				long dateTimeDiff = lCurrentDate.getTime() - lTicssStatusTime.getTime();
				long waitTimeInterval = 3 * (pingInterval*1000);
				if (dateTimeDiff >=  waitTimeInterval) {
					logger.info("Time Diff for MFELS Intf ....." + dateTimeDiff);
					logger.info("ticssRetryCount :: " + ticssRetryCount + " retryCount :: " + retryCount);
					if (ticssRetryCount >= retryCount) {
						schedulerConfig.setTicssAlarmRaised(true);
						schedulerConfig.setTicssRetryCount(0);
						//GENERATE TECHNICAL ALARM RAISE
						processTechnicalAlarmRaise(lEquipStatus, lCurrentDate);
						logger.info("TICSS Alarm Raised....." + ticssAlarmRaised);
					} else {
						schedulerConfig.setTicssRetryCount(++ticssRetryCount);
					}
				} else {
					schedulerConfig.setTicssRetryCount(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error in updating TICSS Technical Alarm Raised/Cleared.....", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorIntfStatus :: monitorTicssStatus .....");
		}
	}

	private void monitorIdssStatus() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR IDSS INTERFACE STATUS .....");
		}
		Date lCurrentDate = new Date();
		int pingInterval = schedulerConfig.getPingInterval();
		int retryCount = schedulerConfig.getRetryCount();		
		boolean idssAlarmRaised = schedulerConfig.getIdssAlarmRaised();
		int idssRetryCount = schedulerConfig.getIdssRetryCount();

		logger.debug("retryCount :: " + retryCount);
		logger.debug("idssAlarmRaised :: " + idssAlarmRaised);
		logger.debug("idssRetryCount :: " + idssRetryCount);
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.IDSS_EQUIP_ID, Constants.OPE_STATE);
			int lIdssStatus = lEquipStatus.getStatus();
			Date lIdssStatusTime = lEquipStatus.getDateTime();

			if (idssAlarmRaised) {
				if (lIdssStatus == Constants.EQUIP_STATUS_NORMAL) {
					//GENERATE TECHNICAL ALARM CLEARED
					processTechnicalAlarmClear(lEquipStatus, lCurrentDate);
					schedulerConfig.setIdssAlarmRaised(false);
					logger.info("IDSS Alarm Raised....." + idssAlarmRaised);
				}
			} else {
				long dateTimeDiff = lCurrentDate.getTime() - lIdssStatusTime.getTime();
				long waitTimeInterval = 3 * (pingInterval*1000);
				if (dateTimeDiff >=  waitTimeInterval) {
					logger.info("Time Diff for IDSS Intf ....." + dateTimeDiff);
					logger.info("idssRetryCount :: " + idssRetryCount + " retryCount :: " + retryCount);
					if (idssRetryCount >= retryCount) {
						schedulerConfig.setIdssAlarmRaised(true);
						schedulerConfig.setIdssRetryCount(0);
						//GENERATE TECHNICAL ALARM RAISE
						processTechnicalAlarmRaise(lEquipStatus, lCurrentDate);
						logger.info("IDSS Alarm Raised....." + idssAlarmRaised);
					} else {
						schedulerConfig.setIdssRetryCount(++idssRetryCount);
					}
				} else {
					schedulerConfig.setIdssRetryCount(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error in updating IDSS Technical Alarm Raised/Cleared.....", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorIntfStatus :: monitorIdssStatus .....");
		}
	}

	private void monitorScssStatus() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR SCSS INTERFACE STATUS .....");
		}
		Date lCurrentDate = new Date();
		int pingInterval = schedulerConfig.getPingInterval();
		int retryCount = schedulerConfig.getRetryCount();
		boolean scssAlarmRaised = schedulerConfig.getScssAlarmRaised();
		int scssRetryCount = schedulerConfig.getScssRetryCount();

		logger.debug("retryCount :: " + retryCount);
		logger.debug("scssAlarmRaised :: " + scssAlarmRaised);
		logger.debug("scssRetryCount :: " + scssRetryCount);
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.SCSS_SYSTEM_NAME, Constants.OPE_STATE);
			int lScssStatus = lEquipStatus.getStatus();
			Date lScssStatusTime = lEquipStatus.getDateTime();

			if (scssAlarmRaised) {
				if (lScssStatus == Constants.EQUIP_STATUS_NORMAL) {
					// GENERATE TECHNICAL ALARM CLEARED
					processTechnicalAlarmClear(lEquipStatus, lCurrentDate);
					schedulerConfig.setScssAlarmRaised(false);
					logger.info("SCSS Alarm Raised....." + scssAlarmRaised);
				}
			} else if (!scssAlarmRaised) {
				long dateTimeDiff = lCurrentDate.getTime()- lScssStatusTime.getTime();
				long waitTimeInterval = 3 * (pingInterval*1000);
				if (dateTimeDiff >= waitTimeInterval) {
					logger.info("Time Diff for SCSS Intf ....." + dateTimeDiff);
					logger.info("scssRetryCount :: " + scssRetryCount + " retryCount :: " + retryCount);
					if (scssRetryCount >= retryCount) {
						schedulerConfig.setScssAlarmRaised(true);
						schedulerConfig.setScssRetryCount(0);
						// GENERATE TECHNICAL ALARM RAISE
						processTechnicalAlarmRaise(lEquipStatus, lCurrentDate);
						logger.info("SCSS Alarm Raised....." + scssAlarmRaised);
					} else {
						schedulerConfig.setScssRetryCount(++scssRetryCount);
					}
				} else {
					schedulerConfig.setScssRetryCount(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error in updating SCSS Technical Alarm Raised/Cleared.....", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorIntfStatus :: monitorScssStatus .....");
		}
	}

	private void monitorEmasccsStatus() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR EMASCCS INTERFACE STATUS .....");
		}
		Date lCurrentDate = new Date();
		int pingInterval = schedulerConfig.getPingInterval();
		int retryCount = schedulerConfig.getRetryCount();
		boolean emasccsAlarmRaised = schedulerConfig.getEmasccsAlarmRaised();
		int emasccsRetryCount = schedulerConfig.getEmasccsRetryCount();

		logger.debug("retryCount :: " + retryCount);
		logger.debug("emasccsAlarmRaised :: " + emasccsAlarmRaised);
		logger.debug("emasccsRetryCount :: " + emasccsRetryCount);
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.EMASCCS_EQUIP_ID, Constants.OPE_STATE);
			int lEmasccsStatus = lEquipStatus.getStatus();
			Date lEmasccsStatusTime = lEquipStatus.getDateTime();

			if (emasccsAlarmRaised) {
				if (lEmasccsStatus == Constants.EQUIP_STATUS_NORMAL) {
					// GENERATE TECHNICAL ALARM CLEARED
					processTechnicalAlarmClear(lEquipStatus, lCurrentDate);
					schedulerConfig.setEmasccsAlarmRaised(false);
					logger.info("EMASCCS Alarm Raised....." + emasccsAlarmRaised);
				}
			} else if (!emasccsAlarmRaised) {
				long dateTimeDiff = lCurrentDate.getTime() - lEmasccsStatusTime.getTime();
				long waitTimeInterval = 3 * (pingInterval*1000);
				if (dateTimeDiff >= waitTimeInterval) {
					logger.info("Time Diff for EMASCCS ....." + dateTimeDiff);
					logger.info("emasccsRetryCount :: " + emasccsRetryCount + " retryCount :: " + retryCount);
					if (emasccsRetryCount >= retryCount) {
						schedulerConfig.setEmasccsAlarmRaised(true);
						schedulerConfig.setEmasccsRetryCount(0);
						// GENERATE TECHNICAL ALARM RAISE
						processTechnicalAlarmRaise(lEquipStatus, lCurrentDate);
						logger.info("EMASCCS Alarm Raised....." + emasccsAlarmRaised);
					} else {
						schedulerConfig.setEmasccsRetryCount(++emasccsRetryCount);
					}
				} else {
					schedulerConfig.setEmasccsRetryCount(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error in updating EMASCCS Technical Alarm Raised/Cleared.....", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorIntfStatus :: monitorEmasccsStatus .....");
		}
	}

	// Added 07/03/22 GH - To implement monitor to ITPT-INTF
	private void monitorEmasitptStatus() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR EMASITPT INTERFACE STATUS .....");
		}
		Date lCurrentDate = new Date();
		int pingInterval = schedulerConfig.getPingInterval();
		int retryCount = schedulerConfig.getRetryCount();
		boolean emasitptAlarmRaised = schedulerConfig.getEmasitptAlarmRaised();
		int emasitptRetryCount = schedulerConfig.getEmasitptRetryCount();

		logger.debug("retryCount :: " + retryCount);
		logger.debug("emasitptAlarmRaised :: " + emasitptAlarmRaised);
		logger.debug("emasitptRetryCount :: " + emasitptRetryCount);
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.EMASITPT_EQUIP_ID, Constants.OPE_STATE);
			int lEmasitptStatus = lEquipStatus.getStatus();
			Date lEmasitptStatusTime = lEquipStatus.getDateTime();

			if (emasitptAlarmRaised) {
				if (lEmasitptStatus == Constants.EQUIP_STATUS_NORMAL) {
					// GENERATE TECHNICAL ALARM CLEARED
					cmhMessageSender.setItptEquipStatus(Constants.EQUIP_STATUS_NORMAL);
					processTechnicalAlarmClear(lEquipStatus, lCurrentDate);
					schedulerConfig.setEmasitptAlarmRaised(false);
					logger.info("EMASITPT Alarm Raised....." + emasitptAlarmRaised);
				}
			} else if (!emasitptAlarmRaised) {
				long dateTimeDiff = lCurrentDate.getTime() - lEmasitptStatusTime.getTime();
				long waitTimeInterval = 3 * (pingInterval*1000);
				if (dateTimeDiff >= waitTimeInterval) {
					logger.info("Time Diff for EMASITPT ....." + dateTimeDiff);
					logger.info("emasitptRetryCount :: " + emasitptRetryCount + " retryCount :: " + retryCount);
					if (emasitptRetryCount >= retryCount) {
						schedulerConfig.setEmasitptAlarmRaised(true);
						schedulerConfig.setEmasitptRetryCount(0);
						// GENERATE TECHNICAL ALARM RAISE
						cmhMessageSender.setItptEquipStatus(Constants.EQUIP_STATUS_NG);
						processTechnicalAlarmRaise(lEquipStatus, lCurrentDate);
						logger.info("EMASITPT Alarm Raised....." + emasitptAlarmRaised);
					} else {
						schedulerConfig.setEmasitptRetryCount(++emasitptRetryCount);
					}
				} else {
					schedulerConfig.setEmasitptRetryCount(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error in updating EMASITPT Technical Alarm Raised/Cleared.....", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorIntfStatus :: monitorEmasitptStatus .....");
		}
	}

	// Added 07/03/22 GH - To implement monitor to TCTS-INTF
	private void monitorEmastctsStatus() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR EMASTCTS INTERFACE STATUS .....");
		}
		Date lCurrentDate = new Date();
		int pingInterval = schedulerConfig.getPingInterval();
		int retryCount = schedulerConfig.getRetryCount();
		boolean emastctsAlarmRaised = schedulerConfig.getEmastctsAlarmRaised();
		int emastctsRetryCount = schedulerConfig.getEmastctsRetryCount();

		logger.debug("retryCount :: " + retryCount);
		logger.debug("emastctsAlarmRaised :: " + emastctsAlarmRaised);
		logger.debug("emastctsRetryCount :: " + emastctsRetryCount);
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.EMASTCTS_EQUIP_ID, Constants.OPE_STATE);
			int lEmastctsStatus = lEquipStatus.getStatus();
			Date lEmastctsStatusTime = lEquipStatus.getDateTime();

			if (emastctsAlarmRaised) {
				if (lEmastctsStatus == Constants.EQUIP_STATUS_NORMAL) {
					// GENERATE TECHNICAL ALARM CLEARED
					processTechnicalAlarmClear(lEquipStatus, lCurrentDate);
					schedulerConfig.setEmastctsAlarmRaised(false);
					logger.info("EMASTCTS Alarm Raised....." + emastctsAlarmRaised);
				}
			} else if (!emastctsAlarmRaised) {
				long dateTimeDiff = lCurrentDate.getTime() - lEmastctsStatusTime.getTime();
				long waitTimeInterval = 3 * (pingInterval*1000);
				if (dateTimeDiff >= waitTimeInterval) {
					logger.info("Time Diff for EMASTCTS ....." + dateTimeDiff);
					logger.info("emastctsRetryCount :: " + emastctsRetryCount + " retryCount :: " + retryCount);
					if (emastctsRetryCount >= retryCount) {
						schedulerConfig.setEmastctsAlarmRaised(true);
						schedulerConfig.setEmastctsRetryCount(0);
						// GENERATE TECHNICAL ALARM RAISE
						processTechnicalAlarmRaise(lEquipStatus, lCurrentDate);
						logger.info("EMASTCTS Alarm Raised....." + emastctsAlarmRaised);
					} else {
						schedulerConfig.setEmastctsRetryCount(++emastctsRetryCount);
					}
				} else {
					schedulerConfig.setEmastctsRetryCount(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error in updating EMASTCTS Technical Alarm Raised/Cleared.....", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorIntfStatus :: monitorEmastctsStatus .....");
		}
	}

	private void monitorDcssStatus() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR DCSS INTERFACE STATUS .....");
		}
		Date lCurrentDate = new Date();
		int pingInterval = schedulerConfig.getPingInterval();
		int retryCount = schedulerConfig.getRetryCount();		
		boolean dcssAlarmRaised = schedulerConfig.getDcssAlarmRaised();
		int dcssRetryCount = schedulerConfig.getDcssRetryCount();

		logger.debug("retryCount :: " + retryCount);
		logger.debug("dcssAlarmRaised :: " + dcssAlarmRaised);
		logger.debug("dcssRetryCount :: " + dcssRetryCount);
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.DCSS_EQUIP_ID, Constants.OPE_STATE);
			int lDcssStatus = lEquipStatus.getStatus();
			Date lDcssStatusTime = lEquipStatus.getDateTime();

			if (dcssAlarmRaised) {
				if (lDcssStatus == Constants.EQUIP_STATUS_NORMAL) {
					//GENERATE TECHNICAL ALARM CLEARED
					processTechnicalAlarmClear(lEquipStatus, lCurrentDate);
					schedulerConfig.setDcssAlarmRaised(false);
					logger.info("DCSS Alarm Raised....." + dcssAlarmRaised);
				}
			} else if (!dcssAlarmRaised) {
				long dateTimeDiff = lCurrentDate.getTime() - lDcssStatusTime.getTime();
				long waitTimeInterval = 3 * (pingInterval*1000);
				if (dateTimeDiff >=  waitTimeInterval) {
					logger.info("Time Diff for DCSS Intf ....." + dateTimeDiff);
					logger.info("dcssRetryCount :: " + dcssRetryCount + " retryCount :: " + retryCount);
					if (dcssRetryCount >= retryCount) {
						schedulerConfig.setDcssAlarmRaised(true);
						schedulerConfig.setDcssRetryCount(0);
						//GENERATE TECHNICAL ALARM RAISE
						processTechnicalAlarmRaise(lEquipStatus, lCurrentDate);
						logger.info("DCSS Alarm Raised....." + dcssAlarmRaised);
					} else {
						schedulerConfig.setDcssRetryCount(++dcssRetryCount);
					}
				} else {
					schedulerConfig.setDcssRetryCount(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error in updating DCSS Technical Alarm Raised/Cleared.....", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorIntfStatus :: monitorDcssStatus .....");
		}
	}

	private void monitorWcssStatus() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR WCSS INTERFACE STATUS .....");
		}
		Date lCurrentDate = new Date();
		int pingInterval = schedulerConfig.getPingInterval();
		int retryCount = schedulerConfig.getRetryCount();		
		boolean wcssAlarmRaised = schedulerConfig.getWcssAlarmRaised();
		int wcssRetryCount = schedulerConfig.getWcssRetryCount();

		logger.debug("retryCount :: " + retryCount);
		logger.debug("wcssAlarmRaised :: " + wcssAlarmRaised);
		logger.debug("wcssRetryCount :: " + wcssRetryCount);
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.WCSS_EQUIP_ID, Constants.OPE_STATE);
			int lWcssStatus = lEquipStatus.getStatus();
			Date lWcssStatusTime = lEquipStatus.getDateTime();

			if (wcssAlarmRaised) {
				if (lWcssStatus == Constants.EQUIP_STATUS_NORMAL) {
					//GENERATE TECHNICAL ALARM CLEARED
					processTechnicalAlarmClear(lEquipStatus, lCurrentDate);
					schedulerConfig.setWcssAlarmRaised(false);
					logger.info("WCSS Alarm Raised....." + wcssAlarmRaised);
				}
			} else if (!wcssAlarmRaised) {
				long dateTimeDiff = lCurrentDate.getTime() - lWcssStatusTime.getTime();
				long waitTimeInterval = 3 * (pingInterval*1000);
				if (dateTimeDiff >=  waitTimeInterval) {
					logger.info("Time Diff for WCSS Intf ....." + dateTimeDiff);
					logger.info("wcssRetryCount :: " + wcssRetryCount + " retryCount :: " + retryCount);
					if (wcssRetryCount >= retryCount) {
						schedulerConfig.setWcssAlarmRaised(true);
						schedulerConfig.setWcssRetryCount(0);
						//GENERATE TECHNICAL ALARM RAISE
						processTechnicalAlarmRaise(lEquipStatus, lCurrentDate);
						logger.info("WCSS Alarm Raised....." + wcssAlarmRaised);
					} else {
						schedulerConfig.setWcssRetryCount(++wcssRetryCount);
					}
				} else {
					schedulerConfig.setWcssRetryCount(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error in updating WCSS Technical Alarm Raised/Cleared.....", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorIntfStatus :: monitorWcssStatus .....");
		}
	}

	private void monitorNmsStatus() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR NMS INTERFACE STATUS .....");
		}
		EquipStatus lEquipStatusOpeState = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.NMS_EQUIP_ID, Constants.OPE_STATE);

		Date lCurrentDate = new Date();
		int pingInterval = schedulerConfig.getPingInterval();
		int retryCount = schedulerConfig.getRetryCount();		
		boolean nmsAlarmRaised = schedulerConfig.getNmsAlarmRaised();
		int nmsRetryCount = schedulerConfig.getNmsRetryCount();

		logger.debug("retryCount :: " + retryCount);
		logger.debug("nmsAlarmRaised :: " + nmsAlarmRaised);
		logger.debug("nmsRetryCount :: " + nmsRetryCount);
		try {
			EquipStatus lEquipStatusSql = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.NMS_EQUIP_ID, Constants.NMS_ARGENT_SQL);
			int lNmsStatus = lEquipStatusSql.getStatus();
			Date lNmsStatusTime = lEquipStatusSql.getDateTime();
			if (nmsAlarmRaised) {
				logger.info("lNmsStatus :: " + lNmsStatus);
				if (lNmsStatus == Constants.EQUIP_STATUS_NORMAL) {
					//GENERATE TECHNICAL ALARM CLEARED
					List<EquipStatus> lEquipStatusList = cmhMonitor.getAllEquipStatusForNms();
					if (lEquipStatusList == null || lEquipStatusList.size() == 0) {
						logger.info("EquipStatusList is null or empty .....");
						processTechnicalAlarmClearForNwt(lEquipStatusOpeState, lCurrentDate);
						schedulerConfig.setNmsAlarmRaised(false);
						logger.info("NMS Alarm Raised....." + nmsAlarmRaised);
					} else {
						logger.info("EquipStatusList is not empty, There are still some alarm .....");
					}
				}
			} else if (!nmsAlarmRaised) {
				long dateTimeDiff = lCurrentDate.getTime() - lNmsStatusTime.getTime();
				long waitTimeInterval = 3 * (pingInterval*1000);
				if (dateTimeDiff >=  waitTimeInterval) {
					logger.info("Time Diff for NMS SQL ....." + dateTimeDiff);
					logger.info("nmsRetryCount :: " + nmsRetryCount + " retryCount :: " + retryCount);
					if (nmsRetryCount >= retryCount) {
						schedulerConfig.setNmsAlarmRaised(true);
						schedulerConfig.setNmsRetryCount(0);
						//GENERATE TECHNICAL ALARM RAISE
						processTechnicalAlarmRaiseForNwt(lEquipStatusSql, lEquipStatusOpeState, lCurrentDate);
						logger.info("NMS Alarm Raised....." + nmsAlarmRaised);
					} else {
						schedulerConfig.setNmsRetryCount(++nmsRetryCount);
					}
				} else {
					schedulerConfig.setNmsRetryCount(0);
				}
			}
			logger.debug("NMS Alarm Raised....." + schedulerConfig.getNmsAlarmRaised());
			if (!schedulerConfig.getNmsAlarmRaised()) {
				boolean lIsNmsDown = false;
				List<EquipStatus> lEquipStatusList = cmhMonitor.getAllEquipStatusForNms();
				if (lEquipStatusList != null && lEquipStatusList.size() > 0) {
					lIsNmsDown = true;
				}
				if (lIsNmsDown) {
					//ALARM_RAISE
					logger.info("lIsNmsDown :: " + lIsNmsDown);
					processTechnicalAlarmRaise(lEquipStatusOpeState, lCurrentDate);
					schedulerConfig.setNmsAlarmRaised(true);
				} //else { NO NEED ELSE, CLEAR IS DONE, AFTER CHECKING ALL THE NMS STATUS
			}
		} catch (Exception e) {
			logger.error("Error in updating Nms Technical Alarm Raised/Cleared.....", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorIntfStatus :: monitorNmsStatus .....");
		}
	}

	private void monitorNwt1Status() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR NWT1 INTERFACE STATUS .....");
		}
		Date lCurrentDate = new Date();
		int retryCount = schedulerConfig.getRetryCount();
		boolean nwt1AlarmRaised = schedulerConfig.getNwt1AlarmRaised();
		int nwt1RetryCount = schedulerConfig.getNwt1RetryCount();

		logger.debug("retryCount :: " + retryCount);
		logger.debug("nwt1AlarmRaised :: " + nwt1AlarmRaised);
		logger.debug("nwt1RetryCount :: " + nwt1RetryCount);
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.NWT1_EQUIP_ID, Constants.OPE_STATE);
			logger.debug("NWT1 Ip from properties :: " + schedulerConfig.getNwt1Ip());

			InetAddress lNwt1 = InetAddress.getByName(schedulerConfig.getNwt1Ip());
			boolean lIsNwt1Reachable = lNwt1.isReachable(10000);

			if (!lIsNwt1Reachable && !nwt1AlarmRaised) {
				logger.info("lIsNwt1Reachable :: " + lIsNwt1Reachable);
				logger.info("nwt1RetryCount :: " + nwt1RetryCount + " retryCount :: " + retryCount);
				if (nwt1RetryCount >= retryCount) {
					schedulerConfig.setNwt1AlarmRaised(true);
					schedulerConfig.setNwt1RetryCount(0);
					//GENERATE TECHNICAL ALARM RAISE
					processTechnicalAlarmRaise(lEquipStatus, lCurrentDate);
					logger.info("NWT1 Alarm Raised....." + nwt1AlarmRaised);
				} else {
					schedulerConfig.setNwt1RetryCount(++nwt1RetryCount);
				}
			} else if (lIsNwt1Reachable && nwt1AlarmRaised) {
				//GENERATE TECHNICAL ALARM CLEARED
				processTechnicalAlarmClearForNwt(lEquipStatus, lCurrentDate);
				schedulerConfig.setNwt1AlarmRaised(false);
				logger.info("NWT1 Alarm Raised....." + nwt1AlarmRaised);
			}
		} catch (UnknownHostException e) {
			logger.error("Problem in pinging NWT_01 :: ", e);
		} catch (IOException e) {
			logger.error("Problem in pinging NWT_01 :: ", e);
		} catch (Exception e) {
			logger.error("Problem in pinging NWT_01 :: ", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorIntfStatus :: monitorNwt1Status .....");
		}
	}

	private void monitorNwt2Status() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR NWT2 INTERFACE STATUS .....");
		}
		Date lCurrentDate = new Date();
		int retryCount = schedulerConfig.getRetryCount();
		boolean nwt2AlarmRaised = schedulerConfig.getNwt2AlarmRaised();
		int nwt2RetryCount = schedulerConfig.getNwt2RetryCount();

		logger.debug("retryCount :: " + retryCount);
		logger.debug("nwt2AlarmRaised :: " + nwt2AlarmRaised);
		logger.debug("nwt2RetryCount :: " + nwt2RetryCount);
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.NWT2_EQUIP_ID, Constants.OPE_STATE);
			logger.debug("NWT2 Ip from properties :: " + schedulerConfig.getNwt2Ip());

			InetAddress lNwt2 = InetAddress.getByName(schedulerConfig.getNwt2Ip());
			boolean lIsNwt2Reachable = lNwt2.isReachable(10000);			

			if (!lIsNwt2Reachable && !nwt2AlarmRaised) {
				logger.info("lIsNwt2Reachable :: " + lIsNwt2Reachable);
				logger.info("nwt2RetryCount :: " + nwt2RetryCount + " retryCount :: " + retryCount);
				if (nwt2RetryCount >= retryCount) {
					schedulerConfig.setNwt2AlarmRaised(true);
					schedulerConfig.setNwt2RetryCount(0);
					//GENERATE TECHNICAL ALARM RAISE
					processTechnicalAlarmRaise(lEquipStatus, lCurrentDate);
					logger.info("NWT2 Alarm Raised....." + nwt2AlarmRaised);
				} else {
					schedulerConfig.setNwt2RetryCount(++nwt2RetryCount);
				}
			} else if (lIsNwt2Reachable && nwt2AlarmRaised) {
				//GENERATE TECHNICAL ALARM CLEARED
				processTechnicalAlarmClearForNwt(lEquipStatus, lCurrentDate);
				schedulerConfig.setNwt2AlarmRaised(false);
				logger.info("NWT2 Alarm Raised....." + nwt2AlarmRaised);
			}
		} catch (UnknownHostException e) {
			logger.error("Problem in pinging NWT_02 :: ", e);
		} catch (IOException e) {
			logger.error("Problem in pinging NWT_02 :: ", e);
		} catch (Exception e) {
			logger.error("Problem in pinging NWT_02 :: ", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorIntfStatus :: monitorNwt2Status .....");
		}
	}

	public void processTechnicalAlarmRaiseForNwt(EquipStatus pEquipStatusSql, EquipStatus pEquipStatusOpeState, Date pCurrentDate) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("Inside MonitorIntfStatus :: Calling processTechnicalAlarmRaise .....");
		}
		//updating status and time for Equip Id - nms_01 and status_code -> argentSql
		pEquipStatusSql.setStatus(Constants.EQUIP_STATUS_NG);
		cmhMonitor.updateEquipStatus(pEquipStatusSql);

		processTechnicalAlarmRaise(pEquipStatusOpeState, pCurrentDate);
	}

	public void processTechnicalAlarmRaise(EquipStatus pEquipStatus, Date pCurrentDate) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("Inside MonitorIntfStatus :: Calling processTechnicalAlarmRaise .....");
		}
		EquipConfig lEquipConfig = pEquipStatus.getEquipConfig();

		pEquipStatus.setStatus(Constants.EQUIP_STATUS_NG);
		pEquipStatus.setDateTime(pCurrentDate);
		cmhMonitor.updateEquipStatus(pEquipStatus);

		// GENERATE TECHNICAL ALARM RAISED
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

		// SENDING TECHNICAL ALARM TO QUEUE EMASCCS & AW
		List<TechnicalAlarmDto> lAlarmList = new ArrayList<TechnicalAlarmDto>();
		lAlarmList.add(lTechAlarmDto);
		TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
		lTechAlarmDtoList.setDtoList(lAlarmList);
		logger.info("Object sent to Queue " + lTechAlarmDtoList);
		cmhMessageSender.sendAWJmsMessage("TechnicalAlarm", MessageConstants.TECH_ALARM_ID);
		if ((!pEquipStatus.getEquipConfig().getEquipId().equals(Constants.EMASCCS_EQUIP_ID))
				|| (!pEquipStatus.getEquipConfig().getEquipId().equals(Constants.EMASITPT_EQUIP_ID))
				|| (!pEquipStatus.getEquipConfig().getEquipId().equals(Constants.EMASTCTS_EQUIP_ID))) {
			cmhMessageSender.sendEmasCcsJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
		}
		// SENDING EQUIP_STATUS_LIST TO AW & ITPT QUEUE
		EquipStatusDto lEquipStatusDto = DTOConverter.convert(EquipStatusDto.class, pEquipStatus);
		lEquipStatusDto.setEquipId(lEquipConfig.getEquipId());

		List<EquipStatusDto> lEquipStatusList = new ArrayList<EquipStatusDto>();
		lEquipStatusList.add(lEquipStatusDto);
		EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList(lEquipStatusList);
		logger.info("Object sent to Queue " + lEquipStatusDtoList);
		cmhMessageSender.sendAWJmsMessage("EquipStatus", MessageConstants.EQUIP_STATUS_ID);
		if ((!pEquipStatus.getEquipConfig().getEquipId().equals(Constants.EMASCCS_EQUIP_ID))
				|| (!pEquipStatus.getEquipConfig().getEquipId().equals(Constants.EMASITPT_EQUIP_ID))
				|| (!pEquipStatus.getEquipConfig().getEquipId().equals(Constants.EMASTCTS_EQUIP_ID))) {
			cmhMessageSender.sendEmasCcsJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
		}
	}

	public void processTechnicalAlarmClear(EquipStatus pEquipStatus, Date pCurrentDate) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("Inside MonitorIntfStatus :: Calling processTechnicalAlarmClear .....");
		}
		String lAlarmId = CommonUtil.generateAlarmId(Constants.SYSTEM_ID, pEquipStatus.getEquipConfig().getEquipId(), Constants.LINK_DOWN);

		TechnicalAlarm lTechnicalAlarmDB = cmhMonitor.findTechnicalAlarmById(lAlarmId);
		logger.info("Technical Alarm Cleared for " + lAlarmId);
		cmhMonitor.processTechAlarmClearMonitor(lTechnicalAlarmDB, pCurrentDate);

		TechnicalAlarmDto lTechnicalAlarmDto = DTOConverter.convert(TechnicalAlarmDto.class, lTechnicalAlarmDB);
		/***** Changed by Grace 18/12/19 *****/
		//lTechnicalAlarmDto.setEquipId(lTechnicalAlarmDB.getEquipConfig().getEquipId());
		lTechnicalAlarmDto.setEquipId(lTechnicalAlarmDB.getEquipId());
		/*************************************/
		lTechnicalAlarmDto.setStartDate(pCurrentDate);
		lTechnicalAlarmDto.setStatus(Constants.ALARM_CLEARED);

		// SENDING TECH_ALARM_LIST TO AW & EMASCCS
		List<TechnicalAlarmDto> lAlarmList = new ArrayList<TechnicalAlarmDto>();
		lAlarmList.add(lTechnicalAlarmDto);
		TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
		lTechAlarmDtoList.setDtoList(lAlarmList);

		logger.info("Object sent to Queue " + lTechAlarmDtoList);
		cmhMessageSender.sendAWJmsMessage("TechnicalAlarm", MessageConstants.TECH_ALARM_ID);
		if ((!pEquipStatus.getEquipConfig().getEquipId().equals(Constants.EMASCCS_EQUIP_ID))
				|| (!pEquipStatus.getEquipConfig().getEquipId().equals(Constants.EMASITPT_EQUIP_ID))
				|| (!pEquipStatus.getEquipConfig().getEquipId().equals(Constants.EMASTCTS_EQUIP_ID))) {
			cmhMessageSender.sendEmasCcsJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
		}

		// GENERATE EQUIP STATUS FOR TECHNICAL ALARM CLEAR
		// SENDING EQUIP_STATUS_LIST TO AW QUEUE
		EquipStatusDto lEquipStatusDto = DTOConverter.convert(EquipStatusDto.class, pEquipStatus);
		lEquipStatusDto.setEquipId(pEquipStatus.getEquipConfig().getEquipId());

		List<EquipStatusDto> lStatusList = new ArrayList<EquipStatusDto>();
		lStatusList.add(lEquipStatusDto);
		EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList(lStatusList);

		logger.info("Object sent to Queue " + lEquipStatusDtoList);
		cmhMessageSender.sendAWJmsMessage("EquipStatus", MessageConstants.EQUIP_STATUS_ID);
		if ((!pEquipStatus.getEquipConfig().getEquipId().equals(Constants.EMASCCS_EQUIP_ID))
				|| (!pEquipStatus.getEquipConfig().getEquipId().equals(Constants.EMASITPT_EQUIP_ID))
				|| (!pEquipStatus.getEquipConfig().getEquipId().equals(Constants.EMASTCTS_EQUIP_ID))) {
			cmhMessageSender.sendEmasCcsJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit MonitorIntfStatus :: Calling processTechnicalAlarmClear .....");
		}
	}

	public void processTechnicalAlarmClearForNwt(EquipStatus pEquipStatus, Date pCurrentDate) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("Inside MonitorIntfStatus :: Calling processTechnicalAlarmClear .....");
		}

		EquipConfig lEquipConfig = pEquipStatus.getEquipConfig();

		pEquipStatus.setStatus(Constants.EQUIP_STATUS_NORMAL);
		pEquipStatus.setDateTime(pCurrentDate);
		cmhMonitor.updateEquipStatus(pEquipStatus);

		String lAlarmId = CommonUtil.generateAlarmId(Constants.SYSTEM_ID, lEquipConfig.getEquipId(), Constants.LINK_DOWN);

		TechnicalAlarm lTechnicalAlarmDB = cmhMonitor.findTechnicalAlarmById(lAlarmId);
		logger.info("Technical Alarm Cleared for " + lAlarmId);
		cmhMonitor.processTechAlarmClearMonitor(lTechnicalAlarmDB, pCurrentDate);

		TechnicalAlarmDto lTechnicalAlarmDto = DTOConverter.convert(TechnicalAlarmDto.class, lTechnicalAlarmDB);
		/***** Changed by Grace 18/12/19 *****/
		//lTechnicalAlarmDto.setEquipId(lTechnicalAlarmDB.getEquipConfig().getEquipId());
		lTechnicalAlarmDto.setEquipId(lTechnicalAlarmDB.getEquipId());
		/*************************************/
		lTechnicalAlarmDto.setStartDate(pCurrentDate);
		lTechnicalAlarmDto.setStatus(Constants.ALARM_CLEARED);

		// SENDING TECH_ALARM_LIST TO AW & EMASCCS
		List<TechnicalAlarmDto> lAlarmList = new ArrayList<TechnicalAlarmDto>();
		lAlarmList.add(lTechnicalAlarmDto);
		TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
		lTechAlarmDtoList.setDtoList(lAlarmList);

		logger.info("Object sent to Queue " + lTechAlarmDtoList);
		cmhMessageSender.sendAWJmsMessage("TechnicalAlarm", MessageConstants.TECH_ALARM_ID);
		cmhMessageSender.sendEmasCcsJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
		// Added 04/03/22 GH - To implement new queues to ITPT-INTF
		cmhMessageSender.sendEmasItptJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);

		// GENERATE EQUIP STATUS FOR TECHNICAL ALARM CLEAR
		// SENDING EQUIP_STATUS_LIST TO AW QUEUE
		EquipStatusDto lEquipStatusDto = DTOConverter.convert(EquipStatusDto.class, pEquipStatus);
		lEquipStatusDto.setEquipId(pEquipStatus.getEquipConfig().getEquipId());

		List<EquipStatusDto> lStatusList = new ArrayList<EquipStatusDto>();
		lStatusList.add(lEquipStatusDto);
		EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList(lStatusList);

		logger.info("Object sent to Queue " + lEquipStatusDtoList);
		cmhMessageSender.sendAWJmsMessage("EquipStatus", MessageConstants.EQUIP_STATUS_ID);
		cmhMessageSender.sendEmasCcsJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
		// Added 04/03/22 GH - To implement new queues to ITPT-INTF
		cmhMessageSender.sendEmasItptJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
		
		if (logger.isInfoEnabled()) {
			logger.info("Exit MonitorIntfStatus :: Calling processTechnicalAlarmClear .....");
		}
	}
}