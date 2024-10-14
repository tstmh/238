/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.common;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;
/***** Changed by Grace 18/12/19 *****/
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
/***** Changed by Grace 18/12/19 *****/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.stee.emas.cmh.caid.CaidKeyBean;
import com.stee.emas.cmh.caid.CaidLaneAlertObj;
import com.stee.emas.cmh.caid.CongestionBuffer;
import com.stee.emas.cmh.service.CMHManager;
import com.stee.emas.cmh.service.EquipStatusManager;
import com.stee.emas.cmh.service.TechnicalAlarmManager;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emas.common.dto.TrafficAlertDto;
import com.stee.emas.common.entity.EquipConfig;
import com.stee.emas.common.entity.EquipStatus;
import com.stee.emas.common.entity.SystemParameter;
import com.stee.emas.common.entity.TechnicalAlarm;

/***** Changed by Grace 18/12/19 *****/
import com.stee.emas.cmh.tpnd.TPNDManager;
import com.stee.emas.cmh.tpnd.common.TPNDConstants;
import com.stee.emas.cmh.tpnd.common.TPNDRaisedAlarm;
/***** Changed by Grace 18/12/19 *****/


/**
 * Title: EMAS Enhancement Project
 * Description : Initialization class for CMH
 * This class is used as initialization class for CMH
 * Copyright: Copyright (c) 2012
 * Company:STEE-InfoComm
 * @author Scindia
 * @since Mar 5, 2013
 * @version 1.0
 *
 */

@Component("startupListener")
public class StartupListener {

	private static Logger logger = LoggerFactory.getLogger(StartupListener.class);

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	CMHManager cmhManager;
	@Autowired
	TechnicalAlarmManager technicalAlarmManager;
	@Autowired
	EquipStatusManager equipStatusManager;
	@Autowired
	CongestionBuffer congestionBuffer;
	/***** Changed by Grace 18/12/19 *****/
	@Autowired
	TPNDManager tpndManager;
	/*************************************/

