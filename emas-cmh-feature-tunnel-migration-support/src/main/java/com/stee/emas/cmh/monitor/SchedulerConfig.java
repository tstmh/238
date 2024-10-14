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
 * @since Apr 23, 2013
 * @version 1.0
 *
 */
public class SchedulerConfig {
	
	private static SchedulerConfig schedulerConfig = new SchedulerConfig();

	private int pingInterval;
	private int retryCount;
	private String nwt1Ip;
	private String nwt2Ip;
	
	private boolean ticssAlarmRaised;
	private int ticssRetryCount;
	
	private boolean scssAlarmRaised;
	private int scssRetryCount;
	
	private boolean emasccsAlarmRaised;
	private int emasccsRetryCount;
	
	private boolean emasitptAlarmRaised;
	private int emasitptRetryCount;
	
	private boolean emastctsAlarmRaised;
	private int emastctsRetryCount;

	private boolean dcssAlarmRaised;
	private int dcssRetryCount;
	
	private boolean wcssAlarmRaised;
	private int wcssRetryCount;
	
	private boolean idssAlarmRaised;
	private int idssRetryCount;
	
	private boolean nmsAlarmRaised;
	private int nmsRetryCount;
	
	private boolean nwt1AlarmRaised;
	private int nwt1RetryCount;
	
	private boolean nwt2AlarmRaised;
	private int nwt2RetryCount;
	
	private boolean lusAlarmRaised;
	private int lusRetryCount;
	
	Map<String, Integer> nmsStateMap	= new TreeMap<String, Integer>();
	
	private SchedulerConfig() {
		
	}
	
