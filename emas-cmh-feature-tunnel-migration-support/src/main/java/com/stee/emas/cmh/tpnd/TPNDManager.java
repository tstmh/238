
/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */

/**
 * <p>Title: TT238 Project</p>
 * <p>Description : TPND Module to reduce number of technical alarms accordingly by group</p>
 * <p>them together with a relationship table</p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Grace
 * @since Oct 29, 2019
 * @version 1.0
 */
package com.stee.emas.cmh.tpnd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import com.stee.emas.cmh.integration.CMHMessageSender;
import com.stee.emas.cmh.service.EquipStatusManager;
import com.stee.emas.cmh.service.TechnicalAlarmManager;
import com.stee.emas.cmh.tpnd.common.SiteSupportEquip;
import com.stee.emas.cmh.tpnd.common.SiteSupportPair;
import com.stee.emas.cmh.tpnd.common.TPNDConstants;
import com.stee.emas.cmh.tpnd.common.TPNDRaisedAlarm;
import com.stee.emas.cmh.tpnd.common.TechAlarmsGroup;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.EquipStatusDto;
import com.stee.emas.common.dto.EquipStatusDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emas.common.entity.EquipConfig;
import com.stee.emas.common.entity.EquipStatus;
import com.stee.emas.common.entity.SystemParameter;
import com.stee.emas.common.entity.TechnicalAlarm;
import com.stee.emas.common.util.MessageConverterUtil;

@Component("tpndManager")
public class TPNDManager {
	private static Logger logger = LoggerFactory.getLogger(TPNDManager.class);

	@Autowired
	TPNDHandler tpndHandler;
	@Autowired
	MessageConverterUtil messageConverterUtil;
	@Autowired
	TechnicalAlarmManager techAlarmManager;
	@Autowired
	EquipStatusManager equipStatusManager;
	@Autowired
	CMHMessageSender cmhMessageSender;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	TPNDProcess tpndProcess = null;
	
	// Added 5 Aug 2021 to see anyway to make a long running TPND Processing thread.
	Timer tpndCheckTimer = null;

	private List<TechAlarmsGroup> taGroupHoldingList;
	private static final Object holdingListLock = new Object();

	private List<TechnicalAlarmDto> techAlarmSendingList;
	private static final Object sendingListLock = new Object();

	private List<TPNDRaisedAlarm> taProcessedList;
	private static final Object processedListLock = new Object();
	
	// New clearing queue to hold clearing alarms during TPND in processing state.
	// 18 Sep 21 implemented by grace 
	private List<TechnicalAlarmDto> techAlarmClearingList;
	private static final Object clearingListLock = new Object();

	private List<SiteSupportEquip> downMDBList;
	private List<SiteSupportEquip> downTelcoList;
	private List<SiteSupportPair> downPairList;

	public List<TechAlarmsGroup> getHoldingList() {
		synchronized (holdingListLock) {
			return taGroupHoldingList;
		}
	}

	public List<TechnicalAlarmDto> getSendingList() {
		synchronized (sendingListLock) {
			return techAlarmSendingList;
		}
	}

	public List<TPNDRaisedAlarm> getProcessedList() {
		synchronized (processedListLock) {
			return taProcessedList;
		}
	}

	// New clearing queue to hold clearing alarms during TPND in processing state.
	// 18 Sep 21 implemented by grace 
	public List<TechnicalAlarmDto> getClearingList() {
		synchronized (clearingListLock) {			
			return techAlarmClearingList;
		}
	}
	
	// New clearing queue to hold clearing alarms during TPND in processing state.
	// 18 Sep 21 implemented by grace 
	public void clearClearingList() {
		synchronized (clearingListLock) {			
			techAlarmClearingList.clear();
		}		
	}

	private long sleepTime;
	private Date updateDate;
	private long tpndWithTicssWaittime;
	private long tpndWithoutTicssWaittime;
	private long tpndRecoverWaittime;
	private String[] equipTypeArray;

	public void setUpdateDate(Date date) {
		updateDate = date;
	}

	public void setTpndWithTicssWaittime(long time) {
		tpndWithTicssWaittime = time;
	}

	public void setTpndWithoutTicssWaittime(long time) {
		tpndWithoutTicssWaittime = time;
	}

	public void setTpndRecoverWaittime(long time) {
		tpndRecoverWaittime = time;
	}
	
	public void setSleepTime(long time) {
		sleepTime = time;
	}
	
	public long getSleepTime() {
		return sleepTime;
	}

	// Initialization of the TPNDManager
	public void init() {
		if (logger.isInfoEnabled()) {
			logger.info("********************************");
			logger.info("***** Starting TPND Module *****");
			logger.info("********************************");
			logger.info(" WithTPN : " + Boolean.valueOf(TPNDConstants.WithTPN));
		}
		taGroupHoldingList = new ArrayList<TechAlarmsGroup>();
		taProcessedList = new ArrayList<TPNDRaisedAlarm>();
		techAlarmSendingList = new ArrayList<TechnicalAlarmDto>();
		techAlarmClearingList = new ArrayList<TechnicalAlarmDto>();

		downMDBList = new ArrayList<SiteSupportEquip>();
		downTelcoList = new ArrayList<SiteSupportEquip>();
		downPairList = new ArrayList<SiteSupportPair>();

		updateDate = new Date();

		SystemParameter lSystemParameter1 = findSystemParameter("TPND_WITHTICSS_WAITTIME");
		if (lSystemParameter1 != null) {
			tpndWithTicssWaittime = Long.parseLong(lSystemParameter1.getValue());
			logger.info("TPND_WITHTICSS_WAITTIME = " + tpndWithTicssWaittime + " minutes");
			tpndWithTicssWaittime *= TPNDConstants.TPND_1_MINUTE;
		} else {
			logger.info("TPND_WITHTICSS_WAITTIME is missing from database");
		}

		SystemParameter lSystemParameter2 = findSystemParameter("TPND_WITHOUTTICSS_WAITTIME");
		if (lSystemParameter2 != null) {
			tpndWithoutTicssWaittime = Long.parseLong(lSystemParameter2.getValue());
			logger.info("TPND_WITHOUTTICSS_WAITTIME = " + tpndWithoutTicssWaittime + " minutes");
			tpndWithoutTicssWaittime *= TPNDConstants.TPND_1_MINUTE;
		} else {
			logger.info("TPND_WITHOUTTICSS_WAITTIME is missing from database");
		}

		SystemParameter lSystemParameter3 = findSystemParameter("TPND_RECOVER_WAITTIME");
		if (lSystemParameter3 != null) {
			tpndRecoverWaittime = Long.parseLong(lSystemParameter3.getValue());
			logger.info("TPND_RECOVER_WAITTIME = " + tpndRecoverWaittime + " minutes");
			tpndRecoverWaittime *= TPNDConstants.TPND_1_MINUTE;
		} else {
			logger.info("TPND_RECOVER_WAITTIME is missing from database");
		}

		SystemParameter systemParameter5 = findSystemParameter("SPECIAL_EQUIP_TYPE");
		if (systemParameter5 != null) {
			String readEquipTypeArray = systemParameter5.getValue();
			logger.info("SPECIAL_EQUIP_TYPE = " + readEquipTypeArray);
			equipTypeArray = StringUtils.split(readEquipTypeArray, ',');
		} else {
			logger.info("SPECIAL_EQUIP_TYPE is missing from database");
		}
		
		tpndProcess = new TPNDProcess();
		beanFactory.autowireBean(tpndProcess);
		tpndProcess.start();

		checkTPNDProcess();
	}
	
