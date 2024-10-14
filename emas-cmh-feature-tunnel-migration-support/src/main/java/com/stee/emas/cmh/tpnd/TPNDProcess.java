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
 * <p>Description : TPNDProcessThread</p>
 * <p>Process thread for TPND. It will wait a specific amount of time and then process</p> 
 * <p>received alarm list</p> 
 * <p></p> 
 * <p>Copyright: Copyright (c) 2019 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Grace
 * @since Jul 18, 2019
 * @version 1.0
 */

package com.stee.emas.cmh.tpnd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stee.emas.cmh.integration.CMHMessageHandler;
import com.stee.emas.cmh.tpnd.common.TPNDConstants;
import com.stee.emas.cmh.tpnd.common.TPNDRaisedAlarm;
import com.stee.emas.cmh.tpnd.common.TechAlarmsGroup;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emas.common.entity.SystemParameter;

@Component("tpndProcess")
public class TPNDProcess extends Thread {
	private static Logger logger = LoggerFactory.getLogger(TPNDProcess.class);

	@Autowired
	TPNDManager tpndManager;
	@Autowired
	CMHMessageHandler cmhMessageHandler;
	
	private boolean processingState;
	
	public boolean getProcessingState() {
		return processingState;
	}

	@Override
	public void run() {
		// Added by grace 21/12/20
		if (logger.isInfoEnabled()) {
			logger.info("***********************************");
			logger.info("***** TPND Process is running *****");
			logger.info("***********************************");
		}
		SystemParameter systemParameter = tpndManager.findSystemParameter("TPND_NORMAL_WAITTIME");
		if (systemParameter != null) {
			long temp = Long.parseLong(systemParameter.getValue());
			logger.info("_sleepTime = " + temp + " seconds");
			temp *= TPNDConstants.TPND_1_SECOND;
			tpndManager.setSleepTime(temp);
		} else {
			logger.info("TPND_NORMAL_WAITTIME is missing from database");
		}

		processingState = false;
		
		while (true) {
			try {
				tpndManager.updateFromDatabase();
				
				processTAGroups();
				// New clearing queue to hold clearing alarms during TPND in processing state.
				// Need to do a check again if any technical alarm clear is being sent while the
				// technical alarm is being processed.
				// 18 Sep 21 implemented by grace 
				sendClearAlarmsDuringTpndProcess();
				recoverTAGroups();
				
				Thread.sleep(tpndManager.getSleepTime());
			} catch (Exception e) {
				logger.error("TPND Process :: Exception caught : ", e);
				tpndManager.destroyTpndProcess();
			}
		} // while
	}
	
	private void processTAGroups() {
		List<TechAlarmsGroup> toProcessList = tpndManager.findTAGroupsToProcess();
		if (!toProcessList.isEmpty()) {
			processingState = true;
			logger.info("TPNDProcess:: Process Technical Alarms into Groups");
			// To combine all technical alarms into TPND equipments if able to.
			tpndManager.processIntoTpnDownEquipments(toProcessList);

			// To removed those alarms from equipments that have been combined into TPND
			// equipments
			List<TechnicalAlarmDto> removedDeviceList = new ArrayList<TechnicalAlarmDto>();
			if (TPNDConstants.FinalVersion) {
				List<TechnicalAlarmDto> mdbLinkedList = tpndManager.getAllMDBLinkedEquips(toProcessList);
				List<TechnicalAlarmDto> telcoLinkedList = tpndManager.getAllTelcoLinkedEquips(toProcessList);
				removedDeviceList.addAll(mdbLinkedList);
				removedDeviceList.addAll(telcoLinkedList);
			} else {
				List<TechnicalAlarmDto> mdbLinkedList = tpndManager.getAllMDBLinkedEquips(toProcessList);
				removedDeviceList.addAll(mdbLinkedList);
			}

			// further remove SiteSupport alarms that have been combined into "Pair" alarms
			if (TPNDConstants.WithTPN) {
				List<TechnicalAlarmDto> removePairList = tpndManager.getPairLinkedSiteSupports();
				removedDeviceList.addAll(removePairList);
			}

			// To update the equipment status of those equipments whose alarm that have been
			// removed
			tpndManager.updateTPNDRemovedEquipStatus(removedDeviceList);

			// Set MDB and Telco that are down into technical alarm and store into received
			// Alarm list.
			tpndManager.processPositiveIndividualAlarms(toProcessList);
			if (TPNDConstants.FinalVersion) {
				tpndManager.processPositiveMdbAlarms(removedDeviceList);
				tpndManager.processPositiveTelAlarms(removedDeviceList);

				// Set Pair that are down into alarms and store into received Alarm list.
				if (TPNDConstants.WithTPN) {
					tpndManager.processPositiveTpnAlarms(removedDeviceList);
				}
			} else {
				List<TechnicalAlarmDto> equipDtoList = new ArrayList<TechnicalAlarmDto>();
				for (TechAlarmsGroup thisGroup : toProcessList) {
					for (TechnicalAlarmDto thisDto : thisGroup.getTAList()) {
						equipDtoList.add(thisDto);
					}
				}

				tpndManager.processPositiveMdbAlarms(equipDtoList);
				tpndManager.processPositiveTelAlarms(equipDtoList);

				// Set Pair that are down into alarms and store into received Alarm list.
				if (TPNDConstants.WithTPN) {
					tpndManager.processPositiveTpnAlarms(equipDtoList);
				}
			}

			List<TechnicalAlarmDto> lTechAlarmList = tpndManager.getSendingList();
			// Release _receivedTechAlarmList into CMH again for processing
			if (!lTechAlarmList.isEmpty()) {
				TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
				lTechAlarmDtoList.setDtoList(lTechAlarmList);
				cmhMessageHandler.handleTechAlarmList(lTechAlarmDtoList, TPNDConstants.TPND, null);
			}

			// To reset whole system for new batch of processing
			processingState = false;
			tpndManager.reset();
			toProcessList.clear();
			/* Codes added to clear list to free space - 03/02/22 GH */
			toProcessList = null;
			/********************/
		}
	}
	
