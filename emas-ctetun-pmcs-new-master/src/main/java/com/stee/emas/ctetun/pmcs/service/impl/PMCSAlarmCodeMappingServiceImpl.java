package com.stee.emas.ctetun.pmcs.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stee.emas.ctetun.pmcs.dao.PMCSAlarmCodeMappingRepo;
import com.stee.emas.ctetun.pmcs.dto.PMCSAlarmCodeMappingDto;
import com.stee.emas.ctetun.pmcs.service.PMCSAlarmCodeMappingService;
import com.stee.emas.ctetun.pmcs.util.DTOConverter;

@Service
public class PMCSAlarmCodeMappingServiceImpl implements PMCSAlarmCodeMappingService {
	
	@Autowired
	PMCSAlarmCodeMappingRepo alarmCodeMappingRepo;
	
	@Autowired
	private DTOConverter dtoConverter;
	
	@Override
	public List<PMCSAlarmCodeMappingDto> getAllAlarmCodeMapping(String felsCode) {
	
		List<PMCSAlarmCodeMappingDto> pmcsAlarmCodeMappingDtoList = new ArrayList<>();
		alarmCodeMappingRepo.findByFelsCode(felsCode).forEach(e -> {
			pmcsAlarmCodeMappingDtoList.add(dtoConverter.convertAlarmCodeMappingEntityToDto(e));
		});
		return pmcsAlarmCodeMappingDtoList;
	}
}