/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.caid;


/**
 * 
 * @author Scindia
 * @since Jun 2, 2015
 * @version 1.0
 *
 */

public class CaidKeyBean implements Comparable<CaidKeyBean> {
	
	private String equipId;
	private int laneId;
	
	public CaidKeyBean(String equipId, int laneId) {
		this.equipId = equipId;
		this.laneId = laneId;
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
	 * @return the laneId
	 */
	public int getLaneId() {
		return laneId;
	}
	/**
	 * @param laneId the laneId to set
	 */
	public void setLaneId(int laneId) {
		this.laneId = laneId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CaidKeyBean [equipId=" + equipId + ", laneId=" + laneId + "]";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((equipId == null) ? 0 : equipId.hashCode());
		result = prime * result + laneId;
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
		CaidKeyBean other = (CaidKeyBean) obj;
		if (equipId == null) {
			if (other.equipId != null)
				return false;
		} else if (!equipId.equals(other.equipId))
			return false;
		if (laneId != other.laneId)
			return false;
		return true;
	}
	
	@Override
	public int compareTo(CaidKeyBean pCaidKeyBean) {
		int i = equipId.compareTo(pCaidKeyBean.getEquipId());
		if (i != 0) {
			return i;
		}
		i = Integer.compare(laneId, pCaidKeyBean.getLaneId());		
		return i;
	}
}