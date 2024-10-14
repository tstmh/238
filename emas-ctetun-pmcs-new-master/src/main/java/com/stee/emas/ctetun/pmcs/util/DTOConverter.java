package com.stee.emas.ctetun.pmcs.util;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stee.emas.ctetun.pmcs.dto.PMCSAlarmCodeMappingDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSAttributeStatusDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSEquipAttributeDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSEquipConfigDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSHostConfigDto;
import com.stee.emas.ctetun.pmcs.entity.PMCSAlarmCodeMappingEntity;
import com.stee.emas.ctetun.pmcs.entity.PMCSAttributeStatusEntity;
import com.stee.emas.ctetun.pmcs.entity.PMCSEquipAttributeEntity;
import com.stee.emas.ctetun.pmcs.entity.PMCSEquipConfigEntity;
import com.stee.emas.ctetun.pmcs.entity.PMCSHostConfigEntity;

@Component
public class DTOConverter {
	
	@Autowired
    private ModelMapper modelMapper;
	
	public PMCSHostConfigDto convertPmcsHostConfigEntityToDto(PMCSHostConfigEntity pmcsHostConfigEntity) {
		PMCSHostConfigDto pmcsHostConfigDto = modelMapper.map(pmcsHostConfigEntity, PMCSHostConfigDto.class);
		return pmcsHostConfigDto;
	}
	
	public PMCSEquipConfigDto convertPmcsEquipConfigEntityToDto(PMCSEquipConfigEntity pmcsEquipConfigEntity) {
		PMCSEquipConfigDto pmcsEquipConfigDto  = modelMapper.map(pmcsEquipConfigEntity, PMCSEquipConfigDto.class);
		return  pmcsEquipConfigDto;
	}
	
	public PMCSEquipAttributeDto convertPmcsEquipAttributeEntityToDto(PMCSEquipAttributeEntity pmcsEquipAttributeEntity) {
		PMCSEquipAttributeDto pmcsEquipAttributeDto =  modelMapper.map(pmcsEquipAttributeEntity, PMCSEquipAttributeDto.class);
		return pmcsEquipAttributeDto;
	}
	
	public PMCSAttributeStatusDto convertAttrributeStatusEntityToDto(PMCSAttributeStatusEntity pmcsAttributeStatusEntity) {
		PMCSAttributeStatusDto pmcsAttributeStatusDto = modelMapper.map(pmcsAttributeStatusEntity, PMCSAttributeStatusDto.class);
		return pmcsAttributeStatusDto;
	}
	
	public PMCSAlarmCodeMappingDto convertAlarmCodeMappingEntityToDto(PMCSAlarmCodeMappingEntity pmcsAlarmCodeMappingEntity) {
		PMCSAlarmCodeMappingDto pmcsAlarmCodeMappingDto =  modelMapper.map(pmcsAlarmCodeMappingEntity, PMCSAlarmCodeMappingDto.class);
		return pmcsAlarmCodeMappingDto;	
	}
}
