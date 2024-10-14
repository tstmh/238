package com.stee.emas.ctetun.wmss.dto;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.stereotype.Component;

@Component
public class WmssBufferDto {
	
	Map<String, WmssEquipConfigDto> wmssEquipConfigMap = new HashMap<>();
	
	MultiKeyMap<String, Integer> wmssEquipStatusMap = new MultiKeyMap<String, Integer>();
	
	MultiKeyMap<String, Integer> wmssAlarmCodeMappingMap = new MultiKeyMap<String, Integer>();

	/**
	 * @return the wmssEquipConfigMap
	 */
	public Map<String, WmssEquipConfigDto> getWmssEquipConfigMap() {
		return wmssEquipConfigMap;
	}

	/**
	 * @param wmssEquipConfigMap the wmssEquipConfigMap to set
	 */
	public void setWmssEquipConfigMap(Map<String, WmssEquipConfigDto> wmssEquipConfigMap) {
		this.wmssEquipConfigMap = wmssEquipConfigMap;
	}

	/**
	 * @return the wmssEquipStatusMap
	 */
	public MultiKeyMap<String, Integer> getWmssEquipStatusMap() {
		return wmssEquipStatusMap;
	}

	/**
	 * @param wmssEquipStatusMap the wmssEquipStatusMap to set
	 */
	public void setWmssEquipStatusMap(MultiKeyMap<String, Integer> wmssEquipStatusMap) {
		this.wmssEquipStatusMap = wmssEquipStatusMap;
	}

	/**
	 * @return the wmssAlarmCodeMappingMap
	 */
	public MultiKeyMap<String, Integer> getWmssAlarmCodeMappingMap() {
		return wmssAlarmCodeMappingMap;
	}

	/**
	 * @param wmssAlarmCodeMappingMap the wmssAlarmCodeMappingMap to set
	 */
	public void setWmssAlarmCodeMappingMap(MultiKeyMap<String, Integer> wmssAlarmCodeMappingMap) {
		this.wmssAlarmCodeMappingMap = wmssAlarmCodeMappingMap;
	}

}
