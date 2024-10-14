package com.stee.emas.ctetun.pmcs.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "equip_status", schema = "emastest", catalog = "emas_testdb")
public class PMCSInterfaceStatusEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "equip_id")
	private String equipId;
	
	@Column(name = "status")
	private Integer status;
	
	@Column(name = "date_time")
	private Date updatedDate;
	
	@Column(name = "status_code")
	private String statusCode;

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
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
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
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	public String toString() {
		return "PmcsInterfaceStatusEntity [id=" + id + ", equipId=" + equipId + ", status=" + status + ", updatedDate="
				+ updatedDate + ", statusCode=" + statusCode + "]";
	}
}