	public void checkTPNDProcess() {
		// To check on TPND process thread 5 Aug 2021
		if (tpndCheckTimer == null) {
			tpndCheckTimer = new Timer();
			tpndCheckTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					try {
						if (!tpndProcess.isAlive()) {
							logger.info("tpndCheckTimer:: Found tpndProcessThread is not alive.");
							startTpndProcess();
						}
					} catch (Exception e) {
						logger.error("exception in Timer function(tpndCheckTimer):", e);
					}
				}
			}, 300000, 300000);
		}
	}

	public void reset() {
		if (logger.isInfoEnabled()) {
			logger.info("***** Resetting SendingList and DownLists *****");
		}

		synchronized (sendingListLock) {
			techAlarmSendingList.clear();
		}
		downMDBList.clear();
		downTelcoList.clear();
		downPairList.clear();
	}

	// Added into holding list for Technical Alarms
	public void addToTAGroupHoldingList(TechnicalAlarmDto lTechAlarmDto) {
		if (!isTechAlarmSafe(lTechAlarmDto)) {
			logger.error("addToAlarmHoldingList :: Technical Alarm Error : " + lTechAlarmDto.getAlarmId());
		}

		// Find group has already been created by another Technical Alarm
		String relatedMdb = tpndHandler.findRelatedMdbByEquip(lTechAlarmDto.getEquipId());
		String relatedTelco = tpndHandler.findRelatedTelcoByEquip(lTechAlarmDto.getEquipId());
		if (relatedMdb == null) {
			relatedMdb = "";
		}
		if (relatedTelco == null) {
			relatedTelco = "";
		}
		// To check if mdb is only holding 1 equipment only inside
		// v_mdb_telco_connect_equip_list
		// If holding 1 equipment only, skip TPND
		if (tpndHandler.getNoOfEquipsByMdbId(relatedMdb) == 1) {
			relatedMdb = "";
			relatedTelco = "";
		}
		// To check if mdb is only holding 1 vms and 1 ids only inside, skip TPND
		if (tpndHandler.isInSpecialMapping(relatedMdb)) {
			relatedMdb = "";
			relatedTelco = "";
		}

				
		TechnicalAlarm lTechnicalAlarmDB = findTechnicalAlarmById(lTechAlarmDto.getAlarmId());
		if (lTechnicalAlarmDB == null) {
			// find if alarm has already been stored in processedList.
			if (isAlarmInProcessedList(lTechAlarmDto))
				return;

			if (!addAlarmInExistingHoldingList(lTechAlarmDto)) {
				synchronized (holdingListLock) {
					if ((relatedMdb != null) && (!relatedMdb.isEmpty())) {
						TechAlarmsGroup newGroup = new TechAlarmsGroup(lTechAlarmDto.getStartDate());
						//if (tpndHandler.isThereTICSSEquipments(lTechAlarmDto.getEquipId())) {
						if (tpndHandler.isThereSpecialEquipment(lTechAlarmDto.getEquipId())) {
							newGroup.setWaitTime(tpndWithTicssWaittime);
						} else {
							newGroup.setWaitTime(tpndWithoutTicssWaittime);
						}
						newGroup.setRelatedMDB(relatedMdb);
						newGroup.setNumOfEquips(tpndHandler.getNoOfEquipsByMdbId(relatedMdb));
						newGroup.addDeviceIntoList(lTechAlarmDto);

						taGroupHoldingList.add(newGroup);
						logger.info("addToAlarmHoldingList :: TAGroup created for: " + newGroup.getRelatedMDB());
						logger.info("addToAlarmHoldingList :: equipment TA added: " + lTechAlarmDto.getAlarmId());

						// Must check for Special Case where device may be affected by different mdb and
						// then add to linked different mdb group
						EquipConfig thisEquipConfig = tpndHandler.findEquipConfigById(lTechAlarmDto.getEquipId());
						if (thisEquipConfig != null) {
							if (!isSpecialEquipType(thisEquipConfig.getSubSystemId())
									|| relatedMdb.equals("")) {
								boolean isRelatedToOtherMDB = true;
								List<String> telcoListbyMDB = tpndHandler.getTelcoListByMdbId(relatedMdb);
								for (String thisTelco : telcoListbyMDB) {
									if (thisTelco.equals(relatedTelco)) {
										isRelatedToOtherMDB = false;
										break;
									}
								}

								// Must add 1 more group related to other MDB if needed
								if (isRelatedToOtherMDB) {
									String anotherRelatedMdb = tpndHandler.getMdbIdByTelcoId(relatedTelco);
									TechAlarmsGroup newGroup2 = new TechAlarmsGroup(lTechAlarmDto.getStartDate());

									newGroup2.setWaitTime(newGroup.getWaitTime());
									newGroup2.setRelatedMDB(anotherRelatedMdb);
									newGroup2.setNumOfEquips(tpndHandler.getNoOfEquipsByMdbId(anotherRelatedMdb));
									newGroup2.addDeviceIntoList(lTechAlarmDto);

									taGroupHoldingList.add(newGroup2);
									logger.info("addToAlarmHoldingList :: TAGroup created for: "
											+ newGroup2.getRelatedMDB());
									logger.info("addToAlarmHoldingList :: equipment TA added: "
											+ lTechAlarmDto.getAlarmId());
								}
							}
						} else {
							logger.error(
									"addToAlarmHoldingList :: Equip Config not found: " + lTechAlarmDto.getEquipId());
						}
					} else { // if alarm received is without mdb information, it will be treated as
								// individual alarm and sent out 1 second later or next process cycle.
						logger.info("addToAlarmHoldingList :: Related Mdb not found in DB or is single equipment!!!! "
								+ lTechAlarmDto.getEquipId());
						TechAlarmsGroup newGroup = new TechAlarmsGroup();
						newGroup.setWaitTime(TPNDConstants.TPND_1_SECOND);
						newGroup.setRelatedMDB(relatedMdb);
						newGroup.addDeviceIntoList(lTechAlarmDto);

						taGroupHoldingList.add(newGroup);
						logger.info("addToAlarmHoldingList :: Neutral TAGroup created");
						logger.info("addToAlarmHoldingList :: equipment TA added: " + lTechAlarmDto.getAlarmId());
					}
				}
			}
		} else {
			logger.info("addToAlarmHoldingList :: equipment TA already raised: " + lTechAlarmDto.getAlarmId());
		}
	}

	private boolean isSpecialEquipType(String equipType) {
		for (String thisEquipType : equipTypeArray) {
			if (equipType.equals(thisEquipType)) {
				return true;
			}
		}
		
		return false;
	}

	// To check if lTechAlarmDto has already been processed by TPND
	private boolean isAlarmInProcessedList(TechnicalAlarmDto lTechAlarmDto) {
		boolean result = false;

		if (!isTechAlarmSafe(lTechAlarmDto)) {
			logger.error("isAlarmInProcessedList :: Technical Alarm Error : " + lTechAlarmDto.getAlarmId());
		}

		synchronized (processedListLock) {
			for (TPNDRaisedAlarm raisedAlarm : taProcessedList) {
				for (TechnicalAlarmDto raisedEquip : raisedAlarm.getEquipDtoList()) {
					if (raisedEquip.getEquipId().equals(lTechAlarmDto.getEquipId())) {
						logger.info("addToAlarmHoldingList :: Found alarm in processed list: "
								+ lTechAlarmDto.getEquipId());
						logger.info("addToAlarmHoldingList :: Alarm not added: " + lTechAlarmDto.getAlarmId());
						result = true;
					}
				}
			}
		}

		return result;
	}

	// To check if lTechAlarmDto has already been processed by TPND
	private boolean addAlarmInExistingHoldingList(TechnicalAlarmDto lTechAlarmDto) {
		boolean result = false;

		if (!isTechAlarmSafe(lTechAlarmDto)) {
			logger.error("addAlarmInExistingHoldingList :: Technical Alarm Error : " + lTechAlarmDto.getAlarmId());
		}

		String relatedMdb = tpndHandler.findRelatedMdbByEquip(lTechAlarmDto.getEquipId());
		String relatedTelco = tpndHandler.findRelatedTelcoByEquip(lTechAlarmDto.getEquipId());
		if (relatedMdb == null) {
			relatedMdb = "";
		}
		if (relatedTelco == null) {
			relatedTelco = "";
		}
		
		// To check if mdb is only holding 1 equipment only inside
		// v_mdb_telco_connect_equip_list
		// If holding 1 equipment only, skip TPND
		if (tpndHandler.getNoOfEquipsByMdbId(relatedMdb) == 1) {
			relatedMdb = "";
			relatedTelco = "";
		}
		// [08/11/2022] - to check if mdb is only holding 1 vms and 1 ids only. If so, skipped TPND
		if (tpndHandler.isInSpecialMapping(relatedMdb)) {
			relatedMdb = "";
			relatedTelco = "";			
		}

		synchronized (holdingListLock) {
			if (!taGroupHoldingList.isEmpty()) {
				// find to see if there is any mdb group that can have this device added
				for (TechAlarmsGroup thisGroup : taGroupHoldingList) {
					if (thisGroup.getRelatedMDB().equals(relatedMdb)) {
						thisGroup.addDeviceIntoList(lTechAlarmDto);
						result = true;
						logger.info("addToAlarmHoldingList :: Found TAGroup under MDB: " + thisGroup.getRelatedMDB());
						logger.info("addToAlarmHoldingList :: equipment TA added: " + lTechAlarmDto.getAlarmId());
					}
				}

				// Must check for other linked mdb via telco
				EquipConfig thisEquipConfig = tpndHandler.findEquipConfigById(lTechAlarmDto.getEquipId());
				if ((thisEquipConfig != null) && (!(isSpecialEquipType(thisEquipConfig.getSubSystemId())
						|| relatedMdb.equals("") || relatedTelco.equals("")))) {
					boolean isRelatedToOtherMDB = true;
					List<String> telcoListbyMDB = tpndHandler.getTelcoListByMdbId(relatedMdb);
					for (String thisTelco : telcoListbyMDB) {
						if (thisTelco.equals(relatedTelco)) {
							isRelatedToOtherMDB = false;
							break;
						}
					}

					if (isRelatedToOtherMDB) {
						String anotherRelatedMdb = tpndHandler.getMdbIdByTelcoId(relatedTelco);

						// Add the same device to related mdb groups
						for (TechAlarmsGroup thisGroup : taGroupHoldingList) {
							if (thisGroup.getRelatedMDB().equals(anotherRelatedMdb)) {
								thisGroup.addDeviceIntoList(lTechAlarmDto);
								result = true;
								logger.info("addToAlarmHoldingList :: Found TAGroup under MDB: "
										+ thisGroup.getRelatedMDB());
								logger.info(
										"addToAlarmHoldingList :: equipment TA added: " + lTechAlarmDto.getAlarmId());
							}
						}
					}
				}
			}
		}

		return result;
	}

	// Remove from the Received Alarm List if there is a ALARM_CLEARED while waiting
	// for TPND processing
	public boolean removeFromTAGroupHoldingList(TechnicalAlarmDto lTechAlarmDto) {
		boolean deleted = false;

		if (!isTechAlarmSafe(lTechAlarmDto)) {
			logger.error("removeFromTAGroupHoldingList :: Technical Alarm Error : " + lTechAlarmDto.getAlarmId());
		}

		synchronized (holdingListLock) {
			List<TechAlarmsGroup> taGroupsToRemove = new ArrayList<TechAlarmsGroup>();

			for (TechAlarmsGroup thisGroup : taGroupHoldingList) {
				List<TechnicalAlarmDto> taListToRemove = new ArrayList<TechnicalAlarmDto>();
				for (TechnicalAlarmDto thisDto : thisGroup.getTAList()) {
					if (thisDto.getEquipId().equals(lTechAlarmDto.getEquipId())) {
						logger.info("removeFromTAGroupHoldingList :: relatedMDB Found :" + thisGroup.getRelatedMDB()
								+ " for " + lTechAlarmDto.getAlarmId());
						taListToRemove.add(thisDto);
						deleted = true;
					}
				}

				for (TechnicalAlarmDto removeDto : taListToRemove) {
					thisGroup.removeDevicefromList(removeDto);
				}
				
				// Add group for cleaning up if the group no longer has any technical alarms
				if (thisGroup.getTAList().isEmpty()) {
					logger.info("removeFromTAGroupHoldingList :: Empty Group Found with relatedMDB :"
							+ thisGroup.getRelatedMDB());
					taGroupsToRemove.add(thisGroup);
				}
			}

			if (!taGroupsToRemove.isEmpty()) {
				taGroupHoldingList.removeAll(taGroupsToRemove);
			}
		}
		
		return deleted;
	}

	public boolean removeFromProcessedHoldingList(TechnicalAlarmDto lTechAlarmDto) {
		boolean removed = false;
		if (!isTechAlarmSafe(lTechAlarmDto)) {
			logger.error("removeFromProcessedHoldingList :: Technical Alarm Error : " + lTechAlarmDto.getAlarmId());
		}

		synchronized (processedListLock) {
			try {
				// To remove tpnd equipment
				if ((lTechAlarmDto.getEquipType().equals(TPNDConstants.TPND_MDB_TYPE))
						|| (lTechAlarmDto.getEquipType().equals(TPNDConstants.TPND_TELCO_TYPE))
						|| (lTechAlarmDto.getEquipType().equals(TPNDConstants.TPND_PAIR_TYPE))) {

					List<TPNDRaisedAlarm> toRemove = new ArrayList<TPNDRaisedAlarm>();
					for (TPNDRaisedAlarm thisAlarm : taProcessedList) {						
						if (thisAlarm.getAlarmDto().getEquipId().equals(lTechAlarmDto.getEquipId())) {
							toRemove.add(thisAlarm);
							logger.info("removeFromProcessedHoldingList :: Under Linked Alarm : "
									+ thisAlarm.getAlarmDto().getAlarmId());
													}
					}

					if (!toRemove.isEmpty()) {
						taProcessedList.removeAll(toRemove);
						removed = true;
					}
				}

				// To remove individual equipment
				for (TPNDRaisedAlarm thisAlarm : taProcessedList) {
					List<TechnicalAlarmDto> toRemoveList = new ArrayList<TechnicalAlarmDto>();
					if (!thisAlarm.getEquipDtoList().isEmpty()) {
						logger.info("removeFromProcessedHoldingList :: Under Linked Alarm : "
								+ thisAlarm.getAlarmDto().getAlarmId());
						for (TechnicalAlarmDto thisEquip : thisAlarm.getEquipDtoList()) {
							if (thisEquip.getEquipId().equals(lTechAlarmDto.getEquipId())) {
								toRemoveList.add(thisEquip);
								logger.info("removeFromProcessedHoldingList :: Under Linked Alarm : "
										+ thisAlarm.getAlarmDto().getAlarmId());
							}
						}

						if (!toRemoveList.isEmpty()) {
							thisAlarm.getEquipDtoList().removeAll(toRemoveList);
							removed = true;
							if (!thisAlarm.isStartDateSet()) {
								thisAlarm.setStartDate();
							}
						}
					}
				}
			} catch (NullPointerException e) {
				logger.error("removeFromProcessedHoldingList :: Alarm_id: " + lTechAlarmDto.getAlarmId(), e);
			}
			

		}
		return removed;
	}


	public List<TechAlarmsGroup> findTAGroupsToProcess() {
		synchronized (holdingListLock) {
			List<TechAlarmsGroup> markedForProcess = new ArrayList<TechAlarmsGroup>();

			for (TechAlarmsGroup thisGroup : taGroupHoldingList) {
				if (thisGroup.isWaitTimeOver()) {
					logger.info("findTAGroupsToProcess :: TAGroup marked for process: " + thisGroup.getRelatedMDB());
					logger.info("findTAGroupsToProcess :: Technical Alarms inside Group: ");
					logger.info(thisGroup.getTAList().toString());

					markedForProcess.add(thisGroup);
				}
			}

			if (!markedForProcess.isEmpty()) {
				taGroupHoldingList.removeAll(markedForProcess);
			}
			return markedForProcess;
		}
	}

	public List<TPNDRaisedAlarm> findAlarmsToRecover() {
		List<TPNDRaisedAlarm> markedForProcess = new ArrayList<TPNDRaisedAlarm>();

		synchronized (processedListLock) {
			for (TPNDRaisedAlarm thisAlarm : taProcessedList) {
				if (thisAlarm.isWaitTimeOver(tpndRecoverWaittime)) {
					logger.info(
							"findAlarmsToRecover :: alarm marked for recover: " + thisAlarm.getAlarmDto().getAlarmId());
					markedForProcess.add(thisAlarm);
				}
			}

			if (!markedForProcess.isEmpty()) {
				taProcessedList.removeAll(markedForProcess);
			}

			return markedForProcess;
		}
	}

	public void processIntoTpnDownEquipments(List<TechAlarmsGroup> groupList) {
		downMDBList = tpndHandler.getDownMDBList(groupList);
		logger.info("processIntoTPNDDownEquipments :: No of down MDB :" + Integer.toString(downMDBList.size()));

		downTelcoList = tpndHandler.getDownTelcoList(groupList);
		logger.info("processIntoTPNDDownEquipments :: No of down Telco :" + Integer.toString(downTelcoList.size()));

		// Get relationship between mdb and telco and populate _downPairList for TPN
		// consideration
		if (TPNDConstants.WithTPN) {
			// Link MDB with Telco
			for (SiteSupportEquip thisMDB : downMDBList) {
				List<String> relatedTelcoList = tpndHandler.getTelcoListByMdbId(thisMDB.getEquipId());
				// check on telco list inside the group
				if (!relatedTelcoList.isEmpty()) {
					List<SiteSupportPair> tempPairList = new ArrayList<SiteSupportPair>();
					int numberOfTelco = tpndHandler.getNoOfTelcoByMdbId(thisMDB.getEquipId());
					int addedPair = 0;
					for (int i = 0; i < numberOfTelco; i++) {
						String compareTelco = relatedTelcoList.get(i);
						for (SiteSupportEquip thisTelco : downTelcoList) {
							if (thisTelco.getEquipId().equals(compareTelco)) {
								SiteSupportPair newPair = new SiteSupportPair(thisMDB.getEquipId(),
										thisTelco.getEquipId(), thisMDB.getStartDate());
								logger.info("processIntoTPNDDownEquipments :: down Pair added to temp List :"
										+ newPair.getPair());
								tempPairList.add(newPair);
								addedPair++;
								break;
							}
						}
					}

					if (addedPair == numberOfTelco) {
						downPairList.addAll(tempPairList);
					}
				}
			}

			// Filter out same pairs
			List<SiteSupportPair> newDownPairList = removePairDuplicates(downPairList);
			downPairList.clear();
			downPairList.addAll(newDownPairList);
			/*******************************************/

			// Note: For each pair that has been generated,
			// if down MDB has TICSS or IDSS, the pair is not valid as it will be MDB down only
			// instead of pair.
			List<SiteSupportPair> reducePair = new ArrayList<SiteSupportPair>();
			for (SiteSupportPair thisPair : downPairList) {
				//if (tpndHandler.isThereTICSSEquipmentsInTPNDDevice(thisPair.getMdb())) {
				if (tpndHandler.isThereSpecialEquipInTPNDDevice(thisPair.getMdb())) {
					logger.info("processIntoTPNDDownEquipments :: down Pair removed from list due to TICSS/IDSS:"
							+ thisPair.getPair());
					reducePair.add(thisPair);
				}
			}
			downPairList.removeAll(reducePair);
			logger.info("processIntoTPNDDownEquipments :: No of down Pair :" + Integer.toString(downPairList.size()));
		}
	}

	// Remove all equipment technical alarms that have been combined into MDB alarms
	// And also remove the telcos powered by their related MDB
	public List<TechnicalAlarmDto> getAllMDBLinkedEquips(List<TechAlarmsGroup> groupList) {
		List<TechnicalAlarmDto> toRemoveDeviceList = new ArrayList<TechnicalAlarmDto>();

		if (TPNDConstants.FinalVersion) {
			// find all equipment held by this MDB
			for (SiteSupportEquip mdbInfo : downMDBList) {
				for (String deviceID : mdbInfo.getEquipList()) {
					for (TechAlarmsGroup thisGroup : groupList) {
						List<TechnicalAlarmDto> removeList = new ArrayList<TechnicalAlarmDto>();

						for (TechnicalAlarmDto receivedDto : thisGroup.getTAList()) {
							if (deviceID.equals(receivedDto.getEquipId())) {
								logger.info("getAllMDBLinkedEquipsToRemove :: Equipment " + deviceID
										+ " to be removed from received list due to down MDB :" + mdbInfo.getEquipId());
								toRemoveDeviceList.add(receivedDto);
								removeList.add(receivedDto);
								break;
							}
						}

						thisGroup.getTAList().removeAll(removeList);
					}
				}
			}
		}

		// find all telco held by this MDB (Have with tpn and without tpn logic)
		List<SiteSupportEquip> toRemoveTelcoList = new ArrayList<SiteSupportEquip>();
		if (TPNDConstants.WithTPN) {
			for (SiteSupportEquip thisMDB : downMDBList) {
				List<String> relatedTelcoList = tpndHandler.getTelcoListByMdbId(thisMDB.getEquipId());
				// check on telco list inside the group
				for (String compareTelco : relatedTelcoList) {
					for (SiteSupportEquip thisTelco : downTelcoList) {
						if (thisTelco.getEquipId().equals(compareTelco)) {
							logger.info("getAllMDBLinkedEquipsToRemove :: Telco " + thisTelco.getEquipId()
									+ " to be removed from received list due to down MDB :" + thisMDB.getEquipId());
							toRemoveTelcoList.add(thisTelco);
							break;
						}
					}
				}
			}
		} else { // Not TPN logic
			for (SiteSupportEquip thisMDB : downMDBList) {
				// Added on 11/04/22 for IDSS implementation into TPND
				if (tpndHandler.isThereSpecialEquipInTPNDDevice(thisMDB.getEquipId())) {
					List<String> relatedTelcoList = tpndHandler.getTelcoListByMdbId(thisMDB.getEquipId());
					// check on telco list inside the group
					for (String compareTelco : relatedTelcoList) {
						for (SiteSupportEquip thisTelco : downTelcoList) {
							if (thisTelco.getEquipId().equals(compareTelco)) {
								logger.info("getAllMDBLinkedEquipsToRemove :: Telco " + thisTelco.getEquipId()
										+ " to be removed from received list due to down MDB :" + thisMDB.getEquipId());
								toRemoveTelcoList.add(thisTelco);
								break;
							}
						}
					}
				}
			}
		}
		downTelcoList.removeAll(toRemoveTelcoList);

		// To add in the removal of telco alarm if telco is holding 1 equipment only
		List<SiteSupportEquip> singleTelcoList = new ArrayList<SiteSupportEquip>();
		for (SiteSupportEquip thisTelcoSupportEquip : downTelcoList) {
			if (thisTelcoSupportEquip.getEquipList().size() == 1) {
				logger.info("getAllMDBLinkedEquipsToRemove :: Telco " + thisTelcoSupportEquip.getEquipId()
						+ " to be removed as single equipment");
				singleTelcoList.add(thisTelcoSupportEquip);
			}
		}
		downTelcoList.removeAll(singleTelcoList);

		for (SiteSupportEquip thisTelcoSupportEquip : toRemoveTelcoList) {
			TechnicalAlarmDto thisTelcoAlarmDTO = generateMDBTELTechAlarmDTORaised(thisTelcoSupportEquip,
					TPNDConstants.TPND_TELCO_TYPE);
			toRemoveDeviceList.add(thisTelcoAlarmDTO);
		}		
					
		return toRemoveDeviceList;
	}

	// Remove all equipment technical alarms that have been combined into Telco
	// alarms
	public List<TechnicalAlarmDto> getAllTelcoLinkedEquips(List<TechAlarmsGroup> groupList) {
		List<TechnicalAlarmDto> toRemoveDeviceList = new ArrayList<TechnicalAlarmDto>();

		// find all equipment held by this Telco
		for (SiteSupportEquip telcoInfo : downTelcoList) {
			for (String deviceID : telcoInfo.getEquipList()) {
				for (TechAlarmsGroup thisGroup : groupList) {
					List<TechnicalAlarmDto> removeList = new ArrayList<TechnicalAlarmDto>();
					for (TechnicalAlarmDto receivedDto : thisGroup.getTAList()) {
						if (deviceID.equals(receivedDto.getEquipId())) {
							logger.info("getAllTelcoLinkedEquipsToRemove :: Equipment " + deviceID
									+ " to be removed from received list due to down Telco :" + telcoInfo.getEquipId());
							toRemoveDeviceList.add(receivedDto);
							removeList.add(receivedDto);
							break;
						}
					}

					thisGroup.getTAList().removeAll(removeList);
				}
			}
		}
		return toRemoveDeviceList;
	}

	// Remove all equipment technical alarms that have been combined into Pair
	// alarms
	public List<TechnicalAlarmDto> getPairLinkedSiteSupports() {
		List<SiteSupportEquip> toRemoveMDBList = new ArrayList<SiteSupportEquip>();
		List<SiteSupportEquip> toRemoveTelcoList = new ArrayList<SiteSupportEquip>();
		List<TechnicalAlarmDto> toRemoveTPNDList = new ArrayList<TechnicalAlarmDto>();

		// find all mdb and telco held by this Pair
		for (SiteSupportPair pairInfo : downPairList) {
			// remove mdb from _downMDBList since it is already inside a Pair
			String deviceID = pairInfo.getMdb();
			for (SiteSupportEquip downMDB : downMDBList) {
				if (deviceID.equals(downMDB.getEquipId())) {
					if (!toRemoveMDBList.contains(downMDB)) {
						logger.info("getPairLinkedSiteSupportsToRemove :: Equipment " + deviceID
								+ " to be removed from _downMDBList due to down Pair :" + pairInfo.getPair());
						toRemoveMDBList.add(downMDB);
						break;
					} else {
						logger.info("getPairLinkedSiteSupportsToRemove :: Equipment " + deviceID
								+ " already inside remove list");
					}
				}
			}

			// remove telco from _downTelcoList since it is already inside a Pair
			deviceID = pairInfo.getTelco();
			for (SiteSupportEquip downTelco : downTelcoList) {
				if (deviceID.equals(downTelco.getEquipId())) {
					if (!toRemoveTelcoList.contains(downTelco)) {
						logger.info("getPairLinkedSiteSupportsToRemove :: Equipment " + deviceID
								+ " to be removed from received list due to down Pair :" + pairInfo.getPair());
						toRemoveTelcoList.add(downTelco);
						break;
					} else {
						logger.info("getPairLinkedSiteSupportsToRemove :: Equipment " + deviceID
								+ " already inside remove list");
					}
				}
			}
		}

		// Setup the DTOs to be sent out for updating of equipStatus
		for (SiteSupportEquip mdbAlarmInfo : toRemoveMDBList) {
			TechnicalAlarmDto techAlarmDto = generateMDBTELTechAlarmDTORaised(mdbAlarmInfo,
					TPNDConstants.TPND_MDB_TYPE);
			toRemoveTPNDList.add(techAlarmDto);
		}

		for (SiteSupportEquip telcoAlarmInfo : toRemoveTelcoList) {
			TechnicalAlarmDto techAlarmDto = generateMDBTELTechAlarmDTORaised(telcoAlarmInfo,
					TPNDConstants.TPND_TELCO_TYPE);
			toRemoveTPNDList.add(techAlarmDto);
		}

		// remove MDB and Telco from respective down list due to down pair list
		downMDBList.removeAll(toRemoveMDBList);
		downTelcoList.removeAll(toRemoveTelcoList);

		return toRemoveTPNDList;
	}

	// Update all equipment technical alarms that have been combined into TPND
	// related alarms
	public void updateTPNDRemovedEquipStatus(List<TechnicalAlarmDto> toRemoveDeviceList) {
		// Update database and equipStatus for all removed equipments
		EquipStatusDto equipStatusDto = null;
		List<EquipStatusDto> changedEquipStatusList = new ArrayList<EquipStatusDto>();

		for (TechnicalAlarmDto thisDto : toRemoveDeviceList) {
			/***
			 * Not to write to HistTechnicalAlarm as long as they are not sent
			 * thisDto.setStatus(Constants.ALARM_CLEARED);
			 * thisDto.setClearBy(TPNDConstants.TPND);
			 * 
			 * TechnicalAlarm thisTechAlarm =
			 * messageConverterUtil.convertTechAlarmDtoToEntity(thisDto); // Save technical
			 * alarm into history DB cleared by TPND (There will be no acknowledgement for
			 * these alarms) techAlarmManager.saveHistTechnialAlarm(thisTechAlarm,
			 * TPNDConstants.TPND);
			 ***/

			// Update OPESTATE of devices even if remove from technical alarm.
			try {
				equipStatusDto = equipStatusManager.generateEquipStatusForAlarmRaised(thisDto);
			} catch (Exception e) {
				logger.error(
						"updateTPNDRemovedEquipStatus :: Error processing EquipStatus for Technical alarm clear in TPND ...",
						e);
			}

			if (equipStatusDto != null) {
				changedEquipStatusList.add(equipStatusDto);
			}
		}

		// Broadcast equip status out
		EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList(changedEquipStatusList);
		if (!lEquipStatusDtoList.getDtoList().isEmpty()) {
			logger.info("updateTPNDRemovedEquipStatus :: Object sent to the queue " + lEquipStatusDtoList);
			cmhMessageSender.sendAWJmsMessage("EquipStatus", MessageConstants.EQUIP_STATUS_ID);
			cmhMessageSender.sendEmasCcsJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
		}
	}

	// Update all equipment equip_status that are related the TPND
	public void updateTPNDRelatedEquipStatusUp(TechnicalAlarmDto lTechAlarmDto) {
		if (lTechAlarmDto.getEquipType().equals(TPNDConstants.TPND_MDB_TYPE)) {
			processMdbRelatedEquipStatusUp(lTechAlarmDto);
		} else if (lTechAlarmDto.getEquipType().equals(TPNDConstants.TPND_TELCO_TYPE)) {
			if (TPNDConstants.FinalVersion) {
				processTelcoRelatedEquipStatusUp(lTechAlarmDto);
			}
		} else { // Constants.TPND_PAIR_TYPE
		}
	}

	public void processMdbRelatedEquipStatusUp(TechnicalAlarmDto lTechAlarmDto) {
		List<EquipStatusDto> updateEquipStatusList = new ArrayList<EquipStatusDto>();
		List<String> relateTelcoList = tpndHandler.getTelcoListByMdbId(lTechAlarmDto.getEquipId());

		// All MDB-Related Telcos have to be status up
		for (String thisEquip : relateTelcoList) {
			logger.info("processMdbRelatedEquipStatusUp :: Processing Telco Status :" + thisEquip);
			EquipStatusDto equipStatusDto = null;

			// Setup TechnicalAlarmDto from HistTechnicalAlarm
			TechnicalAlarmDto techAlarmDto = generateTPNDTechAlarmDTOCleared(lTechAlarmDto, thisEquip,
					TPNDConstants.TPND_TELCO_TYPE);

			try {
				EquipStatus thisEquipStatus = tpndHandler
						.findEquipStatusByEquipIdAndStatusCode(techAlarmDto.getEquipId(), Constants.OPE_STATE);
				if (thisEquipStatus != null) {
					if (thisEquipStatus.getStatus() != Constants.EQUIP_STATUS_NORMAL) {
						equipStatusDto = equipStatusManager.generateTPNDEquipStatusForClearStatus(techAlarmDto);
					} else {
						logger.info("processMdbRelatedEquipStatusUp :: Equip Status not generated :"
								+ techAlarmDto.getEquipId());
					}
				} else {
					logger.info("processMdbRelatedEquipStatusUp :: Equip Status not generated :"
							+ techAlarmDto.getEquipId());
				}
			} catch (Exception e) {
				logger.error(
						"processMdbRelatedEquipStatusUp :: Error processing EquipStatus for Technical alarm clear in TPND ...",
						e);
			}

			if (equipStatusDto != null) {
				updateEquipStatusList.add(equipStatusDto);
			}
		}

		if (TPNDConstants.FinalVersion) {
			// To handle individual equipments status update here instead since there is
			// TICSS.
			List<String> relateEquipList = tpndHandler.getEquipListBySiteSupportId(lTechAlarmDto.getEquipId());
			for (String thisEquip : relateEquipList) {
				logger.info("processMdbRelatedEquipStatusUp :: Processing Equip Status :" + thisEquip);
				EquipStatusDto equipStatusDto = null;

				TechnicalAlarmDto techAlarmDto = generateTPNDTechAlarmDTOCleared(thisEquip, lTechAlarmDto);

				try {
					EquipStatus thisEquipStatus = tpndHandler.findEquipStatusByEquipIdAndStatusCode(thisEquip,
							Constants.OPE_STATE);
					if (thisEquipStatus.getStatus() != Constants.EQUIP_STATUS_NORMAL) {
						equipStatusDto = equipStatusManager.generateEquipStatusForClearStatus(techAlarmDto);
					} else {
						logger.info("processMdbRelatedEquipStatusUp :: Equip Status not generated :" + thisEquip);
					}
				} catch (Exception e) {
					logger.error(
							"processMdbRelatedEquipStatusUp :: Error processing EquipStatus for Technical alarm clear in TPND ...",
							e);
				}

				if (equipStatusDto != null) {
					updateEquipStatusList.add(equipStatusDto);
				}
			}
		}


		EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList(updateEquipStatusList);
		if (!lEquipStatusDtoList.getDtoList().isEmpty()) {
			logger.info("processMdbRelatedEquipStatusUp :: Object sent to the queue " + lEquipStatusDtoList);
			cmhMessageSender.sendAWJmsMessage("EquipStatus", MessageConstants.EQUIP_STATUS_ID);
			cmhMessageSender.sendEmasCcsJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
		}
	}

	public void processMdbRelatedTelcoStatusUp(TechnicalAlarmDto lTechAlarmDto) {
		EquipStatusDto equipStatusDto = null;
		List<EquipStatusDto> updateEquipStatusList = new ArrayList<EquipStatusDto>();
		List<String> relateTelcoList = tpndHandler.getTelcoListByMdbId(lTechAlarmDto.getEquipId());

		// All MDB-Related Telcos have to be status up
		for (String thisEquip : relateTelcoList) {
			logger.info("processMdbRelatedEquipStatusUp :: Processing Telco Status :" + thisEquip);

			// Setup TechnicalAlarmDto from HistTechnicalAlarm
			TechnicalAlarmDto techAlarmDto = generateTPNDTechAlarmDTOCleared(lTechAlarmDto, thisEquip,
					TPNDConstants.TPND_TELCO_TYPE);

			try {
				EquipStatus thisEquipStatus = tpndHandler
						.findEquipStatusByEquipIdAndStatusCode(techAlarmDto.getEquipId(), Constants.OPE_STATE);
				if (thisEquipStatus.getStatus() != Constants.EQUIP_STATUS_NORMAL) {
					equipStatusDto = equipStatusManager.generateTPNDEquipStatusForClearStatus(techAlarmDto);
				} else {
					logger.info("processMdbRelatedEquipStatusUp :: Equip Status not generated :"
							+ techAlarmDto.getEquipId());
				}
			} catch (Exception e) {
				logger.error(
						"processMdbRelatedEquipStatusUp :: Error processing EquipStatus for Technical alarm clear in TPND ...",
						e);
			}

			if (equipStatusDto != null) {
				updateEquipStatusList.add(equipStatusDto);
			}
		}

		EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList(updateEquipStatusList);
		if (!lEquipStatusDtoList.getDtoList().isEmpty()) {
			logger.info("processMdbRelatedEquipStatusUp :: Object sent to the queue " + lEquipStatusDtoList);
			cmhMessageSender.sendAWJmsMessage("EquipStatus", MessageConstants.EQUIP_STATUS_ID);
			cmhMessageSender.sendEmasCcsJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);

		}
	}

	public void processTelcoRelatedEquipStatusUp(TechnicalAlarmDto lTechAlarmDto) {
		List<EquipStatusDto> updateEquipStatusList = new ArrayList<EquipStatusDto>();
		List<String> relateEquipList = tpndHandler.getEquipListBySiteSupportId(lTechAlarmDto.getEquipId());
		for (String thisEquip : relateEquipList) {
			EquipStatusDto equipStatusDto = null;
			logger.info("processTelcoRelatedEquipStatusUp :: Processing Equip Status :" + thisEquip);
			TechnicalAlarmDto techAlarmDto = generateTPNDTechAlarmDTOCleared(thisEquip, lTechAlarmDto);

			try {
				EquipStatus thisEquipStatus = tpndHandler.findEquipStatusByEquipIdAndStatusCode(thisEquip,
						Constants.OPE_STATE);
				if (thisEquipStatus.getStatus() != Constants.EQUIP_STATUS_NORMAL) {
					equipStatusDto = equipStatusManager.generateEquipStatusForClearStatus(techAlarmDto);
				} else {
					logger.info("processTelcoRelatedEquipStatusUp :: Equip Status not generated :" + thisEquip);
				}
			} catch (Exception e) {
				logger.error(
						"processTelcoRelatedEquipStatusUp :: Error processing EquipStatus for Technical alarm clear in TPND ...",
						e);
			}

			if (equipStatusDto != null) {
				updateEquipStatusList.add(equipStatusDto);
			}
		}

		EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList(updateEquipStatusList);
		if (!lEquipStatusDtoList.getDtoList().isEmpty()) {
			logger.info("processTelcoRelatedEquipStatusUp :: Object sent to the queue " + lEquipStatusDtoList);
			cmhMessageSender.sendAWJmsMessage("EquipStatus", MessageConstants.EQUIP_STATUS_ID);
			cmhMessageSender.sendEmasCcsJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
		}
	}

	public void processPositiveIndividualAlarms(List<TechAlarmsGroup> groupList) {
		for (TechAlarmsGroup thisGroup : groupList) {
			for (TechnicalAlarmDto thisDto : thisGroup.getTAList()) {
				synchronized (sendingListLock) {
					boolean added = techAlarmSendingList.add(thisDto);
					if (!added) {
						logger.info("ProcessPositiveTelcoAlarmList ::techAlarmDTO for Individual :"
								+ thisDto.getEquipId() + " Add failed");
					} else {
						logger.info("ProcessPositiveTelcoAlarmList ::techAlarmDTO Added");
						logger.info(thisDto.toString());
					}
				}
			}
		}
	}

	// Change _downMDBList to Technical Alarm List
	public void processPositiveMdbAlarms(List<TechnicalAlarmDto> deviceList) {
		List<TPNDRaisedAlarm> mdbAlarmList = new ArrayList<TPNDRaisedAlarm>();

		for (SiteSupportEquip mdbAlarmInfo : downMDBList) {
			TechnicalAlarmDto techAlarmDto = generateMDBTELTechAlarmDTORaised(mdbAlarmInfo,
					TPNDConstants.TPND_MDB_TYPE);

			synchronized (sendingListLock) {
				boolean added = techAlarmSendingList.add(techAlarmDto);
				if (!added) {
					if (logger.isInfoEnabled()) {
						logger.info("ProcessPositiveMDBAlarms ::techAlarmDTO for MDB :" + mdbAlarmInfo.getEquipId()
								+ " Add failed");
					}
				} else {
					if (logger.isInfoEnabled()) {
						logger.info("ProcessPositiveMDBAlarms ::techAlarmDTO Added for MDB");
						logger.info(techAlarmDto.toString());
					}

					// Save process MDB alarms and their related Dtos
					TPNDRaisedAlarm newProcessedAlarm = new TPNDRaisedAlarm();
					newProcessedAlarm.setAlarmDto(techAlarmDto);

					List<String> relatedEquipList = tpndHandler.getEquipListBySiteSupportId(techAlarmDto.getEquipId());
					List<TechnicalAlarmDto> relatedEquipDto = new ArrayList<TechnicalAlarmDto>();
					for (String relatedEquipId : relatedEquipList) {
						for (TechnicalAlarmDto deviceDto : deviceList) {
							if (deviceDto.getEquipId().contentEquals(relatedEquipId)) {
								relatedEquipDto.add(deviceDto);
								break;
							}
						}
					}
					newProcessedAlarm.setEquipDtoList(relatedEquipDto);

					mdbAlarmList.add(newProcessedAlarm);
				}
			}

		}

		synchronized (processedListLock) {
			taProcessedList.addAll(mdbAlarmList);
		}

	}

	// Change _downTelcoList to Technical Alarm List
	public void processPositiveTelAlarms(List<TechnicalAlarmDto> deviceList) {
		List<TPNDRaisedAlarm> telcoAlarmList = new ArrayList<TPNDRaisedAlarm>();

		for (SiteSupportEquip telcoAlarmInfo : downTelcoList) {
			TechnicalAlarmDto techAlarmDto = generateMDBTELTechAlarmDTORaised(telcoAlarmInfo,
					TPNDConstants.TPND_TELCO_TYPE);

			// remove Technical Alarms related to Telco (combined)
			synchronized (sendingListLock) {
				boolean added = techAlarmSendingList.add(techAlarmDto);
				if (!added) {
					if (logger.isInfoEnabled()) {
						logger.info("ProcessPositiveTelcoAlarmList ::techAlarmDTO for Telco :"
								+ telcoAlarmInfo.getEquipId() + " Add failed");
					}
				} else {
					if (logger.isInfoEnabled()) {
						logger.info("ProcessPositiveTelcoAlarmList ::techAlarmDTO Added for Telco");
						logger.info(techAlarmDto.toString());
					}

					// Save process Telco alarms
					TPNDRaisedAlarm newProcessedAlarm = new TPNDRaisedAlarm();
					newProcessedAlarm.setAlarmDto(techAlarmDto);

					List<String> relatedEquipList = tpndHandler.getEquipListBySiteSupportId(techAlarmDto.getEquipId());
					List<TechnicalAlarmDto> relatedEquipDto = new ArrayList<TechnicalAlarmDto>();
					for (String relatedEquipId : relatedEquipList) {
						for (TechnicalAlarmDto deviceDto : deviceList) {
							if (deviceDto.getEquipId().contentEquals(relatedEquipId)) {
								relatedEquipDto.add(deviceDto);
								break;
							}
						}
					}
					newProcessedAlarm.setEquipDtoList(relatedEquipDto);

					telcoAlarmList.add(newProcessedAlarm);
				}
			}
		}

		synchronized (processedListLock) {
			taProcessedList.addAll(telcoAlarmList);
		}

	}

	// Change _downPairList to Technical Alarm List
	public void processPositiveTpnAlarms(List<TechnicalAlarmDto> deviceList) {
		List<TPNDRaisedAlarm> tpnAlarmList = new ArrayList<TPNDRaisedAlarm>();

		for (SiteSupportPair PairAlarmInfo : downPairList) {
			TechnicalAlarmDto techAlarmDto = generateTpnTechAlarmDTORaised(PairAlarmInfo);

			// remove Technical Alarms related to Telco (combined)
			synchronized (sendingListLock) {
				boolean added = techAlarmSendingList.add(techAlarmDto);
				if (!added) {
					if (logger.isInfoEnabled()) {
						logger.info("processPositiveTPNAlarms ::techAlarmDTO for Pair :" + PairAlarmInfo.getPair()
								+ " Add failed");
					}
				} else {
					if (logger.isInfoEnabled()) {
						logger.info("processPositiveTPNAlarms ::techAlarmDTO Added for Pair");
						logger.info(techAlarmDto.toString());
					}

					// Saved process TPN alarms
					TPNDRaisedAlarm newProcessedAlarm = new TPNDRaisedAlarm();
					newProcessedAlarm.setAlarmDto(techAlarmDto);

					List<String> relatedEquipList = tpndHandler.getEquipListBySiteSupportId(PairAlarmInfo.getMdb());
					List<TechnicalAlarmDto> relatedEquipDto = new ArrayList<TechnicalAlarmDto>();
					for (String relatedEquipId : relatedEquipList) {
						for (TechnicalAlarmDto deviceDto : deviceList) {
							if (deviceDto.getEquipId().contentEquals(relatedEquipId)) {
								relatedEquipDto.add(deviceDto);
								break;
							}
						}
					}
					newProcessedAlarm.setEquipDtoList(relatedEquipDto);

					tpnAlarmList.add(newProcessedAlarm);
				}
			}
		}

		synchronized (processedListLock) {
			taProcessedList.addAll(tpnAlarmList);
		}
	}

	public boolean isLastTpnPairCleared(TechnicalAlarmDto mdbAlarmDTO) {
		boolean result = false;
		List<TechnicalAlarm> thisList = tpndHandler.findTPNPairAlarmByMDB(mdbAlarmDTO.getEquipId());
		if ((thisList == null) || thisList.isEmpty()) {
			result = true;
		}

		return result;
	}

	public TechnicalAlarmDto generateTpnTechAlarmDTORaised(SiteSupportPair thisPair) {
		TechnicalAlarmDto techAlarmDto = new TechnicalAlarmDto();

		techAlarmDto.setEquipId(thisPair.getPair());
		techAlarmDto.setAlarmCode(Constants.LINK_DOWN);
		techAlarmDto.setAlarmId(TPNDConstants.MSG_HEADER + thisPair.getPair() + TPNDConstants.MSG_CODE);
		techAlarmDto.setStartDate(thisPair.getStartDate());
		techAlarmDto.setStatus(Constants.ALARM_RAISED);
		techAlarmDto.setSystemId(Constants.SYSTEM_ID);
		techAlarmDto.setEquipType(TPNDConstants.TPND_PAIR_TYPE);

		return techAlarmDto;
	}

	public TechnicalAlarmDto generateMDBTELTechAlarmDTORaised(SiteSupportEquip thisEquip, String equipType) {
		TechnicalAlarmDto techAlarmDto = new TechnicalAlarmDto();

		techAlarmDto.setEquipId(thisEquip.getEquipId());
		techAlarmDto.setAlarmCode(Constants.LINK_DOWN);
		techAlarmDto.setAlarmId(TPNDConstants.MSG_HEADER + thisEquip.getEquipId() + TPNDConstants.MSG_CODE);
		techAlarmDto.setStartDate(thisEquip.getStartDate());
		techAlarmDto.setStatus(Constants.ALARM_RAISED);
		techAlarmDto.setSystemId(Constants.SYSTEM_ID);
		techAlarmDto.setEquipType(equipType);

		return techAlarmDto;
	}

	public TechnicalAlarmDto generateTPNDTechAlarmDTOCleared(String equipId, TechnicalAlarmDto thisDto) {
		TechnicalAlarmDto techAlarmDto = new TechnicalAlarmDto();

		EquipConfig thisEquipConfig = tpndHandler.findEquipConfigById(equipId);
		int thisAlarmCode;
		if (!thisEquipConfig.getSubSystemId().equals(TPNDConstants.TPND_TICSS_SYSTEM)) {
			thisAlarmCode = 1;
		} else {
			thisAlarmCode = 0;
		}
		String thisEquipAlarmID = TPNDConstants.MSG_HEADER + equipId + TPNDConstants.MSG_JOINT
				+ String.valueOf(thisAlarmCode);

		techAlarmDto.setEquipId(equipId);
		techAlarmDto.setAlarmCode(thisAlarmCode);
		techAlarmDto.setAlarmId(thisEquipAlarmID);
		techAlarmDto.setStartDate(thisDto.getStartDate());
		techAlarmDto.setStatus(Constants.ALARM_CLEARED);
		techAlarmDto.setSystemId(Constants.SYSTEM_ID);
		techAlarmDto.setEquipType(thisEquipConfig.getEquipType());

		return techAlarmDto;
	}

	public TechnicalAlarmDto generateTPNDTechAlarmDTOCleared(TechnicalAlarmDto thisDto, String thisEquipId,
			String thisEquipType) {
		TechnicalAlarmDto techAlarmDto = new TechnicalAlarmDto();

		techAlarmDto.setEquipId(thisEquipId);
		techAlarmDto.setAlarmCode(thisDto.getAlarmCode());
		techAlarmDto.setAlarmId(TPNDConstants.MSG_HEADER + thisEquipId + TPNDConstants.MSG_CODE);
		techAlarmDto.setStartDate(thisDto.getStartDate());
		techAlarmDto.setStatus(Constants.ALARM_CLEARED);
		techAlarmDto.setSystemId(Constants.SYSTEM_ID);
		techAlarmDto.setEquipType(thisEquipType);

		return techAlarmDto;
	}

	public List<String> findDeviceList(String siteSupportId) {
		return tpndHandler.getEquipListBySiteSupportId(siteSupportId);
	}

	public List<String> findTelcoListByMDB(String mdbId) {
		return tpndHandler.getTelcoListByMdbId(mdbId);
	}

	public boolean isDeviceInTICSS(TechnicalAlarmDto pTechAlarmDto) {
		for (String thisEquipType : TPNDConstants.TICSS_TYPE_ARRAY) {
			if (thisEquipType.equals(pTechAlarmDto.getEquipType())) {
				return true;
			}
		}
		return false;
	}

	public SystemParameter findSystemParameter(String pName) {
		return tpndHandler.findSystemParameter(pName);
	}

	public boolean timeToUpdateFromDb() {
		boolean result = false;
		
		Date curDate = new Date();
		long diff = Math.abs(curDate.getTime() - this.updateDate.getTime());

		if (diff > TPNDConstants.TPND_1_HOUR) {
			result = true;
		}
		return result;

	}
	
	public void updateFromDatabase() {
		if (timeToUpdateFromDb()) {
			logger.info("updateFromDatabase :: Updating System Parameters....");
			SystemParameter systemParameter1 = findSystemParameter("TPND_NORMAL_WAITTIME");
			sleepTime = Integer.parseInt(systemParameter1.getValue());
			sleepTime *= TPNDConstants.TPND_1_SECOND;

			SystemParameter systemParameter2 = findSystemParameter("TPND_WITHTICSS_WAITTIME");
			tpndWithTicssWaittime = Integer.parseInt(systemParameter2.getValue());
			tpndWithTicssWaittime *= TPNDConstants.TPND_1_MINUTE;

			SystemParameter systemParameter3 = findSystemParameter("TPND_WITHOUTTICSS_WAITTIME");
			tpndWithoutTicssWaittime = Integer.parseInt(systemParameter3.getValue());
			tpndWithoutTicssWaittime *= TPNDConstants.TPND_1_MINUTE;

			SystemParameter systemParameter4 = findSystemParameter("TPND_RECOVER_WAITTIME");
			tpndRecoverWaittime = Integer.parseInt(systemParameter4.getValue());
			tpndRecoverWaittime *= TPNDConstants.TPND_1_MINUTE;
			
			SystemParameter systemParameter5 = findSystemParameter("SPECIAL_EQUIP_TYPE");
			String readEquipTypeArray = systemParameter5.getValue();
			equipTypeArray = StringUtils.split(readEquipTypeArray, ',');

			setUpdateDate(new Date());
		}
	}

	private List<SiteSupportPair> removePairDuplicates(List<SiteSupportPair> thisList) {
		List<SiteSupportPair> newDownPairList = new ArrayList<SiteSupportPair>();

		for (SiteSupportPair thisDownPair : thisList) {
			if (newDownPairList.isEmpty()) {
				newDownPairList.add(thisDownPair);
			}

			boolean matched = false;
			for (SiteSupportPair newDownPair : newDownPairList) {
				if (newDownPair.getPair().equals(thisDownPair.getPair())) {
					matched = true;
					break;
				}
			}

			if (!matched) {
				newDownPairList.add(thisDownPair);
			}

		}

		return newDownPairList;
	}

	public void destroyTpndProcess() {
		logger.info("TPND Manager :: Destroying TPNDProcess Thread");
		
		// destroying bean to kill off the thread
		DefaultListableBeanFactory factory = (DefaultListableBeanFactory) applicationContext.getParentBeanFactory();
		factory.destroySingleton("tpndProcess");
	}

	public void startTpndProcess() {
		logger.info("TPND Manager :: Starting TPNDProcess Thread");
		
		// Recreate and re-autowired the new tpndProcess
		if (!tpndProcess.isAlive()) {
			tpndProcess = null;
			tpndProcess = new TPNDProcess();
			beanFactory.autowireBean(tpndProcess);
			tpndProcess.start();			
		}
	}
	
	public TechnicalAlarm findTechnicalAlarmById(String pId) {
		return tpndHandler.findTechnicalAlarmById(pId);
	}

	public TechnicalAlarm getTPNTechnicalAlarmByEquipId(String pId) {
		return tpndHandler.getTPNTechnicalAlarmByEquipId(pId);
	}

	public EquipConfig findEquipConfigById(String equipId) {
		return tpndHandler.findEquipConfigById(equipId);
	}

	// New clearing queue to hold clearing alarms during TPND in processing state.
	// 18 Sep 21 implemented by grace
	public void addToClearingList(TechnicalAlarmDto lTechAlarmDto) {
		boolean added = false;
		if (tpndProcess.getProcessingState()) {
			if (!isTechAlarmSafe(lTechAlarmDto)) {
				logger.error("addToClearingList :: Technical Alarm Error : " + lTechAlarmDto.getAlarmId());
			}

			synchronized (clearingListLock) {
				for (TechnicalAlarmDto thisDto : techAlarmClearingList) {
					if (thisDto.getEquipId().equals(lTechAlarmDto.getEquipId())) {
						added = true;
						break;
					}
				}

				if (!added) {
					logger.info("addToClearingList :: Process Thread is processing. Clear Alarm added to ClearingList :"
							+ lTechAlarmDto.getAlarmId());
					techAlarmClearingList.add(lTechAlarmDto);
				}
			}
		}
	}
	
	public boolean isTechAlarmSafe(TechnicalAlarmDto lTechAlarmDto) {
		boolean result = true;

		if (lTechAlarmDto.getEquipId() == null) {
			logger.error("isTechAlarmSafe :: Alarm " + lTechAlarmDto.getAlarmId() + " is missing equipId");
			result = false;
		}
		
		if (lTechAlarmDto.getEquipType() == null) {
			logger.error("isTechAlarmSafe :: Alarm " + lTechAlarmDto.getAlarmId() + " is missing equipType");
			result = false;
		}

		return result;
	}

	/***********************************/
	// below functions are to be called for unit test
	public TPNDManager() {
		taGroupHoldingList = new ArrayList<TechAlarmsGroup>();
		techAlarmSendingList = new ArrayList<TechnicalAlarmDto>();
		taProcessedList = new ArrayList<TPNDRaisedAlarm>();

		downMDBList = new ArrayList<SiteSupportEquip>();
		downTelcoList = new ArrayList<SiteSupportEquip>();
		downPairList = new ArrayList<SiteSupportPair>();

		updateDate = new Date();
		tpndWithTicssWaittime = 5 * TPNDConstants.TPND_1_MINUTE;
		tpndWithoutTicssWaittime = 3 * TPNDConstants.TPND_1_MINUTE;
		tpndRecoverWaittime = 2 * TPNDConstants.TPND_1_MINUTE;
	}

	public List<SiteSupportPair> testRemovePairDuplicates(List<SiteSupportPair> thisList) {
		return removePairDuplicates(thisList);
	}

}