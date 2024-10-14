package com.stee.emas.ctetun.pmcs.service;

import java.util.List;

import com.stee.emas.ctetun.pmcs.dto.PMCSAlarmCodeMappingDto;

public interface PMCSAlarmCodeMappingService {

	List<PMCSAlarmCodeMappingDto> getAllAlarmCodeMapping(String felsCode);
}
