/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.integration;

import java.util.*;
import java.util.function.Supplier;

import com.stee.emas.common.tunnel.lus.StatusCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stee.emas.cmh.caid.CongestionAlertHandler;
import com.stee.emas.cmh.common.CMHKeyBean;
import com.stee.emas.cmh.service.CMHManager;
import com.stee.emas.cmh.service.EquipStatusManager;
import com.stee.emas.cmh.service.TechnicalAlarmManager;
import com.stee.emas.cmh.service.TrafficAlertManager;
import com.stee.emas.cmh.service.VmsMsgManager;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.dto.CmdRespDto;
import com.stee.emas.common.dto.DimmingDto;
import com.stee.emas.common.dto.EquipStatusDto;
import com.stee.emas.common.dto.EquipStatusDtoList;
import com.stee.emas.common.dto.FanOpeModeDto;
import com.stee.emas.common.dto.FlashingTimeDto;
import com.stee.emas.common.dto.ImageSequenceDto;
import com.stee.emas.common.dto.PictogramDto;
import com.stee.emas.common.dto.PixelFailureBMPFileDto;
import com.stee.emas.common.dto.PreviewImageDto;
import com.stee.emas.common.dto.ResetDto;
import com.stee.emas.common.dto.TechAlarmAckDto;
import com.stee.emas.common.dto.TechAlarmAckDtoList;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emas.common.dto.TrafficAlertAckDtoList;
import com.stee.emas.common.dto.TrafficAlertClearDto;
import com.stee.emas.common.dto.TrafficAlertClearDtoList;
import com.stee.emas.common.dto.TrafficAlertDto;
import com.stee.emas.common.dto.VmsMsgDto;
import com.stee.emas.common.dto.VmsMsgDtoList;
import com.stee.emas.common.dto.VmsTimetableConfigDto;
import com.stee.emas.common.entity.TechnicalAlarm;
import com.stee.emas.common.util.MessageConverterUtil;

/***** Changed by Grace 18/12/19 *****/
import org.apache.commons.lang.StringUtils;
import com.stee.emas.cmh.tpnd.TPNDManager;
import com.stee.emas.cmh.tpnd.common.TPNDConstants;

/***** Changed by Grace 18/12/19 *****/

/**
 * Title: EMAS Enhancement Project
 * Description : Message Handler for handling message into CMH
 * This class is used to handle message coming into CMH
 * Copyright: Copyright (c) 2012
 * Company:STEE-InfoComm
 * 
 * @author Scindia
 * @since Sep 26, 2012
 * @version 1.0
 *
 */

