/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.common;

/**
 * 
 * @author Scindia
 * @since Dec 4, 2012
 * @version 1.0
 *
 */

public class CMHKeyBean {
	
	private String execId;
	private String cmdId;
	
	public CMHKeyBean(String execId, String cmdId) {
		this.execId = execId;
		this.cmdId = cmdId;
	}
	/**
	 * @return the execId
	 */
	public String getExecId() {
		return execId;
	}
	/**
	 * @param execId the execId to set
	 */
	public void setExecId(String execId) {
		this.execId = execId;
	}
	/**
	 * @return the cmdId
	 */
	public String getCmdId() {
		return cmdId;
	}
	/**
	 * @param cmdId the cmdId to set
	 */
	public void setCmdId(String cmdId) {
		this.cmdId = cmdId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CMHKeyBean [execId=" + execId + ", cmdId=" + cmdId + "]";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cmdId == null) ? 0 : cmdId.hashCode());
		result = prime * result + ((execId == null) ? 0 : execId.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CMHKeyBean other = (CMHKeyBean) obj;
		if (cmdId == null) {
			if (other.cmdId != null)
				return false;
		} else if (!cmdId.equals(other.cmdId))
			return false;
		if (execId == null) {
			if (other.execId != null)
				return false;
		} else if (!execId.equals(other.execId))
			return false;
		return true;
	}
	
}