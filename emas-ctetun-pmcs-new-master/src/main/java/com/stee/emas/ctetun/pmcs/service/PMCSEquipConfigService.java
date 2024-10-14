package com.stee.emas.ctetun.pmcs.service;

import java.util.List;

import com.stee.emas.ctetun.pmcs.dto.PMCSEquipConfigDto;

public interface PMCSEquipConfigService {
	
	List<PMCSEquipConfigDto> getAllEquipment(String felsCode, String hostName);
	
	PMCSEquipConfigDto getAttributeAddress(String equipmentType, String equipmentId, String hostName);
	
	int getMaxReadRegisterByEquipType(String equipType);
}
