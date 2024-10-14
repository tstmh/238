package com.stee.emas.ctetun.wmss.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.stee.emas.ctetun.wmss.dao.WmssEquipConfigRepo;
import com.stee.emas.ctetun.wmss.dto.WmssEquipConfigDto;
import com.stee.emas.ctetun.wmss.service.WmssEquipConfigService;
import com.stee.emas.ctetun.wmss.util.DTOConverter;

@Service
public class WmssEquipConfigServiceImpl implements WmssEquipConfigService {
	
	@Autowired
	private WmssEquipConfigRepo wmssEquipConfigRepo;
	
	@Autowired
	DTOConverter dtoConverter;
	
	@Override
	public List<WmssEquipConfigDto> getAllWmssEuipment(String felsCode) {

		List<WmssEquipConfigDto> wmssEquipConfigDtoList = new ArrayList<>();
		wmssEquipConfigRepo.getAllWmssEquipment(felsCode).forEach(e -> {		
			wmssEquipConfigDtoList.add(dtoConverter.convertWmssEquipConfigEntityToDto(e));
		});		
		return  wmssEquipConfigDtoList;
	}
}
