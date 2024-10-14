package com.stee.emas.ctetun.pmcs.entity;

import java.io.Serializable;

public class AlarmCodeId implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String felsCode;

    private String equipType;
    
    private String attributeCode;
    

    // default constructor
    
    public AlarmCodeId () {
    	
    }

    public AlarmCodeId(String felsCode, String equipType, String attrCode) {
        this.felsCode = felsCode;
        this.equipType = equipType;
        this.attributeCode = attrCode;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeCode == null) ? 0 : attributeCode.hashCode());
		result = prime * result + ((equipType == null) ? 0 : equipType.hashCode());
		result = prime * result + ((felsCode == null) ? 0 : felsCode.hashCode());
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
		AlarmCodeId other = (AlarmCodeId) obj;
		if (attributeCode == null) {
			if (other.attributeCode != null)
				return false;
		} else if (!attributeCode.equals(other.attributeCode))
			return false;
		if (equipType == null) {
			if (other.equipType != null)
				return false;
		} else if (!equipType.equals(other.equipType))
			return false;
		if (felsCode == null) {
			if (other.felsCode != null)
				return false;
		} else if (!felsCode.equals(other.felsCode))
			return false;
		return true;
	}
}