	public static SchedulerConfig getInstance() {
		return schedulerConfig;
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
	 * @return the retryCount
	 */
	public int getRetryCount() {
		return retryCount;
	}
	/**
	 * @param retryCount the retryCount to set
	 */
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	
	/**
	 * @return the nwt1Ip
	 */
	public String getNwt1Ip() {
		return nwt1Ip;
	}

	/**
	 * @param nwt1Ip the nwt1Ip to set
	 */
	public void setNwt1Ip(String nwt1Ip) {
		this.nwt1Ip = nwt1Ip;
	}

	/**
	 * @return the nwt2Ip
	 */
	public String getNwt2Ip() {
		return nwt2Ip;
	}

	/**
	 * @param nwt2Ip the nwt2Ip to set
	 */
	public void setNwt2Ip(String nwt2Ip) {
		this.nwt2Ip = nwt2Ip;
	}

	/**
	 * @return the ticssAlarmRaised
	 */
	public boolean getTicssAlarmRaised() {
		return ticssAlarmRaised;
	}
	/**
	 * @param ticssAlarmRaised the ticssAlarmRaised to set
	 */
	public void setTicssAlarmRaised(boolean ticssAlarmRaised) {
		this.ticssAlarmRaised = ticssAlarmRaised;
	}
	/**
	 * @return the ticssRetryCount
	 */
	public int getTicssRetryCount() {
		return ticssRetryCount;
	}
	/**
	 * @param ticssRetryCount the ticssRetryCount to set
	 */
	public void setTicssRetryCount(int ticssRetryCount) {
		this.ticssRetryCount = ticssRetryCount;
	}

	/**
	 * @return the scssAlarmRaised
	 */
	public boolean getScssAlarmRaised() {
		return scssAlarmRaised;
	}

	/**
	 * @param scssAlarmRaised the scssAlarmRaised to set
	 */
	public void setScssAlarmRaised(boolean scssAlarmRaised) {
		this.scssAlarmRaised = scssAlarmRaised;
	}

	/**
	 * @return the scssRetryCount
	 */
	public int getScssRetryCount() {
		return scssRetryCount;
	}

	/**
	 * @param scssRetryCount the scssRetryCount to set
	 */
	public void setScssRetryCount(int scssRetryCount) {
		this.scssRetryCount = scssRetryCount;
	}

	/**
	 * @return the emasccsAlarmRaised
	 */
	public boolean getEmasccsAlarmRaised() {
		return emasccsAlarmRaised;
	}

	/**
	 * @param emasccsAlarmRaised the emasccsAlarmRaised to set
	 */
	public void setEmasccsAlarmRaised(boolean emasccsAlarmRaised) {
		this.emasccsAlarmRaised = emasccsAlarmRaised;
	}

	/**
	 * @return the emasccsRetryCount
	 */
	public int getEmasccsRetryCount() {
		return emasccsRetryCount;
	}

	/**
	 * @param emasccsRetryCount the emasccsRetryCount to set
	 */
	public void setEmasccsRetryCount(int emasccsRetryCount) {
		this.emasccsRetryCount = emasccsRetryCount;
	}

	/**
	 * @return the emasitptAlarmRaised
	 */
	public boolean getEmasitptAlarmRaised() {
		return emasitptAlarmRaised;
	}

	/**
	 * @param emasitptAlarmRaised the emasitptAlarmRaised to set
	 */
	public void setEmasitptAlarmRaised(boolean emasitptAlarmRaised) {
		this.emasitptAlarmRaised = emasitptAlarmRaised;
	}

	/** 
	 * @return the emasccsRetryCount
	 */
	public int getEmasitptRetryCount() {
		return emasitptRetryCount;
	}

	/**
	 * @param emasccsRetryCount the emasccsRetryCount to set
	 */
	public void setEmasitptRetryCount(int emasitptRetryCount) {
		this.emasitptRetryCount = emasitptRetryCount;
	}

	/**
	 * @return the emastctsAlarmRaised
	 */
	public boolean getEmastctsAlarmRaised() {
		return emastctsAlarmRaised;
	}

	/**
	 * @param emastctsAlarmRaised the emastctsAlarmRaised to set
	 */
	public void setEmastctsAlarmRaised(boolean emastctsAlarmRaised) {
		this.emastctsAlarmRaised = emastctsAlarmRaised;
	}

	/** 
	 * @return the emasccsRetryCount
	 */
	public int getEmastctsRetryCount() {
		return emastctsRetryCount;
	}

	/**
	 * @param emasccsRetryCount the emasccsRetryCount to set
	 */
	public void setEmastctsRetryCount(int emastctsRetryCount) {
		this.emastctsRetryCount = emastctsRetryCount;
	}

	/**
	 * @return the dcssAlarmRaised
	 */
	public boolean getDcssAlarmRaised() {
		return dcssAlarmRaised;
	}

	/**
	 * @param dcssAlarmRaised the dcssAlarmRaised to set
	 */
	public void setDcssAlarmRaised(boolean dcssAlarmRaised) {
		this.dcssAlarmRaised = dcssAlarmRaised;
	}

	/**
	 * @return the dcssRetryCount
	 */
	public int getDcssRetryCount() {
		return dcssRetryCount;
	}

	/**
	 * @param dcssRetryCount the dcssRetryCount to set
	 */
	public void setDcssRetryCount(int dcssRetryCount) {
		this.dcssRetryCount = dcssRetryCount;
	}

	/**
	 * @return the wcssAlarmRaised
	 */
	public boolean getWcssAlarmRaised() {
		return wcssAlarmRaised;
	}

	/**
	 * @param wcssAlarmRaised the wcssAlarmRaised to set
	 */
	public void setWcssAlarmRaised(boolean wcssAlarmRaised) {
		this.wcssAlarmRaised = wcssAlarmRaised;
	}

	/**
	 * @return the wcssRetryCount
	 */
	public int getWcssRetryCount() {
		return wcssRetryCount;
	}

	/**
	 * @param wcssRetryCount the wcssRetryCount to set
	 */
	public void setWcssRetryCount(int wcssRetryCount) {
		this.wcssRetryCount = wcssRetryCount;
	}
	
	/**
	 * @return the idssAlarmRaised
	 */
	public boolean getIdssAlarmRaised() {
		return idssAlarmRaised;
	}
	
	/**
	 * @param idssAlarmRaised the idssAlarmRaised to set
	 */
	public void setIdssAlarmRaised(boolean idssAlarmRaised) {
		this.idssAlarmRaised = idssAlarmRaised;
	}

	/**
	 * @return the idssRetryCount
	 */	
	public int getIdssRetryCount() {
		return idssRetryCount;
	}

	/**
	 * @param idssRetryCount the idssRetryCount to set
	 */
	public void setIdssRetryCount(int idssRetryCount) {
		this.idssRetryCount = idssRetryCount;
	}

	public boolean getLusAlarmRaised() {
		return lusAlarmRaised;
	}

	public void setLusAlarmRaised(boolean lusAlarmRaised) {
		this.lusAlarmRaised = lusAlarmRaised;
	}

	public int getLusRetryCount() {
		return lusRetryCount;
	}

	public void setLusRetryCount(int lusRetryCount) {
		this.lusRetryCount = lusRetryCount;
	}

	/**
	 * @return the nmsAlarmRaised
	 */
	public boolean getNmsAlarmRaised() {
		return nmsAlarmRaised;
	}

	/**
	 * @param nmsAlarmRaised the nmsAlarmRaised to set
	 */
	public void setNmsAlarmRaised(boolean nmsAlarmRaised) {
		this.nmsAlarmRaised = nmsAlarmRaised;
	}

	/**
	 * @return the nmsRetryCount
	 */
	public int getNmsRetryCount() {
		return nmsRetryCount;
	}

	/**
	 * @param nmsRetryCount the nmsRetryCount to set
	 */
	public void setNmsRetryCount(int nmsRetryCount) {
		this.nmsRetryCount = nmsRetryCount;
	}

	/**
	 * @return the nwt1AlarmRaised
	 */
	public boolean getNwt1AlarmRaised() {
		return nwt1AlarmRaised;
	}

	/**
	 * @param nwt1AlarmRaised the nwt1AlarmRaised to set
	 */
	public void setNwt1AlarmRaised(boolean nwt1AlarmRaised) {
		this.nwt1AlarmRaised = nwt1AlarmRaised;
	}

	/**
	 * @return the nwt1RetryCount
	 */
	public int getNwt1RetryCount() {
		return nwt1RetryCount;
	}

	/**
	 * @param nwt1RetryCount the nwt1RetryCount to set
	 */
	public void setNwt1RetryCount(int nwt1RetryCount) {
		this.nwt1RetryCount = nwt1RetryCount;
	}

	/**
	 * @return the nwt2AlarmRaised
	 */
	public boolean getNwt2AlarmRaised() {
		return nwt2AlarmRaised;
	}

	/**
	 * @param nwt2AlarmRaised the nwt2AlarmRaised to set
	 */
	public void setNwt2AlarmRaised(boolean nwt2AlarmRaised) {
		this.nwt2AlarmRaised = nwt2AlarmRaised;
	}

	/**
	 * @return the nwt2RetryCount
	 */
	public int getNwt2RetryCount() {
		return nwt2RetryCount;
	}

	/**
	 * @param nwt2RetryCount the nwt2RetryCount to set
	 */
	public void setNwt2RetryCount(int nwt2RetryCount) {
		this.nwt2RetryCount = nwt2RetryCount;
	}

	/**
	 * @return the nmsStateMap
	 */
	public Map<String, Integer> getNmsStateMap() {
		return nmsStateMap;
	}

	/**
	 * @param nmsStateMap the nmsStateMap to set
	 */
	public void setNmsStateMap(Map<String, Integer> nmsStateMap) {
		this.nmsStateMap = nmsStateMap;
	}
}