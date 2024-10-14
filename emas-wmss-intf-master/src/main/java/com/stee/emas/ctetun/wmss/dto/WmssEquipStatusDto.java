package com.stee.emas.ctetun.wmss.dto;

import java.io.Serializable;
import org.springframework.stereotype.Component;
import com.google.gson.annotations.SerializedName;

@Component
public class WmssEquipStatusDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
			
	@SerializedName("ip")
	private String ipAddress;
	@SerializedName("did")
	private String equipId;
	@SerializedName("status")
	private String equipStatus;
	@SerializedName("time")
	private String time;
	
	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}
	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	/**
	 * @return the equipId
	 */
	public String getEquipId() {
		return equipId;
	}
	/**
	 * @param equipId the equipId to set
	 */
	public void setEquipId(String equipId) {
		this.equipId = equipId;
	}
	/**
	 * @return the equipStatus
	 */
	public String getEquipStatus() {
		return equipStatus;
	}
	/**
	 * @param equipStatus the equipStatus to set
	 */
	public void setEquipStatus(String equipStatus) {
		this.equipStatus = equipStatus;
	}
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return "WmssDeviceStatusDto [ipAddress=" + ipAddress + ", equipId=" + equipId + ", equipStatus="
				+ equipStatus + ", time=" + time + "]";
	}
}
