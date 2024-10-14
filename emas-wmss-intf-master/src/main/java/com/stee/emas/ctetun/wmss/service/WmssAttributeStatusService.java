package com.stee.emas.ctetun.wmss.service;

import java.util.List;
import com.stee.emas.ctetun.wmss.dto.WmssAttributeStatusDto;

public interface WmssAttributeStatusService {

	void updateEquipAttributeValue(String equipId, String attributeCode, Integer value);
	
	List<WmssAttributeStatusDto> findAllAttributeStatus();
	
}
