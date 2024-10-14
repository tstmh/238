package com.stee.emas.ctetun.pmcs.entity;

import java.io.Serializable;

public class PMCSHostConfigIdClass implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String plcHost;
	private String felsCode;
	
	public PMCSHostConfigIdClass() {
		
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((felsCode == null) ? 0 : felsCode.hashCode());
		result = prime * result + ((plcHost == null) ? 0 : plcHost.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PMCSHostConfigIdClass other = (PMCSHostConfigIdClass) obj;
		if (felsCode == null) {
			if (other.felsCode != null)
				return false;
		} else if (!felsCode.equals(other.felsCode))
			return false;
		if (plcHost == null) {
			if (other.plcHost != null)
				return false;
		} else if (!plcHost.equals(other.plcHost))
			return false;
		return true;
	}
}
