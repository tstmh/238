package com.stee.emas.ctetun.pmcs.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.stee.emas.ctetun.pmcs.dao.PMCSEquipAttributeRepo;
import com.stee.emas.ctetun.pmcs.dto.PMCSEquipAttributeDto;
import com.stee.emas.ctetun.pmcs.service.PMCSEquipAttributeService;
import com.stee.emas.ctetun.pmcs.util.DTOConverter;

@Service
public class PMCSEquipAttributeServiceImpl implements PMCSEquipAttributeService {
	
	@Autowired
	private PMCSEquipAttributeRepo pmcsEquipAttributeRepo;
	
	@Autowired
	DTOConverter dtoConverter;
	
	@Override
	public List<PMCSEquipAttributeDto> getAllEquipmentAttribute(String equipmentCode, Integer registerPosition) {
		
		List<PMCSEquipAttributeDto> pmcsEquipAttributeDtoList =  new ArrayList<>();
		pmcsEquipAttributeRepo.getAllEquipmentAttribute(equipmentCode, registerPosition).forEach(e -> {
			pmcsEquipAttributeDtoList.add(dtoConverter.convertPmcsEquipAttributeEntityToDto(e));
		});
		return pmcsEquipAttributeDtoList;
	}
		
	@Override
	public List<String> getEquipTypeList(String felsCode) {
		return pmcsEquipAttributeRepo.getEquipTypeList(felsCode);
	}
}

	