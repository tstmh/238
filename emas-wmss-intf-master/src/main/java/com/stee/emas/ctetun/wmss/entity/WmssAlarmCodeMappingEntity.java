package com.stee.emas.ctetun.wmss.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "alarm_code_mapping", schema = "emastun")
@IdClass(AlarmCodeId.class)
public class WmssAlarmCodeMappingEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "equip_type")
	private String  equipType;

	@Id
	@Column(name = "attr_code")
	private String  attributeCode;
	
	@Id
	@Column(name = "fels_code")
	private String felsCode;
	
	@Column(name = "alarm_code")
	private Integer alarmCode;
	
	@Column(name = "comments")
	private String comments;

	public String getEquipType() {
		return equipType;
	}

	public void setEquipType(String equipType) {
		this.equipType = equipType;
	}

	public String getAttributeCode() {
		return attributeCode;
	}

	public void setAttributeCode(String attributeCode) {
		this.attributeCode = attributeCode;
	}

	public String getFelsCode() {
		return felsCode;
	}

	public void setFelsCode(String felsCode) {
		this.felsCode = felsCode;
	}

	public Integer getAlarmCode() {
		return alarmCode;
	}

	public void setAlarmCode(Integer alarmCode) {
		this.alarmCode = alarmCode;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "WmssAlarmCodeMappingEntity [equipType=" + equipType + ", attributeCode=" + attributeCode + ", felsCode="
				+ felsCode + ", alarmCode=" + alarmCode + ", comments=" + comments + "]";
	}
}
