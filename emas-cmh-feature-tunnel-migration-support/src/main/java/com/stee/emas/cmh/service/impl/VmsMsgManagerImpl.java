/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stee.emas.cmh.service.VmsMsgManager;
import com.stee.emas.common.dao.VmsMsgDao;
import com.stee.emas.common.dto.VmsMsgDto;
import com.stee.emas.common.dto.VmsMsgDtoList;
import com.stee.emas.common.entity.VmsMsg;
import com.stee.emas.common.util.MessageConverterUtil;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Manager class for VMS</p>
 * <p>This class is used for VMS related functions
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Dec 13, 2012
 * @version 1.0
 *
 */
@Service("vmsMsgManager")
@Transactional
public class VmsMsgManagerImpl implements VmsMsgManager {
	private static Logger logger = LoggerFactory.getLogger(VmsMsgManagerImpl.class);
	
	@Autowired
	VmsMsgDao vmsMsgDao;
	@Autowired
	MessageConverterUtil messageConverterUtil;

	@Override
	public String processVmsMsgResponse(VmsMsgDto pVmsMsgDto) {
		if (logger.isDebugEnabled()) {
			logger.info("Inside Manager :: processVmsMsgResponse -> Start .....");
		}
		VmsMsg lVmsMsg = messageConverterUtil.convertVmsMsgDtoToEntity(pVmsMsgDto);
		String lId = vmsMsgDao.processVmsMsg(lVmsMsg);
		if (logger.isDebugEnabled()) {
			logger.info("Exit Manager :: processVmsMsgResponse .....");
		}
		return lId;
	}

	@Override
	public void processVmsMsgList(VmsMsgDtoList pVmsMsgDtoList) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager :: processVmsMsgList -> Start .....");
		}
		List<VmsMsgDto> lVmsMsgDtoList = pVmsMsgDtoList.getDtoList();
		for (VmsMsgDto lVmsMsgDto : lVmsMsgDtoList) {
			VmsMsg lVmsMsg = messageConverterUtil.convertVmsMsgDtoToEntity(lVmsMsgDto);
			vmsMsgDao.processVmsMsg(lVmsMsg);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager :: processVmsMsgResponse .....");
		}
	}
}