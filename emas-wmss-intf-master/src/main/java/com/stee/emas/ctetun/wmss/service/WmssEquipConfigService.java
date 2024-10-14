package com.stee.emas.ctetun.wmss.service;

import java.util.List;
import com.stee.emas.ctetun.wmss.dto.WmssEquipConfigDto;

public interface WmssEquipConfigService {
	
	List<WmssEquipConfigDto> getAllWmssEuipment(String felsCode);

}
