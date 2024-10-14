package com.stee.emas.ctetun.pmcs.service;

import java.util.List;
import com.stee.emas.ctetun.pmcs.dto.PMCSEquipAttributeDto;

public interface PMCSEquipAttributeService {
	
	List<PMCSEquipAttributeDto> getAllEquipmentAttribute(String equipmentCode, Integer registerPosition);
	
	List<String> getEquipTypeList(String felsCode);
	
}
