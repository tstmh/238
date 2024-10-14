package com.stee.emas.ctetun.pmcs.dto;

import java.io.Serializable;
import org.springframework.stereotype.Component;

@Component
public class PMCSEquipConfigDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String equipId;
	private String equipType;
	private String plcHost;
	private Integer statusAddress;
	private String felsCode;
	private Integer maxReadRegister;
	
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
	/**
	 * @return the statusAddress
	 */
	public Integer getStatusAddress() {
		return statusAddress;
	}
	/**
	 * @param statusAddress the statusAddress to set
	 */
	public void setStatusAddress(Integer statusAddress) {
		this.statusAddress = statusAddress;
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
	 * @return the maxReadRegister
	 */
	public Integer getMaxReadRegister() {
		return maxReadRegister;
	}
	/**
	 * @param maxReadRegister the maxReadRegister to set
	 */
	public void setMaxReadRegister(Integer maxReadRegister) {
		this.maxReadRegister = maxReadRegister;
	}
	
	@Override
	public String toString() {
		return "PmcsEquipConfigDto [equipId=" + equipId + ", equipType=" + equipType + ", plcHost=" + plcHost
				+ ", statusAddress=" + statusAddress + ", felsCode=" + felsCode + ", maxReadRegister=" + maxReadRegister
				+ "]";
	}
}
