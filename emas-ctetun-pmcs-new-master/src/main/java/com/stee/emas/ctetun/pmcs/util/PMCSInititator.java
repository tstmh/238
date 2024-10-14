package com.stee.emas.ctetun.pmcs.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stee.emas.common.constants.Constants;
import com.stee.emas.ctetun.pmcs.dto.PMCSBufferDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSEquipAttributeDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSEquipConfigDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSHostConfigDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSWatchDogConfigDto;
import com.stee.emas.ctetun.pmcs.service.PMCSEquipConfigService;
import com.stee.emas.ctetun.pmcs.service.impl.PMCSAlarmCodeMappingServiceImpl;
import com.stee.emas.ctetun.pmcs.service.impl.PMCSAttributeStatusServiceImpl;
import com.stee.emas.ctetun.pmcs.service.impl.PMCSEquipAttributeServiceImpl;
import com.stee.emas.ctetun.pmcs.service.impl.PMCSHostConfigServiceImpl;


@Component
public class PMCSInititator {
	
	@Autowired
	private PMCSAttributeStatusServiceImpl pmcsAttributeStatusServiceImpl;
	
	@Autowired
    private PMCSHostConfigServiceImpl pmcsHostConfigServiceImpl;
	
	@Autowired
	private PMCSEquipConfigService pmcsEquipConfigServiceImpl;
	
	@Autowired
	private PMCSEquipAttributeServiceImpl pmcsEquipAttributeServiceImpl;
	
	@Autowired
	private PMCSBufferDto pmcsBufferDto;
	
	@Autowired
	private PMCSAlarmCodeMappingServiceImpl pmcsAlarmCodeMappingServiceImpl;
	
	/**
	 * Load PMCS HostConfig to buffer from Database
	 */
	public void initHostConfig() {
		List<PMCSHostConfigDto> pmcsHostConfigDtoList = pmcsHostConfigServiceImpl.findByFelsCode(Constants.PMCS_FELS_CODE);
		pmcsBufferDto.getPmcsHostConfigDtoList().addAll(pmcsHostConfigDtoList);			
	}	
	
	/**
	 * Load PMCS EquipConfig to buffer from Database
	 */
	public void initEquipmentConfig() {
		pmcsBufferDto.getPmcsHostConfigDtoList().forEach(e -> {
			List<PMCSEquipConfigDto> pmcsEquipConfigDtoList = pmcsEquipConfigServiceImpl.getAllEquipment(e.getFelsCode(), e.getPlcHost());
			pmcsBufferDto.getPmcsEquipConfigMap().put(e.getPlcHost(), pmcsEquipConfigDtoList);
		});
	}
	
	/**
	 * Load PMCS EqipmentAttributeConfig to buffer from Database
	 */
	public void initEquipAttributeConfig() {
		
		pmcsEquipAttributeServiceImpl.getEquipTypeList(Constants.PMCS_FELS_CODE).forEach(e -> {
			int maxReadRegister = pmcsEquipConfigServiceImpl.getMaxReadRegisterByEquipType(e);
			for(int registerPosition = 1;registerPosition <= maxReadRegister;registerPosition++) {	
				List<PMCSEquipAttributeDto> pmcsEquipAttributeDtoList = pmcsEquipAttributeServiceImpl.getAllEquipmentAttribute(e,registerPosition);				
				pmcsBufferDto.getPmcsAttributeConfigMap().put(e, String.valueOf(registerPosition), pmcsEquipAttributeDtoList);				
			}	
		});	
	}	
	
	/**
	 * Load PMCS AttributeStatusConfig to buffer from Database
	 */
	public void initEquipAttributeStatus() {
		
		pmcsAttributeStatusServiceImpl.findAllAttributeStatus(Constants.PMCS_FELS_CODE).forEach(e -> {
			pmcsBufferDto.getPmcsEquipStatusMap().put(e.getEquipId(), e.getAttributeCode(),e.getAttributeValue());
		});		
	}
	
	/**
	 * Load PMCS AlarmCodeMapping to buffer from Database
	 */
	public void initAlarmCodeMapping() {
		
		pmcsAlarmCodeMappingServiceImpl.getAllAlarmCodeMapping(Constants.PMCS_FELS_CODE).forEach(e -> {			
			pmcsBufferDto.getPmcsAlarmCodeMappingMap().put(e.getEquipType(), e.getAttributeCode(), e.getAlarmCode());
		});
	}
	
	/**
	 * Load PMCS WatchDogStatus to buffer
	 */
	public void initWatchDogEquipmentConfig() {
		
		pmcsBufferDto.getPmcsHostConfigDtoList().forEach(e ->{
			PMCSWatchDogConfigDto pmcsWatchDogConfigDto = new PMCSWatchDogConfigDto(e.getPlcHost());
			pmcsBufferDto.getPmcsWatchDogConfigDtoList().add(pmcsWatchDogConfigDto);
		});
	}
}
