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
 * <p>Description : TPND Process Handler</p>
 * <p>Do all TPND processing - holds the business logic</p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Grace
 * @since Jul 18, 2019
 * @version 1.0
 */
package com.stee.emas.cmh.tpnd.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stee.emas.cmh.tpnd.common.SiteSupportEquip;
import com.stee.emas.cmh.tpnd.common.TPNDConstants;
import com.stee.emas.cmh.tpnd.TPNDHandler;
import com.stee.emas.cmh.tpnd.common.TechAlarmsGroup;
import com.stee.emas.common.dao.ConfigDao;
import com.stee.emas.common.dao.EquipStatusDao;
import com.stee.emas.common.dao.HistTechnicalAlarmDao;
import com.stee.emas.common.dao.TechnicalAlarmDao;
import com.stee.emas.common.dao.TPNDEquipConfigDao;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emas.common.entity.EquipConfig;
import com.stee.emas.common.entity.EquipStatus;
import com.stee.emas.common.entity.HistTechnicalAlarm;
import com.stee.emas.common.entity.SiteEquipConfig;
import com.stee.emas.common.entity.SystemParameter;
import com.stee.emas.common.entity.TechnicalAlarm;

@Service("tpndHandler")
@Transactional
public class TPNDHandlerImpl implements TPNDHandler {
	private static Logger logger = LoggerFactory.getLogger(TPNDHandlerImpl.class);

	@Autowired
	ConfigDao configDao;
	@Autowired
	TPNDEquipConfigDao tpndEquipConfigDao;
	@Autowired
	HistTechnicalAlarmDao histTADao;
	@Autowired
	TechnicalAlarmDao techAlarmDao;
	@Autowired
	EquipStatusDao equipStatusDao;

	// Added on 08/11/22 for special mapping check - 1 VMS and 1 IDS
	public boolean isInSpecialMapping(String equipID) {
		boolean vms_equip = false;
		boolean ids_equip = false;
		boolean result = false;
		
		List<String> connectedEquipList = tpndEquipConfigDao.getEquipListBySiteSupportId(equipID);
		
		if (connectedEquipList.size() == 2) {
			for (String thisEquipId : connectedEquipList) {
				SiteEquipConfig thisSiteEquipConfig = configDao.findSiteEquipConfigById(thisEquipId);
				if (thisSiteEquipConfig != null) {
					if (thisSiteEquipConfig.getSubSystemId().equals(TPNDConstants.TPND_TICSS_SYSTEM)) {
						if (!vms_equip) {
							vms_equip = true;
						} else { 
							return false;
						}
					} else if (thisSiteEquipConfig.getSubSystemId().equals(TPNDConstants.TPND_IDSS_SYSTEM)) {
						if (!ids_equip) {
							ids_equip = true;
						} else { 
							return false;
						}						
					}
				} else {
					logger.error("isInSpecialMapping() :: Equip Config not found :" + thisEquipId
							+ " Linked to EquipId :" + equipID);
				}
			}			
		}
		
		if ((vms_equip) && (ids_equip)) {
			if (logger.isInfoEnabled()) {
				logger.info("isInSpecialMapping() :: Special Mapping Equipment " + equipID);
			}
			result = true;
		}
		
		return result;
	}

	// Added on 11/04/22 for IDSS implementation into TPND
	public boolean isThereSpecialEquipment(String equipID) {
		List<String> connectedEquipList = tpndEquipConfigDao.getLinkedEquipListByEquipId(equipID);
		for (String thisEquipId : connectedEquipList) {
			SiteEquipConfig thisSiteEquipConfig = configDao.findSiteEquipConfigById(thisEquipId);
			if (thisSiteEquipConfig != null) {
				if ((thisSiteEquipConfig.getSubSystemId().equals(TPNDConstants.TPND_TICSS_SYSTEM))
						|| (thisSiteEquipConfig.getSubSystemId().equals(TPNDConstants.TPND_IDSS_SYSTEM))) {
					if (logger.isInfoEnabled()) {
						logger.info("isThereSpecialEquipment() :: TICSS/IDSS Equipment " + thisEquipId
								+ " Linked to EquipId :" + equipID);
					}
					return true;
				}
			} else {
				logger.error("isThereSpecialEquipment() :: Equip Config not found :" + thisEquipId
						+ " Linked to EquipId :" + equipID);
			}
		}

		return false;
	}

