package com.stee.emas.ctetun.pmcs.entity;

import java.io.Serializable;

public class PMCSAttributeStatusIdClass implements Serializable {

	private static final long serialVersionUID = 1L;

	private String equipId;
	private String attributeCode;

	public PMCSAttributeStatusIdClass() {

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
	 * @return the attributeCode
	 */
	public String getAttributeCode() {
		return attributeCode;
	}

	/**
	 * @param attributeCode the attributeCode to set
	 */
	public void setAttributeCode(String attributeCode) {
		this.attributeCode = attributeCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PMCSAttributeStatusIdClass other = (PMCSAttributeStatusIdClass) obj;
		if (attributeCode == null) {
			if (other.attributeCode != null)
				return false;
		} else if (!attributeCode.equals(other.attributeCode))
			return false;
		if (equipId == null) {
			if (other.equipId != null)
				return false;
		} else if (!equipId.equals(other.equipId))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeCode == null) ? 0 : attributeCode.hashCode());
		result = prime * result + ((equipId == null) ? 0 : equipId.hashCode());
		return result;
	}
}
