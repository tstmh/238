package com.stee.emas.ctetun.wmss.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.stee.emas.ctetun.wmss.dao.WmssAlarmCodeMappingRepo;
import com.stee.emas.ctetun.wmss.dto.WmssAlarmCodeMappingDto;
import com.stee.emas.ctetun.wmss.service.WmssAlarmCodeMappingService;
import com.stee.emas.ctetun.wmss.util.DTOConverter;

@Service
public class WmssAlarmCodeMappingServiceImpl implements WmssAlarmCodeMappingService {
	
	@Autowired
	WmssAlarmCodeMappingRepo alarmCodeMappingRepo;
	
	@Autowired
	private DTOConverter dtoConverter;
	
	@Override
	public List<WmssAlarmCodeMappingDto> getAllAlarmCodeMapping() {
	
		List<WmssAlarmCodeMappingDto> wmssAlarmCodeMappingDtoList = new ArrayList<>();
		alarmCodeMappingRepo.findAll().forEach(e -> {
			wmssAlarmCodeMappingDtoList.add(dtoConverter.convertAlarmCodeMappingEntityToDto(e));
		});
		return wmssAlarmCodeMappingDtoList;
	}
}