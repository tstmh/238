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
 * @since Jul 18, 2019
 * @version 1.0
 */
package com.stee.emas.cmh.tpnd;

import java.util.List;

import com.stee.emas.cmh.tpnd.common.SiteSupportEquip;
import com.stee.emas.cmh.tpnd.common.TechAlarmsGroup;
import com.stee.emas.common.entity.EquipConfig;
import com.stee.emas.common.entity.EquipStatus;
import com.stee.emas.common.entity.HistTechnicalAlarm;
import com.stee.emas.common.entity.SystemParameter;
import com.stee.emas.common.entity.TechnicalAlarm;

public interface TPNDHandler {
	// Check if Aggregate/equipID is a TICSS group
	//public boolean isThereTICSSEquipments(String equipID);
	
	// Check if there is TICSS equipments in this TPND device
	//public boolean isThereTICSSEquipmentsInTPNDDevice(String equipID);

	// Find Mdb related to the equipId
	public String findRelatedMdbByEquip(String equipID);

	// Find Telco related to the equipId
	public String findRelatedTelcoByEquip(String equipID);

	// Check if the TPND device is down
	public boolean isSiteSupportDown(SiteSupportEquip siteSupportEquip);

	// Get back a list of down mdb devices from the list of TAG that are to be
	// processed
	public List<SiteSupportEquip> getDownMDBList(List<TechAlarmsGroup> processTAGroupList);

	// Get back a list of down telco devices from the list of TAG that are to be
	// processed
	public List<SiteSupportEquip> getDownTelcoList(List<TechAlarmsGroup> processTAGroupList);

	// Get back a list of equipment related to the mdb/telco deviceId
	public List<String> getEquipListBySiteSupportId(String equipID);

	// Find the latest Hist Technical Alarm according to AlarmId
	public HistTechnicalAlarm findLatestHistTechAlarmByAlarmId(String pAlarmId);

	// Find a List of Telcos that are related to the Mdb
	public List<String> getTelcoListByMdbId(String equipID);

	// Find a mdb that are related to the telco
	public String getMdbIdByTelcoId(String equipID);
	
	// Find the number of Telco linked to a MDB
	public int getNoOfTelcoByMdbId(String equipID);

	// Find the number of Equips linked to a MDB
	public int getNoOfEquipsByMdbId(String equipID);

	// Find the technical alarm by its alarmId
	public TechnicalAlarm findTechnicalAlarmById(String pId);

	// Find the equipment config of a device
	public EquipConfig findEquipConfigById(String pEquipId);
	
	// Find the list of TPN technical alarms by the mdb
	public List<TechnicalAlarm> findTPNPairAlarmByMDB(String pMdbId);
	
	public EquipStatus findEquipStatusByEquipIdAndStatusCode(String pEquipId, String pStatusCode);
	
	public SystemParameter findSystemParameter(String pName);
	
	public TechnicalAlarm getTPNTechnicalAlarmByEquipId(String pEquipId);
	
	// Added on 11/04/22 for IDSS implementation into TPND
	public boolean isThereSpecialEquipment(String equipID);
	public boolean isThereSpecialEquipInTPNDDevice(String equipID);
	
	public boolean isInSpecialMapping(String equipID);
}