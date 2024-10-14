package com.stee.emas.ctetun.pmcs.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.stee.emas.ctetun.pmcs.dao.PMCSEquipConfigRepo;
import com.stee.emas.ctetun.pmcs.dto.PMCSEquipConfigDto;
import com.stee.emas.ctetun.pmcs.service.PMCSEquipConfigService;
import com.stee.emas.ctetun.pmcs.util.DTOConverter;

@Service
public class PMCSEquipConfigServiceImpl implements PMCSEquipConfigService {
	
	@Autowired
	private PMCSEquipConfigRepo pmcsEquipConfigRepo;
	
	@Autowired
	DTOConverter dtoConverter;
	
	@Override
	public List<PMCSEquipConfigDto> getAllEquipment(String felsCode, String hostName) {

		List<PMCSEquipConfigDto> pmcsConfigDtoList = new ArrayList<>();
		pmcsEquipConfigRepo.getAllEquipment(felsCode, hostName).forEach(e -> {		
			pmcsConfigDtoList.add(dtoConverter.convertPmcsEquipConfigEntityToDto(e));
		});		
		return  pmcsConfigDtoList;
	}
	
	@Override
	public PMCSEquipConfigDto getAttributeAddress(String equipmentType, String equipmentId, String hostName) {
		return dtoConverter.convertPmcsEquipConfigEntityToDto(pmcsEquipConfigRepo.getAttributeAddress(equipmentType, equipmentId, hostName));
	}
	
	@Override
	public int getMaxReadRegisterByEquipType(String equipType) {
		int maxReadRegister = pmcsEquipConfigRepo.getMaxReadRegisterByEquipType(equipType);
		return maxReadRegister;
	}
}
