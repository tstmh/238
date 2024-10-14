package com.stee.emas.ctetun.pmcs.service.impl;

import java.util.Date;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.stee.emas.ctetun.pmcs.dao.PMCSInterfaceStatusRepo;
import com.stee.emas.ctetun.pmcs.entity.PMCSInterfaceStatusEntity;
import com.stee.emas.ctetun.pmcs.service.PMCSInterfaceStatusService;

@Service
public class PMCSInterfaceStatusServiceImpl implements PMCSInterfaceStatusService {
	
	@Autowired
	PMCSInterfaceStatusRepo interfaceStatusRepo;
	
	@Transactional
	@Modifying
	@Override
	public void updateInterfaceStatus(String equipId, int status) {		
		
		PMCSInterfaceStatusEntity lInterfaceStatusEntity = interfaceStatusRepo.getInterfaceStatus(equipId);
		if (lInterfaceStatusEntity == null) {
			lInterfaceStatusEntity = new PMCSInterfaceStatusEntity();
			lInterfaceStatusEntity.setEquipId(equipId);
			lInterfaceStatusEntity.setStatus(2);
			lInterfaceStatusEntity.setStatusCode("opestate");
			lInterfaceStatusEntity.setUpdatedDate(new Date());
		}
		lInterfaceStatusEntity.setStatus(status);
		lInterfaceStatusEntity.setUpdatedDate(new Date());
		interfaceStatusRepo.save(lInterfaceStatusEntity);
	}

}
