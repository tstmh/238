package com.stee.emas.ctetun.pmcs.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "equip_attr_status", schema = "emastun")
@IdClass(com.stee.emas.ctetun.pmcs.entity.PMCSAttributeStatusIdClass.class)
public class PMCSAttributeStatusEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "equip_id")
	private String equipId;
	
	@Id
	@Column(name = "attr_code")
	private String attributeCode;
	
	@Column(name = "attr_value")
	private Integer attributeValue;
	
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "updated_date")
	private Date updatedDate;
	
	@Column(name = "fels_code")
	private String felsCode;
	
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

	/**
	 * @return the attributeValue
	 */
	public Integer getAttributeValue() {
		return attributeValue;
	}

	/**
	 * @param attributeValue the attributeValue to set
	 */
	public void setAttributeValue(Integer attributeValue) {
		this.attributeValue = attributeValue;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the updatedDate
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}

	/**
	 * @param updatedDate the updatedDate to set
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
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
	public String toString() {
		return "PMCSAttributeStatusEntity [equipId=" + equipId + ", attributeCode=" + attributeCode
				+ ", attributeValue=" + attributeValue + ", createdDate=" + createdDate + ", updatedDate=" + updatedDate
				+ ", felsCode=" + felsCode + "]";
	}
}