	// Added on 11/04/22 for IDSS implementation into TPND
	public boolean isThereSpecialEquipInTPNDDevice(String equipID) {
		// Check on equipID to see what type of system.
		List<String> equipList = tpndEquipConfigDao.getEquipListBySiteSupportId(equipID);

		for (String thisEquipId : equipList) {
			SiteEquipConfig thisSiteEquipConfig = configDao.findSiteEquipConfigById(thisEquipId);
			if ((thisSiteEquipConfig != null)
					&& ((thisSiteEquipConfig.getSubSystemId().equals(TPNDConstants.TPND_TICSS_SYSTEM))
							|| (thisSiteEquipConfig.getSubSystemId().equals(TPNDConstants.TPND_IDSS_SYSTEM)))) {
				logger.info("isThereSpecialEquipInTPNDDevice() :: TICSS/IDSS Equipment Linked to MDB :"
						+ thisSiteEquipConfig.getPowerGridAccountNo());
				return true;
			}
		}
		return false;
	}

	public String findRelatedMdbByEquip(String equipID) {
		SiteEquipConfig thisSiteEquipConfig = configDao.findSiteEquipConfigById(equipID);
		if (thisSiteEquipConfig == null) {
			return "";
		}
		return thisSiteEquipConfig.getPowerGridAccountNo();
	}

	public String findRelatedTelcoByEquip(String equipID) {
		SiteEquipConfig thisSiteEquipConfig = configDao.findSiteEquipConfigById(equipID);
		if (thisSiteEquipConfig == null) {
			return "";
		}
		return thisSiteEquipConfig.getSingtelCircuitNo();
	}

	public boolean isSiteSupportDown(SiteSupportEquip siteSupportEquip) {
		if (siteSupportEquip != null) {
			if ((TPNDConstants.TPND_TELCO_TYPE.equals(siteSupportEquip.getEquipType()))
					&& (siteSupportEquip.getEquipList().size() == siteSupportEquip.getTotalEquips())) {
				if (logger.isInfoEnabled()) {
					logger.info("isSiteSupportDown :: return true");
				}
				return true;
			}

			if ((TPNDConstants.TPND_MDB_TYPE.equals(siteSupportEquip.getEquipType()))
					&& (siteSupportEquip.getEquipList().size() == siteSupportEquip.getTotalEquips())) {
				if (logger.isInfoEnabled()) {
					logger.info("isSiteSupportDown :: return true");
				}
				return true;
			}
		} else {
			logger.error("isSiteSupportDown :: siteSupportEquip is null");
		}

		if (logger.isInfoEnabled()) {
			logger.info("isSiteSupportDown :: return false");
		}
		return false;
	}

	public List<SiteSupportEquip> getDownMDBList(List<TechAlarmsGroup> processTAGroupList) {
		List<SiteSupportEquip> mdbList = new ArrayList<SiteSupportEquip>();
		for (TechAlarmsGroup thisGroup : processTAGroupList) {
			if (!thisGroup.getRelatedMDB().isEmpty()) {
				SiteSupportEquip newMDBInfo = new SiteSupportEquip();

				newMDBInfo.setEquipId(thisGroup.getRelatedMDB());
				newMDBInfo.setEquipType(TPNDConstants.TPND_MDB_TYPE);
				newMDBInfo.setStartDate(thisGroup.getStartDate());

				// find total equips MDB should have from database
				newMDBInfo.setTotalEquips(
						tpndEquipConfigDao.getEquipListBySiteSupportId(thisGroup.getRelatedMDB()).size());
				for (TechnicalAlarmDto thisAlarmDto : thisGroup.getTAList()) {
					newMDBInfo.addEquipIntoList(thisAlarmDto.getEquipId());
				}

				mdbList.add(newMDBInfo);
			}
		}

		List<SiteSupportEquip> downList = new ArrayList<SiteSupportEquip>();
		for (SiteSupportEquip thisMDB : mdbList) {
			if (isSiteSupportDown(thisMDB)) {
				logger.info("getDownMDBList :: Down MDB added to list :" + thisMDB.getEquipId());
				downList.add(thisMDB);
			}
		}

		return downList;
	}

