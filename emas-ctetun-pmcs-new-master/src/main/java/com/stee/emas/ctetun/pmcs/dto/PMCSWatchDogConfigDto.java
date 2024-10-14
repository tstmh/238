package com.stee.emas.ctetun.pmcs.dto;

import java.io.Serializable;
import org.springframework.stereotype.Component;

@Component
public class PMCSWatchDogConfigDto implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	private String plcHost= null;
	private int value = 0;
	private int count = 0;
	private int alarmStatus = 2;
	
	public PMCSWatchDogConfigDto(String plcHost) {
		this.plcHost = plcHost;
	}
	
	public PMCSWatchDogConfigDto() {
	}
	
	/**
	 * @return the plcHost
	 */
	public String getPlcHost() {
		return plcHost;
	}
	/**
	 * @param plcHost the plcHost to set
	 */
	public void setPlcHost(String plcHost) {
		this.plcHost = plcHost;
	}
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * @return the alarmStatus
	 */
	public int getAlarmStatus() {
		return alarmStatus;
	}

	/**
	 * @param alarmStatus the alarmStatus to set
	 */
	public void setAlarmStatus(int alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	@Override
	public String toString() {
		return "PMCSWatchDogConfigDto [plcHost=" + plcHost + ", value=" + value + ", count=" + count + ", alarmStatus="
				+ alarmStatus + "]";
	}
}