	private void recoverTAGroups() {
		// To go through processed TPND alarms to see if need to be recovered.
		List<TPNDRaisedAlarm> toRecoverList = tpndManager.findAlarmsToRecover();
		if (!toRecoverList.isEmpty()) {
			logger.info("TPNDProcess:: Recover Technical Alarms From Groups");
			List<TechnicalAlarmDto> toRecoverTechnicalAlarmDto = new ArrayList<TechnicalAlarmDto>();
			List<TechnicalAlarmDto> toReleaseTechnicalAlarmDto = new ArrayList<TechnicalAlarmDto>();

			for (TPNDRaisedAlarm recoverAlarm : toRecoverList) {
				TechnicalAlarmDto recoverDto = recoverAlarm.getAlarmDto();
				recoverDto.setStartDate(new Date());
				recoverDto.setStatus(Constants.ALARM_CLEARED);
				toRecoverTechnicalAlarmDto.add(recoverDto);

				if (TPNDConstants.FinalVersion) {
					for (TechnicalAlarmDto unrecoveredEquip : recoverAlarm.getEquipDtoList()) {
						toReleaseTechnicalAlarmDto.add(unrecoveredEquip);
					}
				}
			}

			// Release toProcessTechnicalAlarmDto into CMH again for processing (Alarms to
			// recover)
			if (!toRecoverTechnicalAlarmDto.isEmpty()) {
				TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
				lTechAlarmDtoList.setDtoList(toRecoverTechnicalAlarmDto);
				cmhMessageHandler.handleTechAlarmList(lTechAlarmDtoList, TPNDConstants.TPND_RECOVERY, null);
			}

			if (TPNDConstants.FinalVersion) {
				// Release toReleaseTechnicalAlarmDto into CMH again for processing (Alarms not
				// yet recover)
				if (!toReleaseTechnicalAlarmDto.isEmpty()) {
					TechAlarmDtoList lTechAlarmDtoList2 = new TechAlarmDtoList();
					lTechAlarmDtoList2.setDtoList(toReleaseTechnicalAlarmDto);
					cmhMessageHandler.handleTechAlarmList(lTechAlarmDtoList2, TPNDConstants.TPND_RECOVERY, null);
				}
			}

			toRecoverList.clear();
			/* Codes added to clear list to free space - 03/02/22 GH */
			toRecoverList = null;
			/********************/
		}
	}
	
	// New clearing queue to hold clearing alarms during TPND in processing state.
	// 18 Sep 21 implemented by grace 
	private void sendClearAlarmsDuringTpndProcess() {
		TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
		List<TechnicalAlarmDto> lTechAlarmList = tpndManager.getClearingList();		
		if (lTechAlarmList.isEmpty()) {
			return;
		}
		
		// Release clearingList into CMH again for processing
		lTechAlarmDtoList.setDtoList(lTechAlarmList);
		cmhMessageHandler.handleTechAlarmList(lTechAlarmDtoList, new String(), null);

		// To reset ClearingList
		tpndManager.clearClearingList();
	}
}