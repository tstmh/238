/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.monitor;

/**
 * 
 * @author Scindia
 * @since May 30, 2016
 * @version 1.0
 *
 */

public class SchedulerConfigGroup1 {
	
	private static SchedulerConfigGroup1 schedulerConfigGroup1 = new SchedulerConfigGroup1();

	private int pingInterval;
	private int retryCount;	
	
	private boolean lusAlarmRaised;
	private int lusRetryCount;
	
	private boolean fireAlarmRaised;
	private int fireRetryCount;
	
	private boolean itmsAlarmRaised;
	private int itmsRetryCount;
	
	private boolean pmcsAlarmRaised;
	private int pmcsRetryCount;
	
	private SchedulerConfigGroup1() {
		
	}
	
	public static SchedulerConfigGroup1 getInstance() {
		return schedulerConfigGroup1;
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
	 * @return the lusAlarmRaised
	 */
	public boolean getLusAlarmRaised() {
		return lusAlarmRaised;
	}
	/**
	 * @param lusAlarmRaised the lusAlarmRaised to set
	 */
	public void setLusAlarmRaised(boolean lusAlarmRaised) {
		this.lusAlarmRaised = lusAlarmRaised;
	}
	/**
	 * @return the lusRetryCount
	 */
	public int getLusRetryCount() {
		return lusRetryCount;
	}
	/**
	 * @param lusRetryCount the lusRetryCount to set
	 */
	public void setLusRetryCount(int lusRetryCount) {
		this.lusRetryCount = lusRetryCount;
	}
	/**
	 * @return the fireAlarmRaised
	 */
	public boolean getFireAlarmRaised() {
		return fireAlarmRaised;
	}
	/**
	 * @param fireAlarmRaised the fireAlarmRaised to set
	 */
	public void setFireAlarmRaised(boolean fireAlarmRaised) {
		this.fireAlarmRaised = fireAlarmRaised;
	}
	/**
	 * @return the fireRetryCount
	 */
	public int getFireRetryCount() {
		return fireRetryCount;
	}
	/**
	 * @param fireRetryCount the fireRetryCount to set
	 */
	public void setFireRetryCount(int fireRetryCount) {
		this.fireRetryCount = fireRetryCount;
	}
	/**
	 * @return the itmsAlarmRaised
	 */
	public boolean getItmsAlarmRaised() {
		return itmsAlarmRaised;
	}
	/**
	 * @param itmsAlarmRaised the itmsAlarmRaised to set
	 */
	public void setItmsAlarmRaised(boolean itmsAlarmRaised) {
		this.itmsAlarmRaised = itmsAlarmRaised;
	}
	/**
	 * @return the itmsRetryCount
	 */
	public int getItmsRetryCount() {
		return itmsRetryCount;
	}
	/**
	 * @param itmsRetryCount the itmsRetryCount to set
	 */
	public void setItmsRetryCount(int itmsRetryCount) {
		this.itmsRetryCount = itmsRetryCount;
	}
	/**
	 * @return the pmcsAlarmRaised
	 */
	public boolean getPmcsAlarmRaised() {
		return pmcsAlarmRaised;
	}
	/**
	 * @param pmcsAlarmRaised the pmcsAlarmRaised to set
	 */
	public void setPmcsAlarmRaised(boolean pmcsAlarmRaised) {
		this.pmcsAlarmRaised = pmcsAlarmRaised;
	}
	/**
	 * @return the pmcsRetryCount
	 */
	public int getPmcsRetryCount() {
		return pmcsRetryCount;
	}
	/**
	 * @param pmcsRetryCount the pmcsRetryCount to set
	 */
	public void setPmcsRetryCount(int pmcsRetryCount) {
		this.pmcsRetryCount = pmcsRetryCount;
	}
}