	public List<SiteSupportEquip> getDownTelcoList(List<TechAlarmsGroup> processTAGroupList) {
		List<SiteSupportEquip> telcoList = new ArrayList<SiteSupportEquip>();
		for (TechAlarmsGroup thisGroup : processTAGroupList) {
			if (thisGroup.getRelatedMDB().isEmpty()) {
				break;
			}

			for (TechnicalAlarmDto thisDto : thisGroup.getTAList()) {
				String relatedMdb = findRelatedMdbByEquip(thisDto.getEquipId());
				if (relatedMdb != null) { // skip processing of telco if mdb is null to reduce processing of equipment
											// not powered by a mdb - Grace 5 May
					SiteEquipConfig lSiteEquipConfig = configDao.findSiteEquipConfigById(thisDto.getEquipId());
					if (lSiteEquipConfig != null) {
						String lTelcoID = lSiteEquipConfig.getSingtelCircuitNo();
						if ((lTelcoID != null) && (!lTelcoID.isEmpty())) {
							// Check if Telco is already added
							boolean isTelcoExist = false;
							if (!telcoList.isEmpty()) {
								for (SiteSupportEquip telcoalarminfo : telcoList) { // Telco is already in
																					// _mdbAlarmInfoList
									if (lTelcoID.equals(telcoalarminfo.getEquipId())) { // Telco exist
										// Update Exisiting TelcoAlarmInfo
										isTelcoExist = true;
										telcoalarminfo.addEquipIntoList(thisDto.getEquipId());
									}
								}
							}

							if (!isTelcoExist) {
								// Setup Telco Info into TelcoAlarmInfo
								SiteSupportEquip lTelcoAlarmInfo = new SiteSupportEquip();
								lTelcoAlarmInfo.setEquipId(lTelcoID);
								lTelcoAlarmInfo.setEquipType(TPNDConstants.TPND_TELCO_TYPE);
								lTelcoAlarmInfo.setStartDate(thisDto.getStartDate());
								// find total equips Telco should have from database
								lTelcoAlarmInfo.setTotalEquips(
										tpndEquipConfigDao.getEquipListBySiteSupportId(lTelcoID).size());
								lTelcoAlarmInfo.addEquipIntoList(thisDto.getEquipId());

								telcoList.add(lTelcoAlarmInfo);
							}
						}
					} else {
						logger.error("getDownTelcoList :: Equip Config not found in DB :" + thisDto.getEquipId());
					}
				}
			}
		}

		List<SiteSupportEquip> downList = new ArrayList<SiteSupportEquip>();
		for (SiteSupportEquip lTelco : telcoList) {
			if (isSiteSupportDown(lTelco)) {
				logger.info("getDownTelcoList :: Down Telco added to list :" + lTelco.getEquipId());
				downList.add(lTelco);
			}
		}
		return downList;
	}

	public List<String> getEquipListBySiteSupportId(String equipID) {
		return tpndEquipConfigDao.getEquipListBySiteSupportId(equipID);
	}

	public HistTechnicalAlarm findLatestHistTechAlarmByAlarmId(String pAlarmId) {
		return histTADao.findLatestHistTechAlarmByAlarmId(pAlarmId);
	}

	public List<String> getTelcoListByMdbId(String equipID) {
		return tpndEquipConfigDao.getTelcoListByMdbId(equipID);
	}

	public int getNoOfTelcoByMdbId(String equipID) {
		return tpndEquipConfigDao.getTelcoListByMdbId(equipID).size();
	}

	public int getNoOfEquipsByMdbId(String equipID) {
		return tpndEquipConfigDao.getEquipListBySiteSupportId(equipID).size();
	}

	public String getMdbIdByTelcoId(String equipID) {
		return tpndEquipConfigDao.getMdbIdByTelcoId(equipID);
	}

	public TechnicalAlarm findTechnicalAlarmById(String pId) {
		return techAlarmDao.findTechnicalAlarmById(pId);
	}

	public EquipConfig findEquipConfigById(String pEquipId) {
		return configDao.findEquipConfigById(pEquipId);
	}

	public List<TechnicalAlarm> findTPNPairAlarmByMDB(String pMdbId) {
		return techAlarmDao.getTPNPairTechnicalAlarmsByMDB(pMdbId);
	}

	public EquipStatus findEquipStatusByEquipIdAndStatusCode(String pEquipId, String pStatusCode) {
		return equipStatusDao.findEquipStatusByEquipIdAndStatusCode(pEquipId, pStatusCode);
	}

	public SystemParameter findSystemParameter(String pName) {
		return configDao.findSystemParameterByName(pName);
	}
	
	public TechnicalAlarm getTPNTechnicalAlarmByEquipId(String pEquipId) {
		return techAlarmDao.getTPNTechnicalAlarmByEquipId(pEquipId);
	}
}