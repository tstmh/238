package com.stee.emas.ctetun.wmss.dto;

import java.io.Serializable;
import org.springframework.stereotype.Component;

@Component
public class WmssEquipConfigDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String equipId;
	private String equipType;
	private String plcHost;
	private String hostIp;
	private Integer statusAddress;
	private String felsCode;
	
	
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
	 * @return the equipType
	 */
	public String getEquipType() {
		return equipType;
	}
	/**
	 * @param equipType the equipType to set
	 */
	public void setEquipType(String equipType) {
		this.equipType = equipType;
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
	
	public String getHostIp() {
		return hostIp;
	}
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	/**
	 * @return the statusAddress
	 */
	public Integer getStatusAddress() {
		return statusAddress;
	}
	
	/**
	 * @return the felsCode
	 */
	public String getFelsCode() {
		return felsCode;
	}
	/**
	 * @param felsCode the felsCode to set
	 */
	public void setFelsCode(String felsCode) {
		this.felsCode = felsCode;
	}
	
	
	/**
	 * @param statusAddress the statusAddress to set
	 */
	public void setStatusAddress(Integer statusAddress) {
		this.statusAddress = statusAddress;
	}
	
	@Override
	public String toString() {
		return "WmcsEquipConfigDto [equipId=" + equipId + ", equipType=" + equipType + ", plcHost=" + plcHost
				+ ", statusAddress=" + statusAddress + "]";
	}
}
