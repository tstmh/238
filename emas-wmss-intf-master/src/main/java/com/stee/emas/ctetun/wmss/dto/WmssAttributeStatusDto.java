package com.stee.emas.ctetun.wmss.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class WmssAttributeStatusDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String equipId;
	private String attributeCode;
	private Integer attributeValue;
	private Date updatedDate;
	
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
	
	@Override
	public String toString() {
		return "WmcsAttributeStatusDto [equipId=" + equipId + ", attributeCode=" + attributeCode + ", attributeValue="
				+ attributeValue + ", updatedDate=" + updatedDate + "]";
	}
}
