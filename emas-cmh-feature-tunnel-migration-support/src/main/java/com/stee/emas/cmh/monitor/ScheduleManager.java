package com.stee.emas.cmh.monitor;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stee.emas.cmh.service.TechnicalAlarmManager;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.entity.SystemParameter;
import com.stee.emas.common.entity.TechnicalAlarm;
import com.stee.emas.common.entity.UpsArgentStatus;
import com.stee.emas.common.util.CommonUtil;
import com.stee.emas.ctetun.constants.CTETunConstants;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Scheduler class for CMH</p>
 * <p>This class is used for scheduling required Jobs
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Apr 30, 2013
 * @version 1.0
 *
 */
@Component
public class ScheduleManager {

	private static Logger logger = LoggerFactory.getLogger(ScheduleManager.class);
	
	@Autowired
	Scheduler scheduler;
	@Autowired
	CmhMonitor cmhMonitor;
	@Autowired
	TechnicalAlarmManager technicalAlarmManager;
	@Autowired
	MonitorUpsStatus monitorUpsStatus;	
	
	@Value("${nms.nwt1Ip}")
	private String nwt1Ip;
	@Value("${nms.nwt2Ip}")
	private String nwt2Ip;
	
	public void init() {
		SystemParameter lSystemParameter = cmhMonitor.findSystemParameterByName("PING_INTERVAL");
		int lPingInterval = Integer.parseInt(lSystemParameter.getValue());
		
		scheduleSelfJob(MonitorCmhStatus.class, "cmhStatus", lPingInterval);
		scheduleIntfJob(MonitorIntfStatus.class, "intfStatus", lPingInterval);
		scheduleGroup1Job(MonitorGroup1Status.class, "group1Status", lPingInterval);
		scheduleNetworkJob(MonitorNetworkStatus.class, "networkStatus", lPingInterval);
		scheduleUpsJob(MonitorUpsStatus.class, "upsStatus", lPingInterval);
		
		//schedulePreviewImage(PreviewImageExecutor.class, "previewImage", 1);
	}
	
