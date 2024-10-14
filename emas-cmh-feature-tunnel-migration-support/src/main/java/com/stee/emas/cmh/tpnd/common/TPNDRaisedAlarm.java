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

public class TPNDRaisedAlarm {
	private TechnicalAlarmDto thisAlarmDto;
	private List<TechnicalAlarmDto> relatedEquipDtoList;
	private Date startDate = null;

	public TPNDRaisedAlarm() {
		thisAlarmDto = new TechnicalAlarmDto();
		relatedEquipDtoList = new ArrayList<TechnicalAlarmDto>();
	}

	public TechnicalAlarmDto getAlarmDto() {
		return thisAlarmDto;
	}

	public List<TechnicalAlarmDto> getEquipDtoList() {
		return relatedEquipDtoList;
	}

	public void setAlarmDto(TechnicalAlarmDto thisAlarmDto) {
		this.thisAlarmDto = thisAlarmDto;
	}

	public void setEquipDtoList(List<TechnicalAlarmDto> relatedEquipDtoList) {
		this.relatedEquipDtoList = relatedEquipDtoList;
	}
	
	public boolean isStartDateSet() {
		boolean result = false;
		
		if (startDate != null) {
			result = true;
		}
		return result;
	}
	
	public void setStartDate() {
		this.startDate = new Date();
	}

	public void setStartDate(Date newDate) {
		this.startDate = newDate;
	}

	public boolean isWaitTimeOver(long waitTime) {
		Date curDate = new Date();

		if (this.startDate != null) {
			long diff = Math.abs(curDate.getTime() - this.startDate.getTime());

			if ((diff > waitTime) || (this.relatedEquipDtoList.isEmpty())) {
				return true;
			}
		}

		return false;
	}

}