package com.stee.emas.ctetun.pmcs.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.stee.emas.ctetun.pmcs.dao.PMCSHostConfigRepo;
import com.stee.emas.ctetun.pmcs.dto.PMCSHostConfigDto;
import com.stee.emas.ctetun.pmcs.service.PMCSHostConfigService;
import com.stee.emas.ctetun.pmcs.util.DTOConverter;

@Service
public class PMCSHostConfigServiceImpl implements PMCSHostConfigService {
	
	@Autowired
	private PMCSHostConfigRepo pmcsHostConfigRepo;
	@Autowired
	DTOConverter dtoConverter;
	
	@Override
	public List<PMCSHostConfigDto> findByFelsCode(String felsCode) {
		
		List<PMCSHostConfigDto> pmcsHostConfigDtoList = new ArrayList<>();
		pmcsHostConfigRepo.findByFelsCode(felsCode).forEach(e -> {			
			pmcsHostConfigDtoList.add(dtoConverter.convertPmcsHostConfigEntityToDto(e));
		});		
		return pmcsHostConfigDtoList;		
	}
	
	@Override
	public PMCSHostConfigDto findByHostId(String hostName) {		
		return dtoConverter.convertPmcsHostConfigEntityToDto(pmcsHostConfigRepo.findByHostId(hostName));		
	}
	
}
