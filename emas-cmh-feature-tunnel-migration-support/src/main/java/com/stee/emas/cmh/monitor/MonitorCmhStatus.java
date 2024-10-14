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
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.entity.EquipStatus;


/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Quartz Job class for Self(CMH) Monitoring</p>
 * <p>This class is used for monitoring CMH Self Status
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Apr 30, 2013
 * @version 1.0
 *
 */
@DisallowConcurrentExecution
@Component("monitorCmhStatus")
public class MonitorCmhStatus implements Job {

	private static Logger logger = LoggerFactory.getLogger(MonitorCmhStatus.class);
	
	@Autowired
	CmhMonitor cmhMonitor;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SchedulerContext schedulerContext;
		try {
			schedulerContext = context.getScheduler().getContext();
			ApplicationContext applicationContext= (ApplicationContext)schedulerContext.get("CMH-Context");
			cmhMonitor =  (CmhMonitor)applicationContext.getBean("cmhMonitor");
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		monitorSelfStatus();
	}
	
	public void monitorSelfStatus() {
		try {
			EquipStatus lEquipStatus = cmhMonitor.findEquipStatusByEquipIdAndStatusCode(Constants.CMH_EQUIP_ID, Constants.OPE_STATE);
			lEquipStatus.setStatus(Constants.EQUIP_STATUS_NORMAL);
			lEquipStatus.setDateTime(new Date());
			cmhMonitor.updateEquipStatus(lEquipStatus);
			logger.info("Equip Status for CMH updated successfully ....." + new Date());
		} catch (Exception e) {
			logger.error("Exception in updating EquipStatus for CMH ", e);
		}
	}
}