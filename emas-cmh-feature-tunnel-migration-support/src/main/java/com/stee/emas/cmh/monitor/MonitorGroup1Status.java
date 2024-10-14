/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.monitor;

import java.util.Date;

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

import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.entity.EquipStatus;
import com.stee.emas.ctetun.constants.CTETunConstants;

/**
 * 
 * @author Scindia
 * @since May 30, 2016
 * @version 1.0
 *
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component("monitorGroup1Status")
public class MonitorGroup1Status implements Job {

	private static Logger logger = LoggerFactory.getLogger(MonitorGroup1Status.class);

	private SchedulerConfigGroup1 schedulerConfigGroup1 = null;

	@Autowired
	MonitorIntfStatus monitorIntfStatus;
	@Autowired
	CmhMonitor cmhMonitor;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SchedulerContext schedulerContext;
		JobDataMap dataMap = null;
		try {
			schedulerContext = context.getScheduler().getContext();
			ApplicationContext applicationContext = (ApplicationContext) schedulerContext.get("CMH-Context");
			cmhMonitor = (CmhMonitor) applicationContext.getBean("cmhMonitor");
			monitorIntfStatus = (MonitorIntfStatus) applicationContext.getBean("monitorIntfStatus");
			dataMap = context.getJobDetail().getJobDataMap();
			schedulerConfigGroup1 = (SchedulerConfigGroup1) dataMap.get("SCHEDULER_CONFIG_GROUP1");
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		monitor();
	}
	
	public void monitor() {
		monitorLusStatus();
	}

	private void monitorLusStatus() {
		if (logger.isInfoEnabled()) {
			logger.info("MONITOR LUS STATUS .....");
		}
		Date lCurrentDate = new Date();
		int pingInterval = schedulerConfigGroup1.getPingInterval();
		int retryCount = schedulerConfigGroup1.getRetryCount();		
		boolean lusAlarmRaised = schedulerConfigGroup1.getLusAlarmRaised();
		int lusRetryCount = schedulerConfigGroup1.getLusRetryCount();
		
		logger.debug("retryCount :: " + retryCount);
		logger.debug("lusAlarmRaised :: " + lusAlarmRaised);
		logger.debug("lusRetryCount :: " + lusRetryCount);
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(CTETunConstants.LUS_EQUIP_ID, Constants.OPE_STATE);
			int lLusStatus = lEquipStatus.getStatus();
			Date lLusStatusTime = lEquipStatus.getDateTime();
			
			if (lusAlarmRaised) {
				if (lLusStatus == Constants.EQUIP_STATUS_NORMAL) {
					//GENERATE TECHNICAL ALARM CLEARED
					monitorIntfStatus.processTechnicalAlarmClear(lEquipStatus, lCurrentDate);
					schedulerConfigGroup1.setLusAlarmRaised(false);
					logger.info("LUS Alarm Raised....." + lusAlarmRaised);
				}
			} else if (!lusAlarmRaised) {
				long dateTimeDiff = lCurrentDate.getTime() - lLusStatusTime.getTime();
				long waitTimeInterval = 3 * (pingInterval*1000);
				if (dateTimeDiff >=  waitTimeInterval) {
					logger.info("Time Diff for LUS ....." + dateTimeDiff);
					logger.info("lusRetryCount :: " + lusRetryCount + " retryCount :: " + retryCount);
					if (lusRetryCount >= retryCount) {
						schedulerConfigGroup1.setLusAlarmRaised(true);
						schedulerConfigGroup1.setLusRetryCount(0);
						//GENERATE TECHNICAL ALARM RAISE
						monitorIntfStatus.processTechnicalAlarmRaise(lEquipStatus, lCurrentDate);
						logger.info("LUS Alarm Raised....." + lusAlarmRaised);
					} else {
						schedulerConfigGroup1.setLusRetryCount(++lusRetryCount);
					}
				} else {
					schedulerConfigGroup1.setLusRetryCount(0);
				}
			}
		} catch (Exception e) {
			logger.error("Error in updating LUS Technical Alarm Raised/Cleared.....", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit MonitorGroup1Status :: monitorLUSStatus .....");
		}
	}
}