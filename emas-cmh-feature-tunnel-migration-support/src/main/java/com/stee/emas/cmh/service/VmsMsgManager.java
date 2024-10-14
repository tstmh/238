/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.service;

import com.stee.emas.common.dto.VmsMsgDto;
import com.stee.emas.common.dto.VmsMsgDtoList;

/**
 * 
 * @author Scindia
 * @since Dec 13, 2012
 * @version 1.0
 *
 */

public interface VmsMsgManager {
	
	String processVmsMsgResponse(VmsMsgDto pVmsMsgDto);
	
	void processVmsMsgList(VmsMsgDtoList pVmsMsgDtoList);

}