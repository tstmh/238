package com.stee.emas.ctetun.wmss.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.ctetun.wmss.dto.WmssBufferDto;
import com.stee.emas.ctetun.wmss.service.WmssEquipConfigService;
import com.stee.emas.ctetun.wmss.service.impl.WmssAlarmCodeMappingServiceImpl;
import com.stee.emas.ctetun.wmss.service.impl.WmssAttributeStatusServiceImpl;

@Component
public class WmssInititator {
	
	@Autowired
	private WmssEquipConfigService wmssEquipConfigServiceImpl;
	
	@Autowired
	private WmssAttributeStatusServiceImpl wmssAttributeStatusServiceImpl;
	
	@Autowired
	private WmssBufferDto wmssBufferDto;
	
	@Autowired
	private WmssAlarmCodeMappingServiceImpl wmssAlarmCodeMappingServiceImpl;
	
	/**
	 * Load WMSS EquipConfig to buffer from Database
	 */
	public void initWmssEquipConfig() {
		
		wmssEquipConfigServiceImpl.getAllWmssEuipment(Constants.WMSS_FELS_CODE).forEach(e -> {
			wmssBufferDto.getWmssEquipConfigMap().put(e.getEquipId(), e);
			
		});
	}
	
	/**
	 * Load WMSS AttributeStatusConfig to buffer from Database
	 */
	public void initEquipAttributeStatus() {
		
		wmssAttributeStatusServiceImpl.findAllAttributeStatus().forEach(e -> {
			wmssBufferDto.getWmssEquipStatusMap().put(e.getEquipId(), e.getAttributeCode(),e.getAttributeValue());
		});		
	}
	
	/**
	 * Load WMSS AlarmCodeMapping to buffer from Database
	 */
	public void initAlarmCodeMapping() {
		
		wmssAlarmCodeMappingServiceImpl.getAllAlarmCodeMapping().forEach(e -> {			
			wmssBufferDto.getWmssAlarmCodeMappingMap().put(e.getEquipType(), e.getAttributeCode(), e.getAlarmCode());
		});
	}
}
