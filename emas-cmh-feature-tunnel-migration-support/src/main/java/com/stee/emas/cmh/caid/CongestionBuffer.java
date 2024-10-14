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
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.stee.emas.common.dto.TrafficAlertDto;

/**
 * 
 * @author Scindia
 * @since Oct 23, 2013
 * @version 1.0
 *
 */
@Component("congestionBuffer")
public class CongestionBuffer {
	
	
	Map<CaidKeyBean, TrafficAlertDto> congestionTempMap = new TreeMap<CaidKeyBean, TrafficAlertDto>();
	Map<String, CaidLaneAlertObj> congestionHoldingMap	= new TreeMap<String, CaidLaneAlertObj>();
	Map<String, TrafficAlertDto> congestionMap	= new TreeMap<String, TrafficAlertDto>();
	
	Map<Integer, TrafficAlertDto> LaneAlertMap = new LinkedHashMap<Integer, TrafficAlertDto>();
		
	public CongestionBuffer() {		
	}

	/**
	 * @return the congestionTempMap
	 */
	public Map<CaidKeyBean, TrafficAlertDto> getCongestionTempMap() {
		return congestionTempMap;
	}

	/**
	 * @param congestionTempMap the congestionTempMap to set
	 */
	public void setCongestionTempMap(Map<CaidKeyBean, TrafficAlertDto> congestionTempMap) {
		this.congestionTempMap = congestionTempMap;
	}

	/**
	 * @return the congestionHoldingMap
	 */
	public Map<String, CaidLaneAlertObj> getCongestionHoldingMap() {
		return congestionHoldingMap;
	}

	/**
	 * @param congestionHoldingMap the congestionHoldingMap to set
	 */
	public void setCongestionHoldingMap(Map<String, CaidLaneAlertObj> congestionHoldingMap) {
		this.congestionHoldingMap = congestionHoldingMap;
	}

	/**
	 * @return the congestionMap
	 */
	public Map<String, TrafficAlertDto> getCongestionMap() {
		return congestionMap;
	}

	/**
	 * @param congestionMap the congestionMap to set
	 */
	public void setCongestionMap(Map<String, TrafficAlertDto> congestionMap) {
		this.congestionMap = congestionMap;
	}
}