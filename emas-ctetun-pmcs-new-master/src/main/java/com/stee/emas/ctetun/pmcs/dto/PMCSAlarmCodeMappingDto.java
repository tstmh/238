package com.stee.emas.ctetun.pmcs.dto;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class PMCSAlarmCodeMappingDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String  equipType;
	private String  attributeCode;
	private String  felsCode;
	private Integer alarmCode;
	private String  comments;
	
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
	 * @return the alarmCode
	 */
	public Integer getAlarmCode() {
		return alarmCode;
	}
	/**
	 * @param alarmCode the alarmCode to set
	 */
	public void setAlarmCode(Integer alarmCode) {
		this.alarmCode = alarmCode;
	}
	
	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	@Override
	public String toString() {
		return "PmcsAlarmCodeMappingDto [equipType=" + equipType + ", attributeCode=" + attributeCode + ", felsCode="
				+ felsCode + ", alarmCode=" + alarmCode + ", comments=" + comments + "]";
	}
}
