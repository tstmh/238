package com.stee.emas.ctetun.pmcs.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.stee.emas.common.constants.Constants;
import com.stee.emas.ctetun.pmcs.dao.PMCSAttributeStatusRepo;
import com.stee.emas.ctetun.pmcs.dto.PMCSAttributeStatusDto;
import com.stee.emas.ctetun.pmcs.entity.PMCSAttributeStatusEntity;
import com.stee.emas.ctetun.pmcs.service.PMCSAttributeStatusService;
import com.stee.emas.ctetun.pmcs.util.DTOConverter;

@Service
public class PMCSAttributeStatusServiceImpl implements PMCSAttributeStatusService {
	
	final static Logger logger = LoggerFactory.getLogger(PMCSAttributeStatusServiceImpl.class);
	
	@Autowired
	private PMCSAttributeStatusRepo attributeStatusRepo;
	
	@Autowired
	private DTOConverter dtoConverter;
	
	@Override
    public PMCSAttributeStatusDto getAttributeValue(String equipId, String attributeCode) {		
		return dtoConverter.convertAttrributeStatusEntityToDto(attributeStatusRepo.getAttributeValue(equipId, attributeCode));		 
	}
	
	@Transactional
	@Modifying
	@Override
     public void updateAttributeValue(String equipId, String attributeCode, Integer value){
		
		PMCSAttributeStatusEntity lAttributeStatusEntity = attributeStatusRepo.getAttributeValue(equipId, attributeCode);
		if (lAttributeStatusEntity == null) {
			lAttributeStatusEntity = new PMCSAttributeStatusEntity();;
			lAttributeStatusEntity.setEquipId(equipId);
			lAttributeStatusEntity.setAttributeCode(attributeCode);
			lAttributeStatusEntity.setCreatedDate(new Date());
			lAttributeStatusEntity.setFelsCode(Constants.PMCS_FELS_CODE);
		}		
		lAttributeStatusEntity.setAttributeValue(value);
		lAttributeStatusEntity.setUpdatedDate(new Date());
		attributeStatusRepo.save(lAttributeStatusEntity);
	}
		
	 @Override
	 public List<PMCSAttributeStatusDto> findAllAttributeStatus(String felsCode){
		 		
		 List<PMCSAttributeStatusDto> PmcsAttributeStatusDtoList = new ArrayList<>();
		 attributeStatusRepo.findByFelsCode(felsCode).forEach(e -> {
			 PmcsAttributeStatusDtoList.add(dtoConverter.convertAttrributeStatusEntityToDto(e));
			});				
		 return PmcsAttributeStatusDtoList;
	 }
}