@Component("cmhMessageHandler")
public class CMHMessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(CMHMessageHandler.class);

	private static final Logger rtLogger = LoggerFactory.getLogger("responsetime");

	public static Hashtable<CMHKeyBean, String> cmhHtable = new Hashtable<CMHKeyBean, String>();
	//public static Hashtable<String, String> pixelHtable = new Hashtable<String, String>();

	@Autowired
	CMHManager cmhManager;
	@Autowired
	TechnicalAlarmManager techAlarmManager;
	@Autowired
	EquipStatusManager equipStatusManager;
	@Autowired
	TrafficAlertManager trafficAlertManager;
	@Autowired
	VmsMsgManager vmsMsgManager;
	@Autowired
	MessageConverterUtil messageConverterUtil;
	@Autowired
	CMHMessageSender cmhMessageSender;
	@Autowired
	CongestionAlertHandler congestionAlertHandler;
	/***** Changed by Grace 18/12/19 *****/
	@Autowired
	TPNDManager tpndManager;
	/*************************************/

	public void handleEquipStatusList(EquipStatusDtoList pEquipStatusDtoList) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handleEquipStatusList -> Start.....");
		}
		List<EquipStatusDto> lChangedEquipStatusDtoList = equipStatusManager.processEquipStatus(pEquipStatusDtoList);

		if (lChangedEquipStatusDtoList != null) {
			EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList();
			lEquipStatusDtoList.setDtoList(lChangedEquipStatusDtoList);
			if (lEquipStatusDtoList.getDtoList().size() > 0) {
				List<EquipStatusDto> lAWEquipStatusList = new ArrayList<EquipStatusDto>();
				for (EquipStatusDto lEquipStatusDto : lEquipStatusDtoList.getDtoList()) {
					if (lEquipStatusDto.getStatusCode().equals(Constants.OPE_STATE)) {
						lAWEquipStatusList.add(lEquipStatusDto);
					}
				}
				if (lAWEquipStatusList.size() > 0) {
					EquipStatusDtoList lAWEquipStatusDtoList = new EquipStatusDtoList();
					lAWEquipStatusDtoList.setDtoList(lAWEquipStatusList);

					logger.info("Object sent to the AW queue " + lAWEquipStatusDtoList);
					cmhMessageSender.sendAWJmsMessage("EquipStatus", MessageConstants.EQUIP_STATUS_ID);
				}
				logger.info("Object sent to the CCS-Intf queue " + lEquipStatusDtoList);
				cmhMessageSender.sendEmasCcsJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
				// Added 04/03/22 GH - To implement new queues to ITPT-INTF
				cmhMessageSender.sendEmasItptJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			}
		} else {
			logger.info("Changed EquipStatus List is null or empty .....");
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleEquipStatusList .....");
		}
	}

	public void handleTechAlarmList(TechAlarmDtoList pTechAlarmDtoList, String pSource, String pClearBy) {
		if (TPNDConstants.TPNDFlag) {
			if (logger.isInfoEnabled()) {
				logger.info("Inside Handler :: handleTechAlarmList -> Start.....");
			}
			boolean lSendToQueue = false;
			/***** Changed by Grace 18/12/19 *****/
			boolean lSendToQueueClearedbyTPND = false;
			/*************************************/

			int lTechAlarmStatus;
			ArrayList<TechnicalAlarmDto> lTechAlarmList = new ArrayList<TechnicalAlarmDto>();
			TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
			/***** Changed by Grace 18/12/19 *****/
			ArrayList<TechnicalAlarmDto> lTechAlarmListClearedByTPND = new ArrayList<TechnicalAlarmDto>();
			/*************************************/

			logger.info("pTechAlarmDtoList.getDtoList() :: " + pTechAlarmDtoList.getDtoList());
			if (pTechAlarmDtoList.getDtoList() != null) {
				for (TechnicalAlarmDto lTechAlarmDto : pTechAlarmDtoList.getDtoList()) {
					logger.info("lTechAlarmDto.getEquipId() :: " + lTechAlarmDto.getEquipId());
					logger.info("lTechAlarmDto.getAlarmId() :: " + lTechAlarmDto.getAlarmId());
					logger.info("lTechAlarmDto.getAlarmCode() :: " + lTechAlarmDto.getAlarmCode());
					lTechAlarmStatus = lTechAlarmDto.getStatus();
					TechnicalAlarm lTechnicalAlarm = messageConverterUtil.convertTechAlarmDtoToEntity(lTechAlarmDto);
					if (lTechAlarmStatus == Constants.ALARM_CLEARED) {
						try {
							techAlarmManager.saveHistTechnialAlarm(lTechnicalAlarm, pClearBy);
						} catch (Exception e) {
							logger.error("Error in saving hist Technical alarm for ALARM_CLEARED...", e);
						}
						/***** Changed by Grace 18/12/19 *****/
						// remove from holding list and processed list for TPND if cleared from WCSS,
						// SCSS, TICSS, DCSS
						boolean clearedWhileHeld = false;
						boolean removedfromProcessedList = false;
						if (((StringUtils.isBlank(pSource)) && (lTechAlarmDto.getAlarmCode() == Constants.LINK_DOWN)
								// Added on 11/04/22 for IDSS implementation into TPND
								&& (!tpndManager.isDeviceInTICSS(lTechAlarmDto)))
								|| ((StringUtils.isBlank(pSource))
										&& (lTechAlarmDto.getAlarmCode() == TPNDConstants.TICSS_LINK_DOWN)
										&& (tpndManager.isDeviceInTICSS(lTechAlarmDto)))) {
							clearedWhileHeld = tpndManager.removeFromTAGroupHoldingList(lTechAlarmDto);
							//tpndManager.removeFromTAGroupHoldingList(lTechAlarmDto);
						}

						removedfromProcessedList = tpndManager.removeFromProcessedHoldingList(lTechAlarmDto);

						// To add the clear alarm into clearingList so as to send process clear alarm
						// while the alarms are being processed.
						// New clearing queue to hold clearing alarms during TPND in processing state.
						// 18 Sep 21 implemented by grace 
						if ((!removedfromProcessedList) && (!clearedWhileHeld)) {
							tpndManager.addToClearingList(lTechAlarmDto);
						}
						/*************************************/

						try {
							lSendToQueue = techAlarmManager.processTechAlarmClear(lTechnicalAlarm, pClearBy);

							/***** Changed by Grace 18/12/19 *****/
							if (!lSendToQueue) { // if equipment alarm (non-TPND) has been cleared by TPND?
								// if (clearedWhileHeld) {
								// lSendToQueue = true;
								// } else {
								lSendToQueueClearedbyTPND = techAlarmManager
										.processTechAlarmClearedByTPND(lTechnicalAlarm, pClearBy);
								// }
							}

							/*************************************/
						} catch (Exception e) {
							lSendToQueue = false;
							logger.error("Error in processing Technical alarm Clear...", e);
						}
					} else if (lTechAlarmStatus == Constants.ALARM_RAISED) {
						/***** Changed by Grace 18/12/19 *****/
						// Activate TPND only for alarms that are having LINK_DOWN error and is not from
						// TPND
						// If they are from TPND, they are being sent from TPND thus do not need to
						// route back to TPND again
						if (((StringUtils.isBlank(pSource)) && (lTechAlarmDto.getAlarmCode() == Constants.LINK_DOWN) && (!tpndManager.isDeviceInTICSS(lTechAlarmDto)))
								|| ((StringUtils.isBlank(pSource)) && (lTechAlarmDto.getAlarmCode() == TPNDConstants.TICSS_LINK_DOWN)
										&& (tpndManager.isDeviceInTICSS(lTechAlarmDto)))) {
							// if AlarmCode is Constants.LINK_DOWN, enters into TPND mode
							// For TPND, cannot write into DB until processing has been done and all alarms
							// has been confirmed as it is.
							// Thus the thing to do is to save into holding list.
							// if EquipType is TPND related, the alarm is from TPND one so cannot go back
							// into processing again.
							// logger.info("lTechAlarmDto.getEquipId() :: " +
							// lTechnicalAlarm.getEquipConfig().getEquipId() +" added into TPND list");
							logger.info("lTechAlarmDto.getEquipId() :: " + lTechnicalAlarm.getEquipId()
									+ " to add to TPND");
							tpndManager.addToTAGroupHoldingList(lTechAlarmDto);
							lSendToQueue = false; // Do not send to queue as alarm is in holding list
						} else {
							try {
								// logger.info("lTechAlarmDto.getEquipId() :: " +
								// lTechnicalAlarm.getEquipConfig().getEquipId());
								logger.info("lTechAlarmDto.getEquipId() :: " + lTechnicalAlarm.getEquipId());
								lSendToQueue = techAlarmManager.processTechAlarmRaise(lTechnicalAlarm);
							} catch (Exception e) {
								lSendToQueue = false;
								logger.error("Error in processing Technical alarm for ALARM_RAISED...", e);
							}
						}
						/*************************************/
					}

					logger.info("SendToQueue :: " + lSendToQueue + " for AlarmId :: " + lTechAlarmDto.getAlarmId());
					if (lSendToQueue) {
						lTechAlarmList.add(lTechAlarmDto);
					}
					/***** Changed by Grace 18/12/19 *****/
					logger.info("SendToQueueClearedbyTPND :: " + lSendToQueueClearedbyTPND + " for AlarmId :: "
							+ lTechAlarmDto.getAlarmId());
					if (lSendToQueueClearedbyTPND) {
						lTechAlarmListClearedByTPND.add(lTechAlarmDto);
					}
					/*************************************/
				}
			}

			if (lTechAlarmList.size() > 0) {
				lTechAlarmDtoList.setDtoList(lTechAlarmList);
			}

			logger.debug("Source of the Object :: " + pSource);
			if (!pSource.equals(Constants.EMASCCS_SENDER)) {
				if (lTechAlarmDtoList != null && lTechAlarmDtoList.getDtoList() != null
						&& lTechAlarmDtoList.getDtoList().size() > 0) {
					logger.info("Object sent to the queue " + lTechAlarmDtoList);
					cmhMessageSender.sendEmasCcsJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
					// Added 04/03/22 GH - To implement new queues to ITPT-INTF
					cmhMessageSender.sendEmasItptJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
				}
			}
			if (lTechAlarmDtoList != null && lTechAlarmDtoList.getDtoList() != null
					&& lTechAlarmDtoList.getDtoList().size() > 0) {
				logger.info("Object sent to the queue " + lTechAlarmDtoList);
				cmhMessageSender.sendAWJmsMessage("TechnicalAlarm", MessageConstants.TECH_ALARM_ID);
			}

			processEquipStatusForTechnicalAlarm(lTechAlarmDtoList, pSource);
			/***** Changed by Grace 18/12/19 *****/
			if (lSendToQueueClearedbyTPND) {
				if (lTechAlarmListClearedByTPND.size() > 0) {
					lTechAlarmDtoList.setDtoList(lTechAlarmListClearedByTPND);
				}

				processEquipStatusForTechnicalAlarm(lTechAlarmDtoList, pSource);
			}
			/*************************************/

			if (logger.isInfoEnabled()) {
				logger.info("Exit Handler :: handleTechAlarmList .....");
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Inside Handler :: handleTechAlarmList -> Start.....");
			}
			boolean lSendToQueue = false;
			int lTechAlarmStatus;
			ArrayList<TechnicalAlarmDto> lTechAlarmList = new ArrayList<TechnicalAlarmDto>();
			TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();

			logger.info("pTechAlarmDtoList.getDtoList() :: " + pTechAlarmDtoList.getDtoList());
			if (pTechAlarmDtoList.getDtoList() != null) {
				for (TechnicalAlarmDto lTechAlarmDto : pTechAlarmDtoList.getDtoList()) {
					logger.info("lTechAlarmDto.getEquipId() :: " + lTechAlarmDto.getEquipId());
					logger.info("lTechAlarmDto.getAlarmId() :: " + lTechAlarmDto.getAlarmId());
					logger.info("lTechAlarmDto.getAlarmCode() :: " + lTechAlarmDto.getAlarmCode());
					lTechAlarmStatus = lTechAlarmDto.getStatus();
					TechnicalAlarm lTechnicalAlarm = messageConverterUtil.convertTechAlarmDtoToEntity(lTechAlarmDto);
					if (lTechAlarmStatus == Constants.ALARM_CLEARED) {
						try {
							techAlarmManager.saveHistTechnialAlarm(lTechnicalAlarm, pClearBy);
						} catch (Exception e) {
							logger.error("Error in saving hist Technical alarm Raise...", e);
						}
						try {
							lSendToQueue = techAlarmManager.processTechAlarmClear(lTechnicalAlarm, pClearBy);
						} catch (Exception e) {
							lSendToQueue = false;
							logger.error("Error in processing Technical alarm Clear...", e);
						}
					} else if (lTechAlarmStatus == Constants.ALARM_RAISED) {
						try {
							logger.info("lTechAlarmDto.getEquipId() :: " + lTechnicalAlarm.getEquipId());
							lSendToQueue = techAlarmManager.processTechAlarmRaise(lTechnicalAlarm);
						} catch (Exception e) {
							lSendToQueue = false;
							logger.error("Error in processing Technical alarm Raise...", e);
						}
					}
					logger.info("SendToQueue :: " + lSendToQueue + " for AlarmId :: " + lTechAlarmDto.getAlarmId());
					if (lSendToQueue) {
						lTechAlarmList.add(lTechAlarmDto);
					}
				}
			}
			if (lTechAlarmList.size() > 0) {
				lTechAlarmDtoList.setDtoList(lTechAlarmList);
			}
			logger.debug("Source of the Object :: " + pSource);
			if (!pSource.equals(Constants.EMASCCS_SENDER)) {
				if (lTechAlarmDtoList != null && lTechAlarmDtoList.getDtoList() != null
						&& lTechAlarmDtoList.getDtoList().size() > 0) {
					logger.info("Object sent to the queue " + lTechAlarmDtoList);
					cmhMessageSender.sendEmasCcsJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
					// Added 04/03/22 GH - To implement new queues to ITPT-INTF
					cmhMessageSender.sendEmasItptJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
				}
			}
			if (lTechAlarmDtoList != null && lTechAlarmDtoList.getDtoList() != null
					&& lTechAlarmDtoList.getDtoList().size() > 0) {
				logger.info("Object sent to the queue " + lTechAlarmDtoList);
				cmhMessageSender.sendAWJmsMessage("TechnicalAlarm", MessageConstants.TECH_ALARM_ID);
			}

			processEquipStatusForTechnicalAlarm(lTechAlarmDtoList, pSource);

			if (logger.isInfoEnabled()) {
				logger.info("Exit Handler :: handleTechAlarmList .....");
			}
		}
	}

	private void processEquipStatusForTechnicalAlarm(TechAlarmDtoList pTechAlarmDtoList, String pSource) {
		if (TPNDConstants.TPNDFlag) {
			if (logger.isInfoEnabled()) {
				logger.info("Calling processEquipStatusForTechnicalAlarm -> Start");
			}
			EquipStatusDto lEquipStatusDto = null;
			int lTechAlarmStatus;
			List<EquipStatusDto> lChangedEquipStatusList = new ArrayList<EquipStatusDto>();
			/***** Changed by Grace 18/12/19 *****/
			EquipStatusDto lEquipStatusDto2 = null;
			String lTechAlarmEquipType;
			/*************************************/

			if (pTechAlarmDtoList != null && pTechAlarmDtoList.getDtoList() != null
					&& pTechAlarmDtoList.getDtoList().size() > 0) {
				for (TechnicalAlarmDto lTechAlarmDto : pTechAlarmDtoList.getDtoList()) {
					lTechAlarmStatus = lTechAlarmDto.getStatus();
					/***** Changed by Grace 18/12/19 *****/
					if (!tpndManager.isTechAlarmSafe(lTechAlarmDto)) {
						logger.error("processEquipStatusForTechnicalAlarm :: Technical Alarm Error : "
										+ lTechAlarmDto.getAlarmId());
					}

					lTechAlarmEquipType = lTechAlarmDto.getEquipType();
					if ((!lTechAlarmEquipType.equals(TPNDConstants.TPND_MDB_TYPE))
							&& (!lTechAlarmEquipType.equals(TPNDConstants.TPND_TELCO_TYPE))
							&& (!lTechAlarmEquipType.equals(TPNDConstants.TPND_PAIR_TYPE))) { // Line added for TPND
						if (lTechAlarmStatus == Constants.ALARM_CLEARED) {
							try {
								lEquipStatusDto = equipStatusManager.generateEquipStatusForClearStatus(lTechAlarmDto);
							} catch (Exception e) {
								logger.error("Error processing EquipStatus for Technical alarm clear .....", e);
							}
						} else if (lTechAlarmStatus == Constants.ALARM_RAISED) {
							try {
								lEquipStatusDto = equipStatusManager.generateEquipStatusForAlarmRaised(lTechAlarmDto);
							} catch (Exception e) {
								logger.error("Error processing EquipStatus for Technical alarm raise .....", e);
							}
						}
						if (lEquipStatusDto != null) {
							lChangedEquipStatusList.add(lEquipStatusDto);
						}
					} else { // processing for TPND portion of alarms
						if (!lTechAlarmEquipType.equals(TPNDConstants.TPND_PAIR_TYPE)) { // TPND_MDB_TYPE and
																							// TPND_TELCO_TYPE
							if (lTechAlarmStatus == Constants.ALARM_CLEARED) {
								if (TPNDConstants.FinalVersion) {
									if (!pSource.equals(TPNDConstants.TPND_RECOVERY)) {
										tpndManager.updateTPNDRelatedEquipStatusUp(lTechAlarmDto);
									}

									if (lTechAlarmEquipType.equals(TPNDConstants.TPND_MDB_TYPE)) {
										tpndManager.processMdbRelatedTelcoStatusUp(lTechAlarmDto);
									}
								} else {
									tpndManager.updateTPNDRelatedEquipStatusUp(lTechAlarmDto);
								}

								try {
									lEquipStatusDto = equipStatusManager
											.generateTPNDEquipStatusForClearStatus(lTechAlarmDto);
								} catch (Exception e) {
									logger.error(
											"Error processing EquipStatus for MDB/Telco Technical alarm clear .....");
									logger.error("Alarm Id :" + lTechAlarmDto.getAlarmId() + " Start_Time :"
											+ lTechAlarmDto.getStartDate());
									logger.error(e.toString());
								}
							} else if (lTechAlarmStatus == Constants.ALARM_RAISED) {
								try {
									lEquipStatusDto = equipStatusManager
											.generateTPNDEquipStatusForAlarmRaised(lTechAlarmDto);
								} catch (Exception e) {
									logger.error(
											"Error processing EquipStatus for MDB/Telco Technical alarm raise .....",
											e);
								}
							}
							if (lEquipStatusDto != null) {
								lChangedEquipStatusList.add(lEquipStatusDto);
							}
						} else {
							String mdbID = "";
							String telcoID = "";
							mdbID = "mdb_" + lTechAlarmDto.getEquipId().substring(4, 10);
							String subTelcoID = lTechAlarmDto.getEquipId().substring(11);
							// Check linked telco to find Telco ID
							// AggregateInfo thisAggregate = tpndManager.findAggregateGroup(mdbID);
							List<String> relatedTelcoList = tpndManager.findTelcoListByMDB(mdbID);
							// for (String compareTelco : thisAggregate.getTelcoList()) {
							for (String compareTelco : relatedTelcoList) {
								if (compareTelco.substring(8).equals(subTelcoID)) {
									telcoID = compareTelco;
									break;
								}
							}

							// create the DTO for mdb in the Pair
							TechnicalAlarmDto mdbAlarmDto = new TechnicalAlarmDto();
							mdbAlarmDto.setEquipId(mdbID);
							mdbAlarmDto.setAlarmCode(Constants.LINK_DOWN);
							mdbAlarmDto.setAlarmId("emas_" + mdbID + "_1");
							mdbAlarmDto.setStartDate(lTechAlarmDto.getStartDate());
							mdbAlarmDto.setSystemId(Constants.SYSTEM_ID);
							mdbAlarmDto.setEquipType(TPNDConstants.TPND_MDB_TYPE);

							// create the DTO for telco in the Pair
							TechnicalAlarmDto telcoAlarmDto = new TechnicalAlarmDto();
							telcoAlarmDto.setEquipId(telcoID);
							telcoAlarmDto.setAlarmCode(Constants.LINK_DOWN);
							telcoAlarmDto.setAlarmId("emas_" + telcoID + "_1");
							telcoAlarmDto.setStartDate(lTechAlarmDto.getStartDate());
							telcoAlarmDto.setSystemId(Constants.SYSTEM_ID);
							telcoAlarmDto.setEquipType(TPNDConstants.TPND_TELCO_TYPE);

							if (lTechAlarmStatus == Constants.ALARM_CLEARED) {
								if (TPNDConstants.FinalVersion) {
									if (!pSource.equals(TPNDConstants.TPND_RECOVERY)) {
										tpndManager.updateTPNDRelatedEquipStatusUp(mdbAlarmDto);
										tpndManager.processMdbRelatedTelcoStatusUp(lTechAlarmDto);
									}
								} else {
									tpndManager.updateTPNDRelatedEquipStatusUp(mdbAlarmDto);
								}

								try {
									mdbAlarmDto.setStatus(Constants.ALARM_CLEARED);
									lEquipStatusDto = equipStatusManager
											.generateTPNDEquipStatusForClearStatus(mdbAlarmDto);

									telcoAlarmDto.setStatus(Constants.ALARM_CLEARED);
									lEquipStatusDto2 = equipStatusManager
											.generateTPNDEquipStatusForClearStatus(telcoAlarmDto);
								} catch (Exception e) {
									logger.error("Error processing EquipStatus for Pair Technical alarm clear .....",
											e);
								}
							} else if (lTechAlarmStatus == Constants.ALARM_RAISED) {
								try {
									mdbAlarmDto.setStatus(Constants.ALARM_RAISED);
									lEquipStatusDto = equipStatusManager
											.generateTPNDEquipStatusForAlarmRaised(mdbAlarmDto);

									telcoAlarmDto.setStatus(Constants.ALARM_RAISED);
									lEquipStatusDto2 = equipStatusManager
											.generateTPNDEquipStatusForAlarmRaised(telcoAlarmDto);
								} catch (Exception e) {
									logger.error("Error processing EquipStatus for Pair Technical alarm raise .....",
											e);
								}
							}
							if (lEquipStatusDto != null) {
								lChangedEquipStatusList.add(lEquipStatusDto);
							}
							if (lEquipStatusDto2 != null) {
								lChangedEquipStatusList.add(lEquipStatusDto2);
							}
						}
					}
					/*************************************/
				}

				/***** Changed by Grace 18/12/19 *****/
				// remove duplicates from lChangedEquipStatusList
				List<EquipStatusDto> newEquipStatusList = new ArrayList<EquipStatusDto>();
				for (EquipStatusDto compareEquipStatus : lChangedEquipStatusList) {
					if (!newEquipStatusList.contains(compareEquipStatus)) {
						newEquipStatusList.add(compareEquipStatus);
					}
				}
				lChangedEquipStatusList.clear();
				lChangedEquipStatusList.addAll(newEquipStatusList);
				/*************************************/

				EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList(lChangedEquipStatusList);
				if (lEquipStatusDtoList.getDtoList().size() > 0) {
					logger.info("Object sent to the queue " + lEquipStatusDtoList);
					if (!pSource.equals(Constants.EMASCCS_SENDER)) {
						cmhMessageSender.sendEmasCcsJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
						// Added 04/03/22 GH - To implement new queues to ITPT-INTF
						cmhMessageSender.sendEmasItptJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
					}
					cmhMessageSender.sendAWJmsMessage("EquipStatus", MessageConstants.EQUIP_STATUS_ID);
				}
			}

			if (logger.isInfoEnabled()) {
				logger.info("Calling processEquipStatusForTechnicalAlarm -> Exit");
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Calling processEquipStatusForTechnicalAlarm -> Start");
			}
			EquipStatusDto lEquipStatusDto = null;
			int lTechAlarmStatus;
			List<EquipStatusDto> lChangedEquipStatusList = new ArrayList<EquipStatusDto>();

			if (pTechAlarmDtoList != null && pTechAlarmDtoList.getDtoList() != null
					&& pTechAlarmDtoList.getDtoList().size() > 0) {
				for (TechnicalAlarmDto lTechAlarmDto : pTechAlarmDtoList.getDtoList()) {
					lTechAlarmStatus = lTechAlarmDto.getStatus();
					if (lTechAlarmStatus == Constants.ALARM_CLEARED) {
						try {
							lEquipStatusDto = equipStatusManager.generateEquipStatusForClearStatus(lTechAlarmDto);
						} catch (Exception e) {
							logger.error("Error processing EquipStatus for Technical alarm clear .....", e);
						}
					} else if (lTechAlarmStatus == Constants.ALARM_RAISED) {
						try {
							lEquipStatusDto = equipStatusManager.generateEquipStatusForAlarmRaised(lTechAlarmDto);
						} catch (Exception e) {
							logger.error("Error processing EquipStatus for Technical alarm raise .....", e);
						}
					}
					if (lEquipStatusDto != null) {
						lChangedEquipStatusList.add(lEquipStatusDto);
					}
				}
				EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList(lChangedEquipStatusList);
				if (lEquipStatusDtoList.getDtoList().size() > 0) {
					logger.info("Object sent to the queue " + lEquipStatusDtoList);
					if (!pSource.equals(Constants.EMASCCS_SENDER)) {
						cmhMessageSender.sendEmasCcsJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
						// Added 04/03/22 GH - To implement new queues to ITPT-INTF
						cmhMessageSender.sendEmasItptJmsMessage(lEquipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
					}
					cmhMessageSender.sendAWJmsMessage("EquipStatus", MessageConstants.EQUIP_STATUS_ID);
				}
			}
			if (logger.isInfoEnabled()) {
				logger.info("Calling processEquipStatusForTechnicalAlarm -> Exit");
			}
		}
	}

	public int handleTimetableObj(VmsTimetableConfigDto pVmsTimetableConfigDto) {
		if (logger.isInfoEnabled()) {
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_AW_CMHA + "," + Constants.LOG_RECV + ","
					+ pVmsTimetableConfigDto.getExecId() + "," + pVmsTimetableConfigDto.getCmdId() + ","
					+ pVmsTimetableConfigDto.getEquipId() + "," + Constants.LOG_TTB);
			logger.info("Inside handler :: handleTimetableObj -> Start.....");
		}
		try {
			String lExecId = pVmsTimetableConfigDto.getExecId();
			String lCmdId = pVmsTimetableConfigDto.getCmdId();
			String lSender = pVmsTimetableConfigDto.getSender();

			CMHKeyBean cmhKeyBean = new CMHKeyBean(lExecId, lCmdId);
			cmhHtable.put(cmhKeyBean, lSender);

			logger.info("Object sent to the queue " + pVmsTimetableConfigDto);
			cmhMessageSender.sendTicssJmsMessage(pVmsTimetableConfigDto, MessageConstants.TIME_TABLE_ID);
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_TCIA + "," + Constants.LOG_SEND + ","
					+ pVmsTimetableConfigDto.getExecId() + "," + pVmsTimetableConfigDto.getCmdId() + ","
					+ pVmsTimetableConfigDto.getEquipId() + "," + Constants.LOG_TTB);

			String lActionDetail = "Time table Info send for the Equipment " + pVmsTimetableConfigDto.getEquipId()
					+ " with ExecId :: " + lExecId + " CmdId :: " + lCmdId;
			cmhManager.createAuditTrail(lSender, "VMS Timetable", lActionDetail);

		} catch (Exception e) {
			logger.error("Error in Sending Timetable Obj to MFELS Queue", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit handler :: handleTimetableObj.....");
		}
		return 0;
	}

	public int handleDimmingObj(DimmingDto pDimmingDto) {
		String lInterface = Constants.LOG_AW_CMHA;
		if (pDimmingDto.getSender().equals("EMASCCS")) {
			lInterface = Constants.LOG_EIF_CMHA;
		}
		if (logger.isInfoEnabled()) {
			rtLogger.info(
					Constants.LOG_CODE + "," + lInterface + "," + Constants.LOG_RECV + "," + pDimmingDto.getExecId()
							+ "," + pDimmingDto.getCmdId() + "," + pDimmingDto.getEquipId() + "," + Constants.LOG_DIM);
			logger.info("Inside Handler :: handleDimmingObj -> Start.....");
		}
		try {
			String lExecId = pDimmingDto.getExecId();
			String lCmdId = pDimmingDto.getCmdId();
			String lSender = pDimmingDto.getSender();
			String lSystemId = pDimmingDto.getSystemId();

			CMHKeyBean cmhKeyBean = new CMHKeyBean(lExecId, lCmdId);
			cmhHtable.put(cmhKeyBean, lSender);

			logger.info("ExecId :: " + lExecId);
			logger.info("CmdId :: " + lCmdId);
			logger.info("lSender :: " + lSender);
			logger.info("SystemId :: " + lSystemId);
			logger.info("Object sent to the queue " + pDimmingDto);

			cmhMessageSender.sendTicssJmsMessage(pDimmingDto, MessageConstants.DIMMING_ID);
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_TCIA + "," + Constants.LOG_SEND + ","
					+ pDimmingDto.getExecId() + "," + pDimmingDto.getCmdId() + "," + pDimmingDto.getEquipId() + ","
					+ Constants.LOG_DIM);

			String lActionDetail = "Dimmng Control send to the Equipment " + pDimmingDto.getEquipId() + "DimMode :: "
					+ pDimmingDto.getDimMode() + "DimLevel :: " + pDimmingDto.getDimLevel() + " with ExecId :: "
					+ pDimmingDto.getExecId() + " CmdId :: " + pDimmingDto.getCmdId();
			cmhManager.createAuditTrail(lSender, "VMS Dimming", lActionDetail);
		} catch (Exception e) {
			logger.error("Error in Sending Dimming Obj to Queue", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager :: handleDimmingObj.....");
		}
		return 0;
	}

	public int handleVmsObj(VmsMsgDto pVmsMsgDto) {
		String lInterface = Constants.LOG_AW_CMHA;
		if (pVmsMsgDto.getSender().equals("EMASCCS")) {
			lInterface = Constants.LOG_EIF_CMHA;
		}
		if (logger.isInfoEnabled()) {
			rtLogger.info(
					Constants.LOG_CODE + "," + lInterface + "," + Constants.LOG_RECV + "," + pVmsMsgDto.getExecId()
							+ "," + pVmsMsgDto.getCmdId() + "," + pVmsMsgDto.getEquipId() + "," + Constants.LOG_MSG);
			logger.info("Inside Handler :: handleVmsObj -> Start.....");
		}
		try {
			String lExecId = pVmsMsgDto.getExecId();
			String lCmdId = pVmsMsgDto.getCmdId();
			String lSender = pVmsMsgDto.getSender();

			CMHKeyBean cmhKeyBean = new CMHKeyBean(lExecId, lCmdId);
			cmhHtable.put(cmhKeyBean, lSender);

			logger.info("lExecId :: " + lExecId);
			logger.info("lCmdId :: " + lCmdId);
			logger.info("lSender :: " + lSender);

			logger.info("Object Sent to the Queue " + pVmsMsgDto);
			cmhMessageSender.sendTicssJmsMessage(pVmsMsgDto, MessageConstants.VMS_ID);
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_TCIA + "," + Constants.LOG_SEND + ","
					+ pVmsMsgDto.getExecId() + "," + pVmsMsgDto.getCmdId() + "," + pVmsMsgDto.getEquipId() + ","
					+ Constants.LOG_MSG);

			String lActionDetail = "VMS Msg send to the Equipment " + pVmsMsgDto.getEquipId() + " with ExecId :: "
					+ lExecId + " CmdId :: " + lCmdId;
			cmhManager.createAuditTrail(lSender, "VMS Msg", lActionDetail);
		} catch (Exception e) {
			logger.error("Error in Sending VmsDefaultMsg Obj to MFELS Queue", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleVmsObj.....");
		}
		return 0;
	}

	public int handleResetObj(ResetDto pResetDto) {
		if (logger.isInfoEnabled()) {
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_AW_CMHA + "," + Constants.LOG_RECV + ","
					+ pResetDto.getExecId() + "," + pResetDto.getCmdId() + "," + pResetDto.getEquipId() + ","
					+ Constants.LOG_RST);
			logger.info("Inside Handler :: handleResetObj -> Start.....");
		}
		try {
			CMHKeyBean cmhKeyBean = new CMHKeyBean(pResetDto.getExecId(), pResetDto.getCmdId());
			cmhHtable.put(cmhKeyBean, pResetDto.getSender());

			logger.info("Object sent to the queue " + pResetDto);
			cmhMessageSender.sendTicssJmsMessage(pResetDto, MessageConstants.RESET_ID);
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_TCIA + "," + Constants.LOG_SEND + ","
					+ pResetDto.getExecId() + "," + pResetDto.getCmdId() + "," + pResetDto.getEquipId() + ","
					+ Constants.LOG_RST);

			String lActionDetail = "Reset command send to the Equipment " + pResetDto.getEquipId() + " with ExecId :: "
					+ pResetDto.getExecId() + " CmdId :: " + pResetDto.getCmdId();
			cmhManager.createAuditTrail(pResetDto.getSender(), "VMS Reset", lActionDetail);
		} catch (Exception e) {
			logger.error("Error in Sending Reset Obj to Queue", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleResetObj.....");
		}
		return 0;
	}

	public int handleFanOpeModeObj(FanOpeModeDto pFanOpeModeDto) {
		if (logger.isInfoEnabled()) {
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_AW_CMHA + "," + Constants.LOG_RECV + ","
					+ pFanOpeModeDto.getExecId() + "," + pFanOpeModeDto.getCmdId() + "," + pFanOpeModeDto.getEquipId()
					+ "," + Constants.LOG_OTH);
			logger.info("Inside Handler :: handleFanOpeMode -> Start.....");
		}
		try {
			CMHKeyBean cmhKeyBean = new CMHKeyBean(pFanOpeModeDto.getExecId(), pFanOpeModeDto.getCmdId());
			cmhHtable.put(cmhKeyBean, pFanOpeModeDto.getSender());

			logger.info("Object sent to the queue " + pFanOpeModeDto);
			cmhMessageSender.sendTicssJmsMessage(pFanOpeModeDto, MessageConstants.FAN_OPE_MODE_ID);
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_TCIA + "," + Constants.LOG_SEND + ","
					+ pFanOpeModeDto.getExecId() + "," + pFanOpeModeDto.getCmdId() + "," + pFanOpeModeDto.getEquipId()
					+ "," + Constants.LOG_OTH);

			String lActionDetail = "FanOpeMode command send to the Equipment " + pFanOpeModeDto.getEquipId()
					+ " with ExecId :: " + pFanOpeModeDto.getExecId() + " CmdId :: " + pFanOpeModeDto.getCmdId();
			cmhManager.createAuditTrail(pFanOpeModeDto.getSender(), "VMS FanOpeMode", lActionDetail);

		} catch (Exception e) {
			logger.error("Error in Sending FanOpeMode to Queue", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleFanOpeMode.....");
		}
		return 0;
	}

	public int handleTechAlarmAckList(TechAlarmAckDtoList pTechAlarmAckDtoList, String pSource) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handleTechAlarmAckList -> Start");
		}
		List<TechAlarmAckDto> lCurrentAWList = new ArrayList<TechAlarmAckDto>();
		try {
			TechAlarmAckDto lTechAlarmAckDto = null;
			for (int k = 0; k < pTechAlarmAckDtoList.getDtoList().size(); k++) {
				lTechAlarmAckDto = pTechAlarmAckDtoList.getDtoList().get(k);

				logger.debug("Source of the object :: " + pSource);
				boolean lIsCurrentTechAlarm = techAlarmManager.processTechAlarmAck(lTechAlarmAckDto, pSource);
				if (lIsCurrentTechAlarm) {
					lCurrentAWList.add(lTechAlarmAckDto);
				}
			}
			TechAlarmAckDtoList lCurrentTechAlarmAWList = null;
			if (lCurrentAWList != null && lCurrentAWList.size() > 0) {
				lCurrentTechAlarmAWList = new TechAlarmAckDtoList(lCurrentAWList);
			}
			if (lCurrentTechAlarmAWList != null) {
				logger.info("Object sent to AW queue " + lCurrentTechAlarmAWList);
				cmhMessageSender.sendAWJmsMessage("TechnicalAlarmAck", MessageConstants.TECH_ALARM_ACK_ID);
			}
		} catch (Exception e) {
			logger.error("Error processing TechAlarmAckList...", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleTechAlarmAckList.....");
		}
		return 0;
	}

	public int handlePixelFailureBMPFile(String pEquipId, String pSender) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handlePixelFailureBMPFile -> Start.....");
		}
		try {
			//pixelHtable.put(pEquipId, pSender);			
			logger.info("Object sent to the queue " + pEquipId);
			cmhMessageSender.sendTicssJmsMessage(pEquipId, MessageConstants.PIXEL_FAIL_BMP_FILE_REQ_ID);

			String lActionDetail = "Pixel Failure BMPFile Requested for the Equipment " + pEquipId;
			cmhManager.createAuditTrail(pSender, "Pixel Failure BMPFile", lActionDetail);

		} catch (Exception e) {
			logger.error("Error in Sending PixelFailureBMPFile to Queue", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handlePixelFailureBMPFile.....");
		}
		return 0;
	}

	@SuppressWarnings("rawtypes")
	public void handleCmdRespObj(CmdRespDto pCmdRespDto) {
		if (logger.isInfoEnabled()) {
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_TCIA_CMHA + "," + Constants.LOG_RECV + ","
					+ pCmdRespDto.getExecId() + "," + pCmdRespDto.getCmdId() + "," + pCmdRespDto.getEquipId() + ","
					+ Constants.LOG_RES);
			logger.info("Inside Handler :: handleCmdRespObj -> Start.....");
			logger.info("Received CmdRespDtoObj");
			logger.info(pCmdRespDto.toString());
		}
		String lExecId = pCmdRespDto.getExecId();
		String lCmdId = pCmdRespDto.getCmdId();

		CMHKeyBean lCmhKeyBean = new CMHKeyBean(lExecId, lCmdId);
		String lSender = cmhHtable.get(lCmhKeyBean);
		cmhHtable.remove(lCmhKeyBean);
		if (lSender == null) {
			lSender = "";
		}
		logger.info("lSender :: " + lSender);
		pCmdRespDto.setSender(lSender);

		if (lSender.equals(Constants.EMASCCS_SENDER)) {
			cmhMessageSender.sendEmasCcsJmsMessage(pCmdRespDto, MessageConstants.CMD_RESP_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(pCmdRespDto, MessageConstants.CMD_RESP_ID);
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_EIF + "," + Constants.LOG_SEND + ","
					+ pCmdRespDto.getExecId() + "," + pCmdRespDto.getCmdId() + "," + pCmdRespDto.getEquipId() + ","
					+ Constants.LOG_RES);
			logger.info("Object Sent to EMASCCS Queue ....." + pCmdRespDto);
		} else {
			Map lCmdRespDtoMap = messageConverterUtil.convertCmdRespDTOToMap(pCmdRespDto);
			cmhMessageSender.sendAWJmsMessage(lCmdRespDtoMap, MessageConstants.CMD_RESP_ID);
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_AW + "," + Constants.LOG_SEND + ","
					+ pCmdRespDto.getExecId() + "," + pCmdRespDto.getCmdId() + "," + pCmdRespDto.getEquipId() + ","
					+ Constants.LOG_RES);
			logger.info("Object Sent to AW Queue ....." + pCmdRespDto);
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleCmdRespObj.....");
		}
	}

	@SuppressWarnings("rawtypes")
	public void handleLusCmdRespObj(CmdRespDto pCmdRespDto) {
		if (logger.isInfoEnabled()) {
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_TCIA_CMHA + "," + Constants.LOG_RECV + ","
					+ pCmdRespDto.getExecId() + "," + pCmdRespDto.getCmdId() + "," + pCmdRespDto.getEquipId() + ","
					+ Constants.LOG_RES);
			logger.info("Inside Handler :: handleCmdRespObj -> Start.....");
			logger.info("Received CmdRespDtoObj");
			logger.info(pCmdRespDto.toString());
		}
		String lExecId = pCmdRespDto.getExecId();
		String lCmdId = pCmdRespDto.getCmdId();

		CMHKeyBean lCmhKeyBean = new CMHKeyBean(lExecId, lCmdId);
		String lSender = cmhHtable.get(lCmhKeyBean);
		cmhHtable.remove(lCmhKeyBean);
		if (lSender == null) {
			lSender = "";
		}
		logger.info("lSender :: " + lSender);
		pCmdRespDto.setSender(lSender);

		if (lSender.equals(Constants.EMASCCS_SENDER)) {
			cmhMessageSender.sendEmasCcsJmsMessage(pCmdRespDto, MessageConstants.CMD_RESP_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(pCmdRespDto, MessageConstants.CMD_RESP_ID);
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_EIF + "," + Constants.LOG_SEND + ","
					+ pCmdRespDto.getExecId() + "," + pCmdRespDto.getCmdId() + "," + pCmdRespDto.getEquipId() + ","
					+ Constants.LOG_RES);
			logger.info("Object Sent to EMASCCS Queue ....." + pCmdRespDto);
		} else {
			// rewrite response to aw
			StatusCodeEnum statusCodeEnum = StatusCodeEnum.fromCode(pCmdRespDto.getStatus());
			Map<String, Object>  lCmdRespDtoMap = new HashMap<String, Object>();
			lCmdRespDtoMap.put("EquipId", pCmdRespDto.getEquipId());
			lCmdRespDtoMap.put("ExecId", pCmdRespDto.getExecId());
			lCmdRespDtoMap.put("CmdId", pCmdRespDto.getCmdId());
			lCmdRespDtoMap.put("Status", statusCodeEnum.getCode());
			lCmdRespDtoMap.put("Sender", pCmdRespDto.getSender());
			lCmdRespDtoMap.put("Message", statusCodeEnum.getDescription());
			cmhMessageSender.sendAWJmsMessage(lCmdRespDtoMap, MessageConstants.CMD_RESP_ID);
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_AW + "," + Constants.LOG_SEND + ","
					+ pCmdRespDto.getExecId() + "," + pCmdRespDto.getCmdId() + "," + pCmdRespDto.getEquipId() + ","
					+ Constants.LOG_RES);
			logger.info("Object Sent to AW Queue ....." + pCmdRespDto);
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleCmdRespObj.....");
		}
	}

	public void handleVmsObjResp(VmsMsgDto pVmsMsgDto) {
		if (logger.isInfoEnabled()) {
			// logger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_TCIA + "," +
			// Constants.LOG_RECV + "," + pVmsMsgDto.getExecId() + "," +
			// pVmsMsgDto.getCmdId() + "," + pVmsMsgDto.getEquipId() + "," +
			// Constants.LOG_MSG);
			logger.info("Inside Handler :: handleVmsObjResp -> Start.....");
		}
		String lId = vmsMsgManager.processVmsMsgResponse(pVmsMsgDto);
		if (lId != null) {
			logger.info("Object sent to the queue " + pVmsMsgDto);
			cmhMessageSender.sendEmasCcsJmsMessage(pVmsMsgDto, MessageConstants.VMS_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(pVmsMsgDto, MessageConstants.VMS_ID);
			// logger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_EIF + "," +
			// Constants.LOG_SEND + "," + pVmsMsgDto.getExecId() + "," +
			// pVmsMsgDto.getCmdId() + "," + pVmsMsgDto.getEquipId() + "," +
			// Constants.LOG_MSG);
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleVmsObjResp .....");
		}
	}

	public void handleVmsList(VmsMsgDtoList pVmsMsgDtoList) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handleVmsList -> Start.....");
		}
		vmsMsgManager.processVmsMsgList(pVmsMsgDtoList);
		logger.info("Object sent to the queue " + pVmsMsgDtoList);
		cmhMessageSender.sendEmasCcsJmsMessage(pVmsMsgDtoList, MessageConstants.VMS_LIST_ID);
		// Added 04/03/22 GH - To implement new queues to ITPT-INTF
		cmhMessageSender.sendEmasItptJmsMessage(pVmsMsgDtoList, MessageConstants.VMS_LIST_ID);

		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleVmsList .....");
		}
	}

	@SuppressWarnings("rawtypes")
	public void handlePixelFailureBMPFileResp(PixelFailureBMPFileDto pPixelFailureBMPFileDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handlePixelFailureBMPFileResp -> EquipId ::"
					+ pPixelFailureBMPFileDto.getEquipId());
		}
		//String lSender = pixelHtable.get(pPixelFailureBMPFileDto.getEquipId());
		//pPixelFailureBMPFileDto.setSender(lSender);

		Map lPixelFailureDtoMap = messageConverterUtil.convertPixelFailureDTOToMap(pPixelFailureBMPFileDto);
		cmhMessageSender.sendAWJmsMessage(lPixelFailureDtoMap, MessageConstants.PIXEL_FAIL_BMP_FILE_RESP_ID);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handlePixelFailureBMPFileResp.....");
		}
	}

	public void handleTrafficAlertObj(TrafficAlertDto pTrafficAlertDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handleTrafficAlertObj -> Start ....." + pTrafficAlertDto.getAlertId());
		}
		if (!Constants.CONGESTION_EVENT_TYPE.equals(pTrafficAlertDto.getAlertCode())) {
			trafficAlertManager.processTrafficAlert(pTrafficAlertDto);

			logger.info("Object Sent to the Queue " + pTrafficAlertDto);
			cmhMessageSender.sendAWJmsMessage("TrafficAlert", MessageConstants.TRAFFIC_ALERT_ID);
			
			String notifyString = "AlertNotify,"+pTrafficAlertDto.getAlertId()+","+pTrafficAlertDto.getAlertCode();
			cmhMessageSender.sendAWJmsMessage(notifyString, MessageConstants.TRAFFIC_ALERT_ID);
			
			cmhMessageSender.sendEmasCcsJmsMessage(pTrafficAlertDto, MessageConstants.TRAFFIC_ALERT_ID);
			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
			cmhMessageSender.sendEmasItptJmsMessage(pTrafficAlertDto, MessageConstants.TRAFFIC_ALERT_ID);
		} else if (Constants.CONGESTION_EVENT_TYPE.equals(pTrafficAlertDto.getAlertCode())) {
			logger.info("Incident Type 4 received..." + pTrafficAlertDto.getEquipId());

			boolean lIsTrafficDataEnabled = cmhManager.canProcessTrafficAlert(pTrafficAlertDto.getEquipId());
			if (lIsTrafficDataEnabled) {
				congestionAlertHandler.processLOSBegin(pTrafficAlertDto);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleTrafficAlertObj .....");
		}
	}

	public void handleTrafficAlertClearList(TrafficAlertClearDtoList pTrafficAlertClearDtoList) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handleTrafficAlertClearList -> Start .....");
		}
		for (TrafficAlertClearDto lTrafficAlertClearDto : pTrafficAlertClearDtoList.getDtoList()) {
			logger.info("Alert Cleared recieved from TFELS for AlertId :: " + lTrafficAlertClearDto.getAlertId());
			logger.info("Alert Cleared recieved from TFELS for AlertType :: " + lTrafficAlertClearDto.getAlertCode());

			if (!Constants.CONGESTION_EVENT_TYPE.equals(lTrafficAlertClearDto.getAlertCode())) {
				try {
					TrafficAlertDto lTrafficAlertDto = trafficAlertManager.processSmokeClear(lTrafficAlertClearDto);

					logger.info("Object Sent to the Queue " + lTrafficAlertDto);
					cmhMessageSender.sendAWJmsMessage("TrafficAlert", MessageConstants.TRAFFIC_ALERT_ID);
					cmhMessageSender.sendEmasCcsJmsMessage(lTrafficAlertDto, MessageConstants.TRAFFIC_ALERT_ID);
					// Added 04/03/22 GH - To implement new queues to ITPT-INTF
					cmhMessageSender.sendEmasItptJmsMessage(lTrafficAlertDto, MessageConstants.TRAFFIC_ALERT_ID);

				} catch (Exception e) {
					logger.error("Error in Processing Smoke Aler Clear...", e);
				}
			} else {
				congestionAlertHandler.processLOSEnd(lTrafficAlertClearDto);
			}
		}
	}

	/*
	 * public void handleTrafficAlertClearList(TrafficAlertClearDtoList
	 * pTrafficAlertClearDtoList) { if (logger.isInfoEnabled()) {
	 * logger.info("Inside Handler :: handleTrafficAlertClearList -> Start .....");
	 * } for (TrafficAlertClearDto lTrafficAlertClearDto :
	 * pTrafficAlertClearDtoList.getDtoList()) {
	 * logger.info("Alert Cleared recieved from TFELS for AlertId :: " +
	 * lTrafficAlertClearDto.getAlertId());
	 * congestionAlertHandler.processLOSEnd(lTrafficAlertClearDto); } }
	 */

	public int handleTrafficAlertAckList(TrafficAlertAckDtoList pTrafficAlertAckDtoList) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handleTrafficAlertAcklist -> Start");
		}
		try {
			trafficAlertManager.processTrafficAlertAck(pTrafficAlertAckDtoList);

			logger.info("Object sent to the queue " + pTrafficAlertAckDtoList);
			cmhMessageSender.sendAWJmsMessage("TrafficAlertAck", MessageConstants.TRAFFIC_ALERT_ACK_ID);
		} catch (Exception e) {
			logger.error("Error processing TrafficAlertAckList...", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleTechAlarmAckList.....");
		}
		return 0;
	}

	public void handlePreviewImageObj(PreviewImageDto pPreviewImageDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handlePreviewImageObj -> Start .....");
		}
		trafficAlertManager.processPreviewImage(pPreviewImageDto);
		logger.info("Object Sent to the Queue " + pPreviewImageDto);
		cmhMessageSender.sendEmasCcsJmsMessage(pPreviewImageDto, MessageConstants.PREVIEW_IMAGE_ID);
		// Added 04/03/22 GH - To implement new queues to ITPT-INTF
		cmhMessageSender.sendEmasItptJmsMessage(pPreviewImageDto, MessageConstants.PREVIEW_IMAGE_ID);

		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handlePreviewImageObj .....");
		}
	}

	// Added by Grace on 02/07/2020 to hand new image sequence
	public void handleImageSequenceObj(ImageSequenceDto pImageSequenceDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handleImageSequenceObj -> Start .....");
		}
		trafficAlertManager.processImageSequence(pImageSequenceDto);
		//logger.info("Object Sent to the Queue " + pImageSequenceDto);
		// ToDo: Uncomment once CCS/ITPT Interface can handle
		//cmhMessageSender.sendEmasCcsJmsMessage(pImageSequenceDto, MessageConstants.IMAGE_SEQUENCE_ID);
		//cmhMessageSender.sendEmasItptJmsMessage(pImageSequenceDto, MessageConstants.PREVIEW_IMAGE_ID);

		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleImageSequenceObj .....");
		}
	}

	public void handleTravelTimePageObj(VmsMsgDto pVmsMsgDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handleTravelTimePageObj -> Start .....");
		}
		logger.info("Object Sent to the Queue " + pVmsMsgDto);
		cmhMessageSender.sendTicssJmsMessage(pVmsMsgDto, MessageConstants.TRAVEL_TIME_ID);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleTravelTimePageObj .....");
		}
	}

	public void handleTravelTimePageDel(String pEquipId) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handleTravelTimePageDel -> Start .....");
		}
		logger.info("Object Sent to the Queue " + pEquipId);
		cmhMessageSender.sendTicssJmsMessage(pEquipId, MessageConstants.DEL_TRAVEL_TIME_PAGE_ID);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleTravelTimePageDel .....");
		}
	}

	public int handleFlashingTime(FlashingTimeDto pFlashingTimeDto) {
		if (logger.isInfoEnabled()) {
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_AW + "," + Constants.LOG_RECV + ","
					+ pFlashingTimeDto.getExecId() + "," + pFlashingTimeDto.getCmdId() + ","
					+ pFlashingTimeDto.getEquipId() + "," + Constants.LOG_OTH);
			logger.info("Inside Handler :: handleFlashingTime -> Start .....");
		}
		try {
			String lExecId = pFlashingTimeDto.getExecId();
			String lCmdId = pFlashingTimeDto.getCmdId();
			String lSender = pFlashingTimeDto.getSender();

			CMHKeyBean cmhKeyBean = new CMHKeyBean(lExecId, lCmdId);
			cmhHtable.put(cmhKeyBean, lSender);

			logger.info("lExecId :: " + lExecId);
			logger.info("lCmdId :: " + lCmdId);
			logger.info("lSender :: " + lSender);

			logger.info("Object Sent to the Queue " + pFlashingTimeDto);
			cmhMessageSender.sendTicssJmsMessage(pFlashingTimeDto, MessageConstants.VMS_FLASHING_TIME_ID);
			rtLogger.info(Constants.LOG_CODE + "," + Constants.LOG_CMHA_TCIA + "," + Constants.LOG_SEND + ","
					+ pFlashingTimeDto.getExecId() + "," + pFlashingTimeDto.getCmdId() + ","
					+ pFlashingTimeDto.getEquipId() + "," + Constants.LOG_OTH);

			String lActionDetail = "Flashing Time send to the Equipment " + pFlashingTimeDto.getEquipId()
					+ " with ExecId :: " + lExecId + " CmdId :: " + lCmdId;
			cmhManager.createAuditTrail(lSender, "Flashing Time", lActionDetail);

		} catch (Exception e) {
			logger.error("Error in Sending FlashingTime Obj to MFELS Queue", e);
			return 1;
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleFlashingTime .....");
		}
		return 0;
	}

	public void handleAWCfelsUploadPictogram(String pEquipId, String pPictogramId, String pSender) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handleAWCfelsUploadPictogram -> Start .....");
		}
		StringBuffer sb = new StringBuffer();
		sb.append(pEquipId);
		sb.append(",");
		sb.append(pPictogramId);

		logger.info("Object Sent to the Queue " + sb);
		cmhMessageSender.sendTicssJmsMessage(sb.toString(), MessageConstants.UPLOAD_PICTOGRAM_REQUEST);

		String lActionDetail = "Upload Pictogram to the Equipment " + pEquipId;
		cmhManager.createAuditTrail(pSender, "Upload Pictogram", lActionDetail);

		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleAWCfelsUploadPictogram .....");
		}
	}

	@SuppressWarnings("rawtypes")
	public void handleUploadPictogramResp(PictogramDto pPictogramDto) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Handler :: handleUploadPictogramResp -> EquipId ::" + pPictogramDto.getEquipId()
					+ " PictogramId :: " + pPictogramDto.getPictogramId());
		}
		Map lPictogramDtoMap = messageConverterUtil.convertPictogramDTOToMap(pPictogramDto);
		cmhMessageSender.sendAWJmsMessage(lPictogramDtoMap, MessageConstants.UPLOAD_PICTOGRAM_RESPONSE);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Handler :: handleUploadPictogramResp.....");
		}
	}

	public Hashtable<CMHKeyBean, String> getCMHHashTable() {
		return cmhHtable;
	}
}
