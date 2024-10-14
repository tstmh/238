package com.stee.emas.ctetun.pmcs.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.stereotype.Component;
import com.focus_sw.fieldtalk.MbusTcpMasterProtocol;

@Component
public class PMCSBufferDto {
	
	List<PMCSHostConfigDto> pmcsHostConfigDtoList = new ArrayList<>();
	
	List<PMCSWatchDogConfigDto> pmcsWatchDogConfigDtoList = new ArrayList<>();
	
	Map<String, List<PMCSEquipConfigDto>> pmcsEquipConfigMap = new HashMap<>();
	
	MultiKeyMap<String, List<PMCSEquipAttributeDto>> pmcsAttributeConfigMap =new MultiKeyMap<String,  List<PMCSEquipAttributeDto>>();
	
	MultiKeyMap<String, Integer> pmcsEquipStatusMap = new MultiKeyMap<String, Integer>();
	
	MultiKeyMap<String, Integer> pmcsAlarmCodeMappingMap = new MultiKeyMap<String, Integer>();
	
	public ConcurrentHashMap<String, MbusTcpMasterProtocol> connectedPLCMap = new ConcurrentHashMap<>();

	/**
	 * @return the pmcsHostConfigDtoList
	 */
	public List<PMCSHostConfigDto> getPmcsHostConfigDtoList() {
		return pmcsHostConfigDtoList;
	}

	/**
	 * @param pmcsHostConfigDtoList the pmcsHostConfigDtoList to set
	 */
	public void setPmcsHostConfigDtoList(List<PMCSHostConfigDto> pmcsHostConfigDtoList) {
		this.pmcsHostConfigDtoList = pmcsHostConfigDtoList;
	}
	
	/**
	 * @return the pmcsWatchDogConfigDtoList
	 */
	public List<PMCSWatchDogConfigDto> getPmcsWatchDogConfigDtoList() {
		return pmcsWatchDogConfigDtoList;
	}

	/**
	 * @param pmcsWatchDogConfigDtoList the pmcsWatchDogConfigDtoList to set
	 */
	public void setPmcsWatchDogConfigDtoList(List<PMCSWatchDogConfigDto> pmcsWatchDogConfigDtoList) {
		this.pmcsWatchDogConfigDtoList = pmcsWatchDogConfigDtoList;
	}

	/**
	 * @return the pmcsEquipConfigMap
	 */
	public Map<String, List<PMCSEquipConfigDto>> getPmcsEquipConfigMap() {
		return pmcsEquipConfigMap;
	}

	/**
	 * @param pmcsEquipConfigMap the pmcsEquipConfigMap to set
	 */
	public void setPmcsEquipConfigMap(Map<String, List<PMCSEquipConfigDto>> pmcsEquipConfigMap) {
		this.pmcsEquipConfigMap = pmcsEquipConfigMap;
	}

	/**
	 * @return the pmcsAttributeConfigMap
	 */
	public MultiKeyMap<String, List<PMCSEquipAttributeDto>> getPmcsAttributeConfigMap() {
		return pmcsAttributeConfigMap;
	}

	/**
	 * @param pmcsAttributeConfigMap the pmcsAttributeConfigMap to set
	 */
	public void setPmcsAttributeConfigMap(MultiKeyMap<String, List<PMCSEquipAttributeDto>> pmcsAttributeConfigMap) {
		this.pmcsAttributeConfigMap = pmcsAttributeConfigMap;
	}

	/**
	 * @return the pmcsEquipStatusMap
	 */
	public MultiKeyMap<String, Integer> getPmcsEquipStatusMap() {
		return pmcsEquipStatusMap;
	}

	/**
	 * @param pmcsEquipStatusMap the pmcsEquipStatusMap to set
	 */
	public void setPmcsEquipStatusMap(MultiKeyMap<String, Integer> pmcsEquipStatusMap) {
		this.pmcsEquipStatusMap = pmcsEquipStatusMap;
	}

	/**
	 * @return the pmcsAlarmCodeMappingMap
	 */
	public MultiKeyMap<String, Integer> getPmcsAlarmCodeMappingMap() {
		return pmcsAlarmCodeMappingMap;
	}

	/**
	 * @param pmcsAlarmCodeMappingMap the pmcsAlarmCodeMappingMap to set
	 */
	public void setPmcsAlarmCodeMappingMap(MultiKeyMap<String, Integer> pmcsAlarmCodeMappingMap) {
		this.pmcsAlarmCodeMappingMap = pmcsAlarmCodeMappingMap;
	}

	/**
	 * @return the connectedPLCMap
	 */
	public ConcurrentHashMap<String, MbusTcpMasterProtocol> getConnectedPLCMap() {
		return connectedPLCMap;
	}

	/**
	 * @param connectedPLCMap the connectedPLCMap to set
	 */
	public void setConnectedPLCMap(ConcurrentHashMap<String, MbusTcpMasterProtocol> connectedPLCMap) {
		this.connectedPLCMap = connectedPLCMap;
	}

	@Override
	public String toString() {
		return "PMCSBufferDto [pmcsHostConfigDtoList=" + pmcsHostConfigDtoList + ", pmcsWatchDogConfigDtoList="
				+ pmcsWatchDogConfigDtoList + ", pmcsEquipConfigMap=" + pmcsEquipConfigMap + ", pmcsAttributeConfigMap="
				+ pmcsAttributeConfigMap + ", pmcsEquipStatusMap=" + pmcsEquipStatusMap + ", pmcsAlarmCodeMappingMap="
				+ pmcsAlarmCodeMappingMap + ", connectedPLCMap=" + connectedPLCMap + "]";
	}
}
