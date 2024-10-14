package com.stee.emas.ctetun.pmcs.dto;

import java.io.Serializable;
import org.springframework.stereotype.Component;

@Component
public class PMCSHostConfigDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String plcHost;
	private String hostIp;
	private String port;
	private String felsCode;
	private Integer slaveAddress;
	
	private int plcStatus = 0;
	
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
	
	public int getPlcStatus() {
		return plcStatus;
	}
	
	public void setPlcStatus(int plcStatus) {
		this.plcStatus = plcStatus;
	}
	
	@Override
	public String toString() {
		return "PmcsHostConfigDto [plcHost=" + plcHost + ", hostIp=" + hostIp + ", port=" + port + ", felsCode="
				+ felsCode + ", slaveAddress=" + slaveAddress + ", plcStatus=" + plcStatus + "]";
	}
}