	public void init() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				logger.info("*************************");
				logger.info("Copyrights-STEE-InfoComm");
				logger.info(".....CMH Started.....");
				logger.info("*************************");
				clearCaidBuffer();
				processOPEFlag();
				/***** Changed by Grace 18/12/19 *****/
				if (TPNDConstants.TPNDFlag) {
					if (TPNDConstants.WithTPN) {
						processTPNPairOPEState();
					}
				}
				/*************************************/
			}
		});
	}

	public void clearCaidBuffer() {
		logger.info("Clearing Congestion Related Buffer .....");
		congestionBuffer.setCongestionHoldingMap(new TreeMap<String, CaidLaneAlertObj>());
		congestionBuffer.setCongestionMap(new TreeMap<String, TrafficAlertDto>());
		congestionBuffer.setCongestionTempMap(new TreeMap<CaidKeyBean, TrafficAlertDto>());
	}

	public void processOPEFlag() {
		if (TPNDConstants.TPNDFlag) {
			logger.info("Calling processOPEFlag .....");
			try {
				WebApplicationContext springContext = (WebApplicationContext) ApplicationContextProvider
						.getApplicationContext();
				SystemParameter lSystemParameter = cmhManager.findSystemParameterByName(Constants.OPE_ENABLE_FLAG);
				String lOpeFlag = lSystemParameter.getValue();
				springContext.getServletContext().setAttribute("OPE_FLAG", lOpeFlag);

				logger.info("processOPEFlag :: lOpeFlag ........ " + lOpeFlag);
				if (lOpeFlag.equals(Constants.ENABLE_OPE_FLAG)) {
					List<TechnicalAlarm> lTechnicalAlarmList = technicalAlarmManager.getAllTechnicalAlarm();
					for (TechnicalAlarm lTechnicalAlarm : lTechnicalAlarmList) {
						/***** Changed by Grace 18/12/19 *****/
						// logger.info("EquipId :: " + lTechnicalAlarm.getEquipConfig().getEquipId());
						// EquipStatus lEquipStatus =
						// equipStatusManager.findEquipStatusByEquipIdAndStatusCode(lTechnicalAlarm.getEquipConfig().getEquipId(),
						// Constants.OPE_STATE);
						logger.info("processOPEFlag :: EquipId :: " + lTechnicalAlarm.getEquipId());
						EquipStatus lEquipStatus = equipStatusManager.findEquipStatusByEquipIdAndStatusCode(
								lTechnicalAlarm.getEquipId(), Constants.OPE_STATE);
						/*************************************/

						if (lEquipStatus != null) {
							int lStatus = lEquipStatus.getStatus();
							if (lStatus != Constants.EQUIP_STATUS_NORMAL) {
								lEquipStatus.setStatus(Constants.EQUIP_STATUS_NG);
								lEquipStatus.setDateTime(new Date());
								equipStatusManager.updateEquipStatus(lEquipStatus);
								/***** Changed by Grace 18/12/19 *****/
								// logger.info("Equip Status changed to " + Constants.EQUIP_STATUS_NG + " for
								// Equip Id :: " + lTechnicalAlarm.getEquipConfig().getEquipId());
								logger.info("processOPEFlag :: Equip Status changed to " + Constants.EQUIP_STATUS_NG
										+ " for Equip Id :: " + lTechnicalAlarm.getEquipId());
								if ((lTechnicalAlarm.getEquipId().startsWith(TPNDConstants.TPND_MDB_TYPE))
										|| (lTechnicalAlarm.getEquipId().startsWith(TPNDConstants.TPND_TELCO_TYPE))) {
									processTPNDRelatedOPEFlagToNg(lTechnicalAlarm.getEquipId());
									if (!TPNDConstants.FinalVersion) {
										restoreTPNDProcessedList(lTechnicalAlarm.getEquipId());
									}
								}
								/*************************************/
							}
						}
						/***** Changed by Grace 18/12/19 *****/
						else {
							logger.error(
									"processOPEFlag :: Equip Status not found for " + lTechnicalAlarm.getEquipId());
						}
						/*************************************/
					}
				} else if (lOpeFlag.equals(Constants.DISABLE_OPE_FLAG)) {
					/***** Changed by Grace 18/12/19 *****/
					String[] lEquipTypeArray = Constants.OPE_EQUIP_TYPE_ARRAY;
					// int[] lAlarmCodeArray = {1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1};
					int[] lAlarmCodeArray = Constants.OPE_EQUIP_STATUS_ARRAY;
					/*************************************/
					for (int k = 0; k < lEquipTypeArray.length; k++) {
						// List<TechnicalAlarm> lTechnicalAlarmListIn =
						// technicalAlarmManager.getTechnicalAlarmByEquipTypeAndAlarmCode(lEquipTypeArray[k],
						// lAlarmCodeArray[k]);
						// if (lTechnicalAlarmListIn == null || lTechnicalAlarmListIn.size() == 0) {
						List<TechnicalAlarm> lTechnicalAlarmListNotIn = technicalAlarmManager
								.getTechAlarmByEquipTypeAndNotInAlarmCode(lEquipTypeArray[k], lAlarmCodeArray[k]);
						for (TechnicalAlarm lTechnicalAlarm : lTechnicalAlarmListNotIn) {
							/***** Changed by Grace 18/12/19 *****/
							// TechnicalAlarm lAlarmOpe =
							// technicalAlarmManager.getTechnicalAlarmByEquipIdAndAlarmCode(lTechnicalAlarm.getEquipConfig().getEquipId(),
							// lAlarmCodeArray[k]);
							TechnicalAlarm lAlarmOpe = technicalAlarmManager.getTechnicalAlarmByEquipIdAndAlarmCode(
									lTechnicalAlarm.getEquipId(), lAlarmCodeArray[k]);
							/*************************************/
							if (lAlarmOpe == null) {
								// EquipStatus lEquipStatus =
								// equipStatusManager.findEquipStatusByEquipIdAndStatusCode(lTechnicalAlarm.getEquipConfig().getEquipId(),
								// Constants.OPE_STATE);
								EquipStatus lEquipStatus = equipStatusManager.findEquipStatusByEquipIdAndStatusCode(
										lTechnicalAlarm.getEquipId(), Constants.OPE_STATE);
								if (lEquipStatus != null) {
									int lStatus = lEquipStatus.getStatus();
									if (lStatus != Constants.EQUIP_STATUS_NG) {
										lEquipStatus.setStatus(Constants.EQUIP_STATUS_NORMAL);
										lEquipStatus.setDateTime(new Date());
										equipStatusManager.updateEquipStatus(lEquipStatus);
										/***** Changed by Grace 18/12/19 *****/
										// logger.info("Equip Status changed to " + Constants.EQUIP_STATUS_NORMAL + "
										// for Equip Id :: " + lTechnicalAlarm.getEquipConfig().getEquipId());
										logger.info("processOPEFlag :: Equip Status changed to "
												+ Constants.EQUIP_STATUS_NORMAL + " for Equip Id :: "
												+ lTechnicalAlarm.getEquipId());
										/*************************************/
										if ((lTechnicalAlarm.getEquipId().startsWith(TPNDConstants.TPND_MDB_TYPE))
												|| (lTechnicalAlarm.getEquipId()
														.startsWith(TPNDConstants.TPND_TELCO_TYPE))) {
											processTPNDRelatedOPEFlagToNormal(lTechnicalAlarm.getEquipId());
										}
									} else if (!TPNDConstants.FinalVersion) {
										if ((lTechnicalAlarm.getEquipId().startsWith(TPNDConstants.TPND_MDB_TYPE))
												|| (lTechnicalAlarm.getEquipId()
														.startsWith(TPNDConstants.TPND_TELCO_TYPE))) {
											restoreTPNDProcessedList(lTechnicalAlarm.getEquipId());
										}
									}
								}
								/***** Changed by Grace 18/12/19 *****/
								else {
									logger.error("processOPEFlag :: Equip Status not found for "
											+ lTechnicalAlarm.getEquipId());
								}
								/*************************************/
							}
						}
						// }
					}
				}
			} catch (Exception e) {
				logger.error("Error in processing OPE_FLAG ", e);
			}
		} else {
			logger.info("Calling processOPEFlag .....");		
			try {
				WebApplicationContext springContext = (WebApplicationContext)ApplicationContextProvider.getApplicationContext();
				SystemParameter lSystemParameter = cmhManager.findSystemParameterByName(Constants.OPE_ENABLE_FLAG);
				String lOpeFlag = lSystemParameter.getValue();
				springContext.getServletContext().setAttribute("OPE_FLAG", lOpeFlag);
				
				logger.info("lOpeFlag ........ " + lOpeFlag);
				if (lOpeFlag.equals(Constants.ENABLE_OPE_FLAG)) {
					List<TechnicalAlarm> lTechnicalAlarmList = technicalAlarmManager.getAllTechnicalAlarm();
					for (TechnicalAlarm lTechnicalAlarm : lTechnicalAlarmList) {
						logger.info("EquipId :: " + lTechnicalAlarm.getEquipId());
						EquipStatus lEquipStatus = equipStatusManager.findEquipStatusByEquipIdAndStatusCode(lTechnicalAlarm.getEquipId(), Constants.OPE_STATE);
						if (lEquipStatus != null) {
							int lStatus = lEquipStatus.getStatus();
							if (lStatus != Constants.EQUIP_STATUS_NORMAL) {
								lEquipStatus.setStatus(Constants.EQUIP_STATUS_NG);
								lEquipStatus.setDateTime(new Date());
								equipStatusManager.updateEquipStatus(lEquipStatus);
								logger.info("Equip Status changed to " + Constants.EQUIP_STATUS_NG  + " for Equip Id :: " + lTechnicalAlarm.getEquipId());
							}
						}
					}
				} else if (lOpeFlag.equals(Constants.DISABLE_OPE_FLAG)) {
					String[] lEquipTypeArray = Constants.OPE_EQUIP_TYPE_ARRAY;
					int[] lAlarmCodeArray = {1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1};
					for (int k = 0 ; k < lEquipTypeArray.length; k++) {
						//List<TechnicalAlarm> lTechnicalAlarmListIn = technicalAlarmManager.getTechnicalAlarmByEquipTypeAndAlarmCode(lEquipTypeArray[k], lAlarmCodeArray[k]);
						//if (lTechnicalAlarmListIn == null || lTechnicalAlarmListIn.size() == 0) {
							List<TechnicalAlarm> lTechnicalAlarmListNotIn = technicalAlarmManager.getTechAlarmByEquipTypeAndNotInAlarmCode(lEquipTypeArray[k], lAlarmCodeArray[k]);
							for (TechnicalAlarm lTechnicalAlarm : lTechnicalAlarmListNotIn) {
								TechnicalAlarm lAlarmOpe = technicalAlarmManager.getTechnicalAlarmByEquipIdAndAlarmCode(lTechnicalAlarm.getEquipId(), lAlarmCodeArray[k]);
								if (lAlarmOpe == null) {
									EquipStatus lEquipStatus = equipStatusManager.findEquipStatusByEquipIdAndStatusCode(lTechnicalAlarm.getEquipId(), Constants.OPE_STATE);
									if (lEquipStatus != null) {
										int lStatus = lEquipStatus.getStatus();
										if (lStatus != Constants.EQUIP_STATUS_NG) {
											lEquipStatus.setStatus(Constants.EQUIP_STATUS_NORMAL);
											lEquipStatus.setDateTime(new Date());
											equipStatusManager.updateEquipStatus(lEquipStatus);
											logger.info("Equip Status changed to " + Constants.EQUIP_STATUS_NORMAL  + " for Equip Id :: " + lTechnicalAlarm.getEquipId());
										}
									}
								}
							}
						//}
					}
				}
			} catch (Exception e) {
				logger.error("Error in processing OPE_FLAG ", e);
			}
		}
	}

	/***** Changed by Grace 18/12/19 *****/
	public void processTPNDRelatedOPEFlagToNormal(String equipId) {
		List<String> deviceRelatedList = tpndManager.findDeviceList(equipId);
		for (String relatedDeviceId : deviceRelatedList) {
			EquipStatus lEquipStatus = equipStatusManager.findEquipStatusByEquipIdAndStatusCode(relatedDeviceId,
					Constants.OPE_STATE);
			if (lEquipStatus != null) {
				int lStatus = lEquipStatus.getStatus();
				if (lStatus != Constants.EQUIP_STATUS_NG) {
					lEquipStatus.setStatus(Constants.EQUIP_STATUS_NORMAL);
					lEquipStatus.setDateTime(new Date());
					equipStatusManager.updateEquipStatus(lEquipStatus);
					logger.info("processTPNDRelatedOPEFlagToNormal :: Equip Status changed to "
							+ Constants.EQUIP_STATUS_NORMAL + " for Equip Id :: " + relatedDeviceId);
				}
			} else {
				logger.error("processTPNDRelatedOPEFlagToNormal :: Equip Status not found for " + relatedDeviceId);
			}
		}
	}

	public void processTPNDRelatedOPEFlagToNg(String equipId) {
		List<String> deviceRelatedList = tpndManager.findDeviceList(equipId);
		for (String relatedDeviceId : deviceRelatedList) {
			EquipStatus lEquipStatus = equipStatusManager.findEquipStatusByEquipIdAndStatusCode(relatedDeviceId,
					Constants.OPE_STATE);
			if (lEquipStatus != null) {
				int lStatus = lEquipStatus.getStatus();
				if (lStatus != Constants.EQUIP_STATUS_NORMAL) {
					lEquipStatus.setStatus(Constants.EQUIP_STATUS_NG);
					lEquipStatus.setDateTime(new Date());
					equipStatusManager.updateEquipStatus(lEquipStatus);
					logger.info("processTPNDRelatedOPEFlagToNg :: Equip Status changed to " + Constants.EQUIP_STATUS_NG
							+ " for Equip Id :: " + relatedDeviceId);
				}
			} else {
				logger.error("processTPNDRelatedOPEFlagToNg :: Equip Status not found for " + relatedDeviceId);
			}
		}
	}

	public void restoreTPNDProcessedList(String equipId) {
		logger.debug("Calling restoreTPNDProcessedList .....");
		TPNDRaisedAlarm newProcessedAlarm = new TPNDRaisedAlarm();

		// Set the correct TPND error into the processed list for recovery processing
		if (equipId.startsWith("tpn_")) {
			String procMdbId = TPNDConstants.MDB_EQUIP_HEADER + equipId.substring(4, 10);
			List<String> relatedDeviceList = tpndManager.findDeviceList(procMdbId);
			List<TechnicalAlarmDto> relatedDeviceDtoList = new ArrayList<TechnicalAlarmDto>();

			// Set tpn DTO
			newProcessedAlarm
					.setAlarmDto(convertEntityToTechAlarmDto(tpndManager.getTPNTechnicalAlarmByEquipId(equipId)));
			// Set related Devices Dto
			for (String thisDevice : relatedDeviceList) {
				if (thisDevice.startsWith("tip_") || thisDevice.startsWith("tsp_") || thisDevice.startsWith("tep_")
						|| thisDevice.startsWith("ttp_") || thisDevice.startsWith("tic_")) { // "tip","tsp","tep","ttp","tic"
					relatedDeviceDtoList.add(convertEntityToTechAlarmDto(technicalAlarmManager
							.getTechnicalAlarmByEquipIdAndAlarmCode(thisDevice, TPNDConstants.TICSS_LINK_DOWN)));
				} else {
					relatedDeviceDtoList.add(convertEntityToTechAlarmDto(technicalAlarmManager
							.getTechnicalAlarmByEquipIdAndAlarmCode(thisDevice, Constants.LINK_DOWN)));
				}
			}

			newProcessedAlarm.setEquipDtoList(relatedDeviceDtoList);

		} else {
			TechnicalAlarmDto thisDto = convertEntityToTechAlarmDto(
					technicalAlarmManager.getTechnicalAlarmByEquipIdAndAlarmCode(equipId, Constants.LINK_DOWN));
			newProcessedAlarm.setAlarmDto(thisDto);
			newProcessedAlarm.setStartDate(thisDto.getStartDate());
			List<String> relatedDeviceList = tpndManager.findDeviceList(equipId);
			List<TechnicalAlarmDto> relatedDeviceDtoList = new ArrayList<TechnicalAlarmDto>();
			for (String thisDevice : relatedDeviceList) {
				if (thisDevice.startsWith("tip_") || thisDevice.startsWith("tsp_") || thisDevice.startsWith("ttp_")) {
					relatedDeviceDtoList.add(convertEntityToTechAlarmDto(technicalAlarmManager
							.getTechnicalAlarmByEquipIdAndAlarmCode(thisDevice, TPNDConstants.TICSS_LINK_DOWN)));
				} else {
					relatedDeviceDtoList.add(convertEntityToTechAlarmDto(technicalAlarmManager
							.getTechnicalAlarmByEquipIdAndAlarmCode(thisDevice, Constants.LINK_DOWN)));
				}
			}

			newProcessedAlarm.setEquipDtoList(relatedDeviceDtoList);
		}

		boolean addedCheck = false;
		for (TPNDRaisedAlarm thisAlarm : tpndManager.getProcessedList()) {
			if (thisAlarm.getAlarmDto().getAlarmId().equals(newProcessedAlarm.getAlarmDto().getAlarmId())) {
				addedCheck = true;
			}
		}

		if (addedCheck == false) {
			tpndManager.getProcessedList().add(newProcessedAlarm);
			logger.info("restoreTPNDProcessedList :: Added processed alarm into tpnd processed list :"
					+ newProcessedAlarm.getAlarmDto().getAlarmId());
		}
	}

	private TechnicalAlarmDto convertEntityToTechAlarmDto(TechnicalAlarm pTechAlarm) {
		logger.debug("Calling convertEntityToTechAlarmDto .....");
		TechnicalAlarmDto lTechnicalAlarmDto = new TechnicalAlarmDto();
		if (pTechAlarm != null) {
			lTechnicalAlarmDto.setAlarmId(pTechAlarm.getAlarmId());
			lTechnicalAlarmDto.setEquipId(pTechAlarm.getEquipId());
			lTechnicalAlarmDto.setAlarmCode(pTechAlarm.getAlarmCode());
			lTechnicalAlarmDto.setStartDate(pTechAlarm.getStartDate());
			lTechnicalAlarmDto.setAckDate(pTechAlarm.getAckDate());
			lTechnicalAlarmDto.setAckBy(pTechAlarm.getAckBy());
			lTechnicalAlarmDto.setSystemId(Constants.SYSTEM_ID);
			lTechnicalAlarmDto.setStatus(new Integer(pTechAlarm.getStatus()));

			EquipConfig thisEquipConfig = tpndManager.findEquipConfigById(pTechAlarm.getEquipId());
			if (thisEquipConfig != null) {
				lTechnicalAlarmDto.setEquipType(thisEquipConfig.getEquipType());
			} else if (pTechAlarm.getEquipId().contains("tpn_")) {
				lTechnicalAlarmDto.setEquipType(TPNDConstants.TPND_PAIR_TYPE);
			}
		} else {
			logger.error("convertEntityToTechAlarmDto :: pTechAlarm is null");
		}

		return lTechnicalAlarmDto;
	}

	public void processTPNPairOPEState() {
		logger.info("Calling processTPNPairOPEState .....");
		try {
			WebApplicationContext springContext = (WebApplicationContext) ApplicationContextProvider
					.getApplicationContext();
			String lOpeFlag = (String) springContext.getServletContext().getAttribute("OPE_FLAG");

			logger.info("lOpeFlag : " + lOpeFlag);
			List<TechnicalAlarm> lTPNAlarmList = technicalAlarmManager.getAllTPNTechnicalAlarm();
			// Rearrange TPN alarms according to their mdb
			HashMap<String, List<TechnicalAlarm>> hashMap = new HashMap<String, List<TechnicalAlarm>>();
			List<TechnicalAlarm> fullSetTPNTechAlarmList = new ArrayList<TechnicalAlarm>();
			List<TechnicalAlarm> toRemoveTPNTechAlarmList = new ArrayList<TechnicalAlarm>();
			for (TechnicalAlarm lTechnicalAlarm : lTPNAlarmList) {
				String mdbID = "";
				mdbID = TPNDConstants.MDB_EQUIP_HEADER + lTechnicalAlarm.getEquipId().substring(4, 10);
				if (!hashMap.containsKey(mdbID)) {
					List<TechnicalAlarm> TechAlarmlist = new ArrayList<TechnicalAlarm>();
					TechAlarmlist.add(lTechnicalAlarm);
					hashMap.put(mdbID, TechAlarmlist);
				} else {
					hashMap.get(mdbID).add(lTechnicalAlarm);
				}

				// find full sets of pairs error
				for (Map.Entry<String, List<TechnicalAlarm>> mapElement : hashMap.entrySet()) {
					String thisMDBId = (String) mapElement.getKey();
					List<TechnicalAlarm> thisTechAlarmList = (List<TechnicalAlarm>) mapElement.getValue();
					List<String> dbMdbTelcoList = tpndManager.findTelcoListByMDB(thisMDBId);
					if (thisTechAlarmList.size() == dbMdbTelcoList.size()) { // full set found
						fullSetTPNTechAlarmList.add(thisTechAlarmList.get(0));
						toRemoveTPNTechAlarmList.addAll(thisTechAlarmList);
					}
				}
			}
			lTPNAlarmList.removeAll(toRemoveTPNTechAlarmList);

			// processing TPN tech alarms
			if (!fullSetTPNTechAlarmList.isEmpty()) {
				logger.info("processTPNPairOPEState :: processing TPN tech alarms " + fullSetTPNTechAlarmList.size());
				for (TechnicalAlarm thisTPNAlarm : fullSetTPNTechAlarmList) {
					String procMdbId = TPNDConstants.MDB_EQUIP_HEADER + thisTPNAlarm.getEquipId().substring(4, 10);
					List<String> procTelcoIdList = tpndManager.findTelcoListByMDB(procMdbId);

					// Process Mdb Status
					EquipStatus mdbEquipStatus = equipStatusManager.findEquipStatusByEquipIdAndStatusCode(procMdbId,
							Constants.OPE_STATE);
					if (mdbEquipStatus != null) {
						int lStatus = mdbEquipStatus.getStatus();
						if (lStatus != Constants.EQUIP_STATUS_NORMAL) {
							mdbEquipStatus.setStatus(Constants.EQUIP_STATUS_NG);
							mdbEquipStatus.setDateTime(new Date());
							equipStatusManager.updateEquipStatus(mdbEquipStatus);
							logger.info("processTPNPairOPEState :: Equip Status changed to " + Constants.EQUIP_STATUS_NG
									+ " for Equip Id :: " + procMdbId);
							processTPNDRelatedOPEFlagToNg(procMdbId);
							if (!TPNDConstants.FinalVersion) {
								restoreTPNDProcessedList(thisTPNAlarm.getEquipId());
							}
						}
					} else {
						logger.error("processTPNPairOPEState :: Equip Status not found for " + procMdbId);
					}

					// Process telco Status
					for (String thisTelcoId : procTelcoIdList) {
						EquipStatus telcoEquipStatus = equipStatusManager
								.findEquipStatusByEquipIdAndStatusCode(thisTelcoId, Constants.OPE_STATE);
						if (telcoEquipStatus != null) {
							int lStatus = mdbEquipStatus.getStatus();
							if (lStatus != Constants.EQUIP_STATUS_NORMAL) {
								telcoEquipStatus.setStatus(Constants.EQUIP_STATUS_NG);
								telcoEquipStatus.setDateTime(new Date());
								equipStatusManager.updateEquipStatus(telcoEquipStatus);
								if (!TPNDConstants.FinalVersion) {
									restoreTPNDProcessedList(thisTPNAlarm.getEquipId());
								}
								logger.info("processTPNPairOPEState :: Equip Status changed to "
										+ Constants.EQUIP_STATUS_NG + " for Equip Id :: " + thisTelcoId);
							}
						} else {
							logger.error("processTPNPairOPEState :: Equip Status not found for " + thisTelcoId);
						}
					}
				}
			} else {
				logger.info("processTPNPairOPEState :: Full set of TPNTechAlarmList is empty");
			}
		} catch (Exception e) {
			logger.error("processTPNPairOPEState :: Error in processing OPE State for TPN technical alarms ", e);
		}

	}
	/*************************************/
}