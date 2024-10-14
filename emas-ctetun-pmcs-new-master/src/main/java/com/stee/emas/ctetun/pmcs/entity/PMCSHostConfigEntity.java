package com.stee.emas.ctetun.pmcs.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name ="plc_config", schema ="emastun")
@IdClass(com.stee.emas.ctetun.pmcs.entity.PMCSHostConfigIdClass.class)
public class PMCSHostConfigEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;	
	
	@Id
	@Column(name ="plc_host")
	private String plcHost;
	
	@Column(name ="ip_address_a")
	private String hostIp;
	
	@Column(name ="ip_port_a")
	private String port;
	
	@Id
	@Column(name ="fels_code")
	private String felsCode;
	
	@Column(name = "slave_address")
	private Integer slaveAddress;

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
	 * @return the hostIp
	 */
	public String getHostIp() {
		return hostIp;
	}

	/**
	 * @param hostIp the hostIp to set
	 */
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
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
	 * @return the slaveAddress
	 */
	public Integer getSlaveAddress() {
		return slaveAddress;
	}

	/**
	 * @param slaveAddress the slaveAddress to set
	 */
	public void setSlaveAddress(Integer slaveAddress) {
		this.slaveAddress = slaveAddress;
	}

	@Override
	public String toString() {
		return "PmcsHostConfigEntity [plcHost=" + plcHost + ", hostIp=" + hostIp + ", port=" + port + ", felsCode="
				+ felsCode + ", slaveAddress=" + slaveAddress + "]";
	}
}