	public void scheduleSelfJob(Class<? extends Job> pJobClassName, String pJobId, int pPingInterval) {
		logger.info("Initialize Monitor Self Status .....");
		try {
			JobDetail jobDetail = JobBuilder.newJob(pJobClassName).withIdentity(pJobId).build();
			Trigger trigger = TriggerBuilder.newTrigger().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(pPingInterval).repeatForever()).build();
			
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void scheduleIntfJob(Class<? extends Job> pJobClassName, String pJobId, int pPingInterval) {
		logger.info("Initialize Monitor Interface Status .....");
		try {
			JobDetail jobDetail = JobBuilder.newJob(pJobClassName).withIdentity(pJobId).build();
			initialize(jobDetail.getJobDataMap(), pPingInterval);
			Trigger trigger = TriggerBuilder.newTrigger().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(pPingInterval).repeatForever()).build();
			
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void scheduleGroup1Job(Class<? extends Job> pJobClassName, String pJobId, int pPingInterval) {
		logger.info("Initialize Group1 Status .....");
		try {
			JobDetail jobDetail = JobBuilder.newJob(pJobClassName).withIdentity(pJobId).build();
			initializeGroup1FELS(jobDetail.getJobDataMap(), pPingInterval);
			Trigger trigger = TriggerBuilder.newTrigger().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(pPingInterval).repeatForever()).build();
			
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void scheduleNetworkJob(Class<? extends Job> pJobClassName, String pJobId, int pPingInterval) {
		logger.info("Initialize Monitor Network Status .....");
		try {
			JobDetail jobDetail = JobBuilder.newJob(pJobClassName).withIdentity(pJobId).build();
			initializeNetworkStatus(jobDetail.getJobDataMap(), pPingInterval);
			Trigger trigger = TriggerBuilder.newTrigger().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(pPingInterval).repeatForever()).build();
			
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void scheduleUpsJob(Class<? extends Job> pJobClassName, String pJobId, int pPingInterval) {
		logger.info("Initialize Monitor Ups Status .....");
		try {
			JobDetail jobDetail = JobBuilder.newJob(pJobClassName).withIdentity(pJobId).build();
			initializeUpsStatus(jobDetail.getJobDataMap(), pPingInterval);
			
			Trigger trigger = TriggerBuilder.newTrigger().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(pPingInterval).repeatForever()).build();
			
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*public void schedulePreviewImage(Class<? extends Job> pJobClassName, String pJobId, int pPingInterval) {
		logger.info("Initialize PreviewImageExecutor .....");
		try {
			JobDetail jobDetail = JobBuilder.newJob(pJobClassName).withIdentity(pJobId).build();
			Trigger trigger = TriggerBuilder.newTrigger().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(pPingInterval).repeatForever()).build();
			
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public void initialize(JobDataMap pJobDataMap, int pPingInterval) {
		try {
			SystemParameter lSystemParameterRetryCount = cmhMonitor.findSystemParameterByName("RETRY_COUNT");
			int retryCount = Integer.parseInt(lSystemParameterRetryCount.getValue());
			boolean ticssAlarmRaised = false;
			TechnicalAlarm lTechAlarmObj_TICSS = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, Constants.TICSS_EQUIP_ID, Constants.LINK_DOWN));
			if (lTechAlarmObj_TICSS == null) {
				ticssAlarmRaised = false;
			} else {
				ticssAlarmRaised = true;
			}
			boolean scssAlarmRaised = false;
			TechnicalAlarm lTechAlarmObj_SCSS = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, Constants.SCSS_SYSTEM_NAME, Constants.LINK_DOWN));
			if (lTechAlarmObj_SCSS == null) {
				scssAlarmRaised = false;
			} else {
				scssAlarmRaised = true;
			}
			boolean emasccsAlarmRaised = false;
			TechnicalAlarm lTechAlarmObj_EMASCCS = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, Constants.EMASCCS_EQUIP_ID, Constants.LINK_DOWN));
			if (lTechAlarmObj_EMASCCS == null) {
				emasccsAlarmRaised = false;
			} else {
				emasccsAlarmRaised = true;
			}
			boolean dcssAlarmRaised = false;
			TechnicalAlarm lTechAlarmObj_DCSS = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, Constants.DCSS_EQUIP_ID, Constants.LINK_DOWN));
			if (lTechAlarmObj_DCSS == null) {
				dcssAlarmRaised = false;
			} else {
				dcssAlarmRaised = true;
			}
			boolean wcssAlarmRaised = false;
			TechnicalAlarm lTechAlarmObj_WCSS = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, Constants.WCSS_EQUIP_ID, Constants.LINK_DOWN));
			if (lTechAlarmObj_WCSS == null) {
				wcssAlarmRaised = false;
			} else {
				wcssAlarmRaised = true;
			}
			boolean nmsAlarmRaised = false;
			TechnicalAlarm lTechAlarmObj_NMS = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, Constants.NMS_EQUIP_ID, Constants.LINK_DOWN));
			if (lTechAlarmObj_NMS == null) {
				nmsAlarmRaised = false;
			} else {
				nmsAlarmRaised = true;
			}
			boolean nwt1AlarmRaised = false;
			TechnicalAlarm lTechAlarmObj_NWT1 = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, Constants.NWT1_EQUIP_ID, Constants.LINK_DOWN));
			if (lTechAlarmObj_NWT1 == null) {
				nwt1AlarmRaised = false;
			} else {
				nwt1AlarmRaised = true;
			}
			boolean nwt2AlarmRaised = false;
			TechnicalAlarm lTechAlarmObj_NWT2 = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, Constants.NWT2_EQUIP_ID, Constants.LINK_DOWN));
			if (lTechAlarmObj_NWT2 == null) {
				nwt2AlarmRaised = false;
			} else {
				nwt2AlarmRaised = true;
			}
			boolean emasitptAlarmRaised = false;
			TechnicalAlarm lTechAlarmObj_EMASITPT = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, Constants.EMASITPT_EQUIP_ID, Constants.LINK_DOWN));
			if (lTechAlarmObj_EMASITPT == null) {
				emasitptAlarmRaised = false;
			} else {
				emasitptAlarmRaised = true;
			}
			boolean emastctsAlarmRaised = false;
			TechnicalAlarm lTechAlarmObj_EMASTCTS = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, Constants.EMASTCTS_EQUIP_ID, Constants.LINK_DOWN));
			if (lTechAlarmObj_EMASTCTS == null) {
				emastctsAlarmRaised = false;
			} else {
				emastctsAlarmRaised = true;
			}
			boolean emasidssAlarmRaised = false;
			TechnicalAlarm lTechAlarmObj_IDSS = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, Constants.IDSS_EQUIP_ID, Constants.LINK_DOWN));
			if (lTechAlarmObj_IDSS == null) {
				emasidssAlarmRaised = false;
			} else {
				emasidssAlarmRaised = true;
			}

			
			SchedulerConfig schedulerConfig = SchedulerConfig.getInstance();
			schedulerConfig.setPingInterval(pPingInterval);
			schedulerConfig.setRetryCount(retryCount);
			schedulerConfig.setNwt1Ip(nwt1Ip);
			schedulerConfig.setNwt2Ip(nwt2Ip);
			
			schedulerConfig.setTicssAlarmRaised(ticssAlarmRaised);
			schedulerConfig.setScssAlarmRaised(scssAlarmRaised);
			schedulerConfig.setEmasccsAlarmRaised(emasccsAlarmRaised);
			schedulerConfig.setDcssAlarmRaised(dcssAlarmRaised);
			schedulerConfig.setWcssAlarmRaised(wcssAlarmRaised);
			schedulerConfig.setNmsAlarmRaised(nmsAlarmRaised);
			schedulerConfig.setNwt1AlarmRaised(nwt1AlarmRaised);
			schedulerConfig.setNwt2AlarmRaised(nwt2AlarmRaised);
			
			schedulerConfig.setIdssAlarmRaised(emasidssAlarmRaised);
			schedulerConfig.setEmasitptAlarmRaised(emasitptAlarmRaised);
			schedulerConfig.setEmastctsAlarmRaised(emastctsAlarmRaised);
			
			pJobDataMap.put("SCHEDULER_CONFIG", schedulerConfig);
		} catch (Exception e) {
			logger.error("Error in initializing Interface Status :: ", e);
		}		
	}
	
	public void initializeGroup1FELS(JobDataMap pJobDataMap, int pPingInterval) {
		try {
			SystemParameter lSystemParameterRetryCount = cmhMonitor.findSystemParameterByName("RETRY_COUNT");
			int retryCount = Integer.parseInt(lSystemParameterRetryCount.getValue());
			boolean lusAlarmRaised = false;
			TechnicalAlarm lTechAlarmObj_LUS = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, CTETunConstants.LUS_EQUIP_ID, Constants.LINK_DOWN));
			if (lTechAlarmObj_LUS == null) {
				lusAlarmRaised = false;
			} else {
				lusAlarmRaised = true;
			}
			
			SchedulerConfigGroup1 schedulerConfigGroup1 = SchedulerConfigGroup1.getInstance();
			schedulerConfigGroup1.setPingInterval(pPingInterval);
			schedulerConfigGroup1.setRetryCount(retryCount);
			
			schedulerConfigGroup1.setLusAlarmRaised(lusAlarmRaised);
			pJobDataMap.put("SCHEDULER_CONFIG_GROUP1", schedulerConfigGroup1);
		} catch (Exception e) {
			logger.error("Error in initializing Group 1 FELS Status :: ", e);
		}		
	}
	
	public void initializeNetworkStatus(JobDataMap pJobDataMap, int pPingInterval) {
		try {
			Map<String, Integer> pingStateMap	= new TreeMap<String, Integer>();
			List<String> lEquipIdList =  cmhMonitor.getAllNetworkEquipments();
			SchedulerConfigNetwork schedulerConfigNetwork = SchedulerConfigNetwork.getInstance();
			schedulerConfigNetwork.setEquipIdList(lEquipIdList);
			schedulerConfigNetwork.setPingInterval(pPingInterval);
			
			for (String lEquipId : lEquipIdList) {
				TechnicalAlarm lTechAlarmObj = technicalAlarmManager.findTechnicalAlarmById(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, lEquipId, Constants.LINK_DOWN));
				if (lTechAlarmObj == null) {
					pingStateMap.put(lEquipId, Constants.EQUIP_STATUS_NORMAL);
				} else {
					pingStateMap.put(lEquipId, Constants.EQUIP_STATUS_NG);
				}
			}
			logger.debug("pingStateMap :: " + pingStateMap);
			schedulerConfigNetwork.setPingStateMap(pingStateMap);
			pJobDataMap.put("SCHEDULER_CONFIG_NETWORK", schedulerConfigNetwork);
		} catch (Exception e) {
			logger.error("Error in initializing Network Status :: ", e);
		}
	}
	
	public void initializeUpsStatus(JobDataMap pJobDataMap, int pPingInterval) {
		try {			
			
			Map<String, Integer> upsMap	= new TreeMap<String, Integer>();//EquipId_AlarmCode, Status
			//Map<String, Map<Integer, Integer>> upsMap 	= new TreeMap<String, Map<Integer, Integer>>();//EquipId, Map
			//Map<Integer, Integer> upsStateMap	= new TreeMap<Integer, Integer>();//AlarmCode, Status
			List<UpsArgentStatus> lUpsArgentStatusList = cmhMonitor.getStatusForOtherMultipleAlarmsForInit();
			for (UpsArgentStatus lUpsArgentStatus : lUpsArgentStatusList) {
				String lEquipId = lUpsArgentStatus.getEquipId();				
				logger.info("EquipId :: " + lEquipId);
				TechnicalAlarm lTechnicalAlarm = technicalAlarmManager.getTechnicalAlarmByEquipIdAndAlarmCode(lEquipId, lUpsArgentStatus.getAlarmCode());
				logger.info("Alarm Code :: " + lUpsArgentStatus.getAlarmCode());
				if (lUpsArgentStatus.getStatus() == Constants.EQUIP_STATUS_NORMAL) {					
					if (lTechnicalAlarm != null) {
						monitorUpsStatus.processTechnicalAlarmClear(lUpsArgentStatus);						
					}
					upsMap.put(lEquipId+"_"+lUpsArgentStatus.getAlarmCode(), Constants.EQUIP_STATUS_NORMAL);
					//upsStateMap.put(lUpsArgentStatus.getAlarmCode(), Constants.EQUIP_STATUS_NORMAL);					
				} else if (lUpsArgentStatus.getStatus() == Constants.EQUIP_STATUS_NG) {
					if (lTechnicalAlarm == null) {
						monitorUpsStatus.processTechnicalAlarmRaise(lUpsArgentStatus);
					}
					upsMap.put(lEquipId+"_"+lUpsArgentStatus.getAlarmCode(), Constants.EQUIP_STATUS_NG);
					//upsStateMap.put(lUpsArgentStatus.getAlarmCode(), Constants.EQUIP_STATUS_NG);
				}
				//upsMap.put(lEquipId, upsStateMap);
			}			
			
			/*Map<String, Map<Integer, Integer>> upsMap 	= new TreeMap<String, Map<Integer, Integer>>();//EquipId, Map
			List<String> lEquipIdList =  cmhMonitor.getUpsEquipments();
			for (String lEquipId : lEquipIdList) {
				Map<Integer, Integer> upsStateMap	= new TreeMap<Integer, Integer>();//AlarmCode, Status
				logger.info("EquipId :: " + lEquipId);
				List<TechnicalAlarm> lUps1TechAlarmList = technicalAlarmManager.getTechnicalAlarmByEquipId(lEquipId);
				for (TechnicalAlarm lTechnicalAlarm : lUps1TechAlarmList) {
					logger.info("Alarm Code :: " + lTechnicalAlarm.getAlarmCode());
					upsStateMap.put(lTechnicalAlarm.getAlarmCode(), Constants.EQUIP_STATUS_NG);
				}
				upsMap.put(lEquipId, upsStateMap);
			}*/
			SchedulerConfigUps schedulerConfigUps = SchedulerConfigUps.getInstance();
			schedulerConfigUps.setUpsMap(upsMap);
			pJobDataMap.put("SCHEDULER_CONFIG_UPS", schedulerConfigUps);
		} catch (Exception e) {
			logger.error("Error in initializing UPS Status :: ", e);
		}
	}
}