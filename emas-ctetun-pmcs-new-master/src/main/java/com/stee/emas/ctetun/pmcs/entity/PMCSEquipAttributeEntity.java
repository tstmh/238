package com.stee.emas.ctetun.pmcs.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "equip_attr_config", schema = "emastun")
public class PMCSEquipAttributeEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "equip_type")
	private String equipType;

	@Column(name = "attr_code")
	private String attributeCode;

	@Column(name = "bit_start")
	private Integer bitStart;

	@Column(name = "bit_length")
	private Integer bitLength;
	
	@Column(name = "fels_code")
	private String 	felsCode;
	
	@Column(name = "register_position")
	private Integer registerPosition;

	@Column(name = "attr_type_id")
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
		return "PmcsEquipAttributeEntity [id=" + id + ", equipType=" + equipType + ", attributeCode=" + attributeCode
				+ ", bitStart=" + bitStart + ", bitLength=" + bitLength + ", felsCode=" + felsCode
				+ ", registerPosition=" + registerPosition + ", attrTypeId=" + attrTypeId + "]";

	}
}
