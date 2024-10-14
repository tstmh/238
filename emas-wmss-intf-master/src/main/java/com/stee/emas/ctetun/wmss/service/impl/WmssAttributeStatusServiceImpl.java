package com.stee.emas.ctetun.wmss.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import com.stee.emas.ctetun.wmss.dao.WmssAttributeStatusRepo;
import com.stee.emas.ctetun.wmss.dto.WmssAttributeStatusDto;
import com.stee.emas.ctetun.wmss.entity.WmssAttributeStatusEntity;
import com.stee.emas.ctetun.wmss.service.WmssAttributeStatusService;
import com.stee.emas.ctetun.wmss.util.DTOConverter;

@Service
public class WmssAttributeStatusServiceImpl implements WmssAttributeStatusService {
	
	final static Logger logger = LoggerFactory.getLogger(WmssAttributeStatusServiceImpl.class);
	
	@Autowired
	private WmssAttributeStatusRepo attributeStatusRepo;
	
	@Autowired
	private DTOConverter dtoConverter;
	
	@Transactional
	@Modifying
	@Override
     public void updateEquipAttributeValue(String equipId, String attributeCode, Integer value){
		
		WmssAttributeStatusEntity lAttributeStatusEntity = attributeStatusRepo.getAttributeValue(equipId, attributeCode);
		if (lAttributeStatusEntity == null) {
			lAttributeStatusEntity = new WmssAttributeStatusEntity();;
			lAttributeStatusEntity.setEquipId(equipId);
			lAttributeStatusEntity.setAttributeCode(attributeCode);
			lAttributeStatusEntity.setCreatedDate(new Date());
		}		
		lAttributeStatusEntity.setAttributeValue(value);
		lAttributeStatusEntity.setUpdatedDate(new Date());
		attributeStatusRepo.save(lAttributeStatusEntity);
	}
		
	 @Override
	 public List<WmssAttributeStatusDto> findAllAttributeStatus(){
		 		
		 List<WmssAttributeStatusDto> wmcsAttributeStatusDtoList = new ArrayList<>();
		 attributeStatusRepo.findAll().forEach(e -> {
			 wmcsAttributeStatusDtoList.add(dtoConverter.convertAttrributeStatusEntityToDto(e));
			});				
		 return wmcsAttributeStatusDtoList;
	 }
}
