package com.stee.emas.ctetun.pmcs.service;

import java.util.List;

import com.stee.emas.ctetun.pmcs.dto.PMCSAttributeStatusDto;

public interface PMCSAttributeStatusService {

	PMCSAttributeStatusDto getAttributeValue(String equipId, String attributeCode);
	
	void updateAttributeValue(String equipId, String attributeCode, Integer value);
	
	List<PMCSAttributeStatusDto> findAllAttributeStatus(String felsCode);
	
}
