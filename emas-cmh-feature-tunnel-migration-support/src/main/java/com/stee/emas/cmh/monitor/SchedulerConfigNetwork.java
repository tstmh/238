/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.monitor;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @author Scindia
 * @since May 3, 2013
 * @version 1.0
 *
 */

public class SchedulerConfigNetwork {
	
	private static SchedulerConfigNetwork schedulerConfigNetwork = new SchedulerConfigNetwork();
	
	private int pingInterval;
	List<String> equipIdList;
	Map<String, Integer> pingStateMap	= new TreeMap<String, Integer>();
	
	private SchedulerConfigNetwork() {
		
	}
	
	public static SchedulerConfigNetwork getInstance() {
		return schedulerConfigNetwork;
	}

	
	/**
	 * @return the pingInterval
	 */
	public int getPingInterval() {
		return pingInterval;
	}

	/**
	 * @param pingInterval the pingInterval to set
	 */
	public void setPingInterval(int pingInterval) {
		this.pingInterval = pingInterval;
	}

	/**
	 * @return the equipIdList
	 */
	public List<String> getEquipIdList() {
		return equipIdList;
	}

	/**
	 * @param equipIdList the equipIdList to set
	 */
	public void setEquipIdList(List<String> equipIdList) {
		this.equipIdList = equipIdList;
	}

	/**
	 * @return the pingStateMap
	 */
	public Map<String, Integer> getPingStateMap() {
		return pingStateMap;
	}

	/**
	 * @param pingStateMap the pingStateMap to set
	 */
	public void setPingStateMap(Map<String, Integer> pingStateMap) {
		this.pingStateMap = pingStateMap;
	}
}