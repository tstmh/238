package com.stee.emas.ctetun.wmss.dto;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class WmssEquipStatusData{
	
	@SerializedName("results")
	List<WmssEquipStatusDto> equipStatus;

	/**
	 * @return the equipStatus
	 */
	public List<WmssEquipStatusDto> getEquipStatus() {
		return equipStatus;
	}

	/**
	 * @param equipStatus the equipStatus to set
	 */
	public void setEquipStatus(List<WmssEquipStatusDto> equipStatus) {
		this.equipStatus = equipStatus;
	}
}
