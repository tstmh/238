/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.monitor;

import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @author Scindia
 * @since Dec 12, 2013
 * @version 1.0
 *
 */

public class SchedulerConfigUps {
	
	private static SchedulerConfigUps schedulerConfigUps = new SchedulerConfigUps();
	
	Map<String, Integer> upsMap	= new TreeMap<String, Integer>();//EquipId_AlarmCode, Status
	//Map<String, Map<Integer, Integer>> upsMap	= new TreeMap<String, Map<Integer, Integer>>();//EquipId, Map -> AlarmCode, Status
	
	/*Map<Integer, Integer> ups1StateMap	= new TreeMap<Integer, Integer>();
	Map<Integer, Integer> ups2StateMap	= new TreeMap<Integer, Integer>();*/
	
	private SchedulerConfigUps() {
		
	}
	
	public static SchedulerConfigUps getInstance() {
		return schedulerConfigUps;
	}

	/**
	 * @return the upsMap
	 */
	public Map<String, Integer> getUpsMap() {
		return upsMap;
	}

	/**
	 * @param upsMap the upsMap to set
	 */
	public void setUpsMap(Map<String, Integer> upsMap) {
		this.upsMap = upsMap;
	}

	
}