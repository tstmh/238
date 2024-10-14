package com.stee.emas.ctetun.pmcs.service;

import java.util.List;
import com.stee.emas.ctetun.pmcs.dto.PMCSHostConfigDto;

public interface PMCSHostConfigService {
	
	List<PMCSHostConfigDto> findByFelsCode(String felsCode);
	
	PMCSHostConfigDto findByHostId(String hostName);
	
}
