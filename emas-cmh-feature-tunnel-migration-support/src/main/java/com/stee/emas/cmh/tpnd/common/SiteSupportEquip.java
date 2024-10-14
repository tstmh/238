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
 * <p>Description : SiteSupportEquip Info</p>
 * <p>This class of TPND Module holds information of the SiteSupportEquip(MDB or Telco) class needed by the module</p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Grace
 * @since Jul 18, 2019
 * @version 1.0
 */
package com.stee.emas.cmh.tpnd.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class SiteSupportEquip {
	// Variables
	private String equipId; // equipID
	private String equipType; // equipType
	private int totalEquips; // Total equipments linked
	private List<String> equipList; // equipid whose alarm received linked to this MDB
	private Date startDate;

	// Constructor class
	public SiteSupportEquip() {
		if (equipList == null)
			equipList = new ArrayList<String>();
	}

	// Setter and Getter for _equipId
	public void setEquipId(String equipID) {
		this.equipId = equipID;
	}

	public String getEquipId() {
		return this.equipId;
	}

	// Setter and Getter for _equipType
	public void setEquipType(String equipType) {
		this.equipType = equipType;
	}

	public String getEquipType() {
		return this.equipType;
	}

	// Setter and Getter for _totalEquips
	public void setTotalEquips(int totalEquips) {
		this.totalEquips = totalEquips;
	}

	public int getTotalEquips() {
		return this.totalEquips;
	}

	// Setter and Getter for _equipList
	public void setEquipList(List<String> equipList) {
		this.equipList = equipList;
	}

	public List<String> getEquipList() {
		return this.equipList;
	}

	// Setter and Getter for startDate
	public void setStartDate(Date date) {
		this.startDate = date;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	/*************
	 * Additional Functions for SiteSupportEquip
	 **************/
	// function to add unique equipment into equipList once alarm and is not inside
	// the list
	public boolean addEquipIntoList(String equipID) {
		if (this.equipList.contains(equipID))
			return false; // Equipment is already in the list
		this.equipList.add(equipID);
		return true;
	}
}