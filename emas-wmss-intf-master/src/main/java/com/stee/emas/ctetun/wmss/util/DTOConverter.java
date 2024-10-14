package com.stee.emas.ctetun.wmss.util;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.stee.emas.ctetun.wmss.dto.WmssAlarmCodeMappingDto;
import com.stee.emas.ctetun.wmss.dto.WmssAttributeStatusDto;
import com.stee.emas.ctetun.wmss.dto.WmssEquipConfigDto;
import com.stee.emas.ctetun.wmss.entity.WmssAlarmCodeMappingEntity;
import com.stee.emas.ctetun.wmss.entity.WmssAttributeStatusEntity;
import com.stee.emas.ctetun.wmss.entity.WmssEquipConfigEntity;

@Component
public class DTOConverter {
	
	@Autowired
    private ModelMapper modelMapper;
	
	public WmssEquipConfigDto convertWmssEquipConfigEntityToDto(WmssEquipConfigEntity wmssEquipConfigEntity) {
		WmssEquipConfigDto wmssEquipConfigDto  = modelMapper.map(wmssEquipConfigEntity, WmssEquipConfigDto.class);
		return  wmssEquipConfigDto;
	}
	
	public WmssAttributeStatusDto convertAttrributeStatusEntityToDto(WmssAttributeStatusEntity wmssAttributeStatusEntity) {
		WmssAttributeStatusDto wmssAttributeStatusDto = modelMapper.map(wmssAttributeStatusEntity, WmssAttributeStatusDto.class);
		return wmssAttributeStatusDto;
	}
	
	public WmssAlarmCodeMappingDto convertAlarmCodeMappingEntityToDto(WmssAlarmCodeMappingEntity wmssAlarmCodeMappingEntity) {
		WmssAlarmCodeMappingDto wmssAlarmCodeMappingDto =  modelMapper.map(wmssAlarmCodeMappingEntity, WmssAlarmCodeMappingDto.class);
		return wmssAlarmCodeMappingDto;	
	}
}
