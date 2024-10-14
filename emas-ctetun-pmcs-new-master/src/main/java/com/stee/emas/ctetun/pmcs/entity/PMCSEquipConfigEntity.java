package com.stee.emas.ctetun.pmcs.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "equip_config", schema = "emastun")
public class PMCSEquipConfigEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "equip_id")
	private String equipId;

	@Column(name = "equip_type")
	private String equipType;
	
	@Column(name = "plc_host")
	private String plcHost;
	
	@Column(name = "status_address")
	private Integer statusAddress;
	
	@Column(name = "fels_code")
	private String felsCode;
	
	@Column(name = "status_address_length")
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
		return "PmcsDeviceConfigEntity [equipId=" + equipId + ", equipType=" + equipType + ", plcHost=" + plcHost
				+ ", statusAddress=" + statusAddress + ", felsCode=" + felsCode + ", maxReadRegister=" + maxReadRegister
				+ "]";
	}
}
