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
 * <p>Description : Technical Alarm Group</p>
 * <p>Holds the groups of alarm received that are linked to the same MDB</p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Grace
 * @since Oct 29, 2019
 * @version 1.0
 */

package com.stee.emas.cmh.tpnd.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.stee.emas.common.dto.TechnicalAlarmDto;

public class TechAlarmsGroup {
	private String relatedMDB;
	private int numOfEquips;
	private long waitTime;
	private Date startDate;
	private Date startGroupDate;
	private List<TechnicalAlarmDto> taList;

	public TechAlarmsGroup() {
		taList = new ArrayList<TechnicalAlarmDto>();
		startGroupDate = new Date();
		startDate = new Date();
	}

	public TechAlarmsGroup(Date date) {
		taList = new ArrayList<TechnicalAlarmDto>();
		startGroupDate = new Date();
		startDate = date;
	}

	public String getRelatedMDB() {
		return relatedMDB;
	}

	public void setRelatedMDB(String relatedMDB) {
		this.relatedMDB = relatedMDB;
	}

	public int getNumOfEquips() {
		return numOfEquips;
	}

	public void setNumOfEquips(int equipNum) {
		this.numOfEquips = equipNum;
	}

	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

	public List<TechnicalAlarmDto> getTAList() {
		return taList;
	}

	public void setTAList(List<TechnicalAlarmDto> taList) {
		this.taList = taList;
	}

	public Date getStartDate() {
		return startDate;
	}

	public boolean isDeviceExisted(String equipID) {
		if (!taList.isEmpty()) {
			for (TechnicalAlarmDto thisDto : taList) {
				if (thisDto.getEquipId().equals(equipID)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean addDeviceIntoList(TechnicalAlarmDto lTechAlarmDto) {
		if (!isDeviceExisted(lTechAlarmDto.getEquipId())) {
			return taList.add(lTechAlarmDto);
		}
		return false;
	}
	
	public boolean removeDevicefromList(TechnicalAlarmDto lTechAlarmDto) {
		TechnicalAlarmDto toRemoveDto = null;
		for (TechnicalAlarmDto keptDto : taList) {
			if (keptDto.getEquipId().equals(lTechAlarmDto.getEquipId())) {
				toRemoveDto = keptDto;
				break;
			}
		}
		
		if (toRemoveDto != null) {
			return taList.remove(toRemoveDto);
		}
		return false;
	}

	public boolean isWaitTimeOver() {
		boolean result = false;
		Date curDate = new Date();

		long diff = Math.abs(curDate.getTime() - this.startGroupDate.getTime());

		if ((diff > this.waitTime) || (numOfEquips == taList.size())) {
			result = true;
		}

		return result;
	}
	
}