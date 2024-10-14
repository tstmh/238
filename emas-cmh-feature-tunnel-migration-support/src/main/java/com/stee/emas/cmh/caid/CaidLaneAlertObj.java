/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.caid;

import java.util.LinkedHashMap;

import com.stee.emas.common.dto.TrafficAlertDto;

/**
 * 
 * @author Scindia
 * @since Jun 2, 2015
 * @version 1.0
 *
 */

public class CaidLaneAlertObj {
	
	LinkedHashMap<Integer, TrafficAlertDto> laneAlertMap =  new LinkedHashMap<Integer, TrafficAlertDto>();
	
	

	public CaidLaneAlertObj(LinkedHashMap<Integer, TrafficAlertDto> laneAlertMap) {
		//super();
		this.laneAlertMap = laneAlertMap;
	}

	/**
	 * @return the laneAlertMap
	 */
	public LinkedHashMap<Integer, TrafficAlertDto> getLaneAlertMap() {
		return laneAlertMap;
	}

	/**
	 * @param laneAlertMap the laneAlertMap to set
	 */
	public void setLaneAlertMap(LinkedHashMap<Integer, TrafficAlertDto> laneAlertMap) {
		this.laneAlertMap = laneAlertMap;
	}

	
}