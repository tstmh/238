package com.stee.emas.ctetun.pmcs.dto;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class PMCSEquipAttributeDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String  equipType;
	private String  attributeCode;
	private Integer bitStart;
	private Integer bitLength;
	private String  felsCode;
	private Integer registerPosition;
	private Integer attrTypeId;
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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
	/**
	 * @return the bitStart
	 */
	public Integer getBitStart() {
		return bitStart;
	}
	/**
	 * @param bitStart the bitStart to set
	 */
	public void setBitStart(Integer bitStart) {
		this.bitStart = bitStart;
	}
	/**
	 * @return the bitLength
	 */
	public Integer getBitLength() {
		return bitLength;
	}
	/**
	 * @param bitLength the bitLength to set
	 */
	public void setBitLength(Integer bitLength) {
		this.bitLength = bitLength;
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
	 * @return the registerPosition
	 */
	public Integer getRegisterPosition() {
		return registerPosition;
	}
	/**
	 * @param registerPosition the registerPosition to set
	 */
	public void setRegisterPosition(Integer registerPosition) {
		this.registerPosition = registerPosition;
	}
	/**
	 * @return the attrTypeId
	 */
	public Integer getAttrTypeId() {
		return attrTypeId;
	}
	/**
	 * @param attrTypeId the attrTypeId to set
	 */
	public void setAttrTypeId(Integer attrTypeId) {
		this.attrTypeId = attrTypeId;
	}
	
	@Override
	public String toString() {
		return "PmcsEquipAttributeDto [id=" + id + ", equipType=" + equipType + ", attributeCode=" + attributeCode
				+ ", bitStart=" + bitStart + ", bitLength=" + bitLength + ", felsCode=" + felsCode
				+ ", registerPosition=" + registerPosition + ", attrTypeId=" + attrTypeId + "]";
	}
}
