/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.ws;

import com.stee.emas.cmh.dto.lus.LusRemoteControlDto;
import com.stee.emas.cmh.dto.lus.LusSetupDimmingDto;
import com.stee.emas.cmh.dto.lus.LusSetupFlashRateDto;
import com.stee.emas.cmh.dto.wmcs.WmcsRemoteControlDto;
import com.stee.emas.ctetun.dto.LusMsgDto;
import com.stee.emas.ctetun.dto.PmcsMsgDto;

/*
 * @author Scindia
 * @since Dec 4, 2015
 * @version 1.0
 */

public interface CTETunWebService {

	int AW_CFELS_LUS(LusMsgDto pLusStatusDto);	

	int AW_CFELS_PMCS(PmcsMsgDto pPmcsMsgDto);	

	int TEST_LUS_CMD(String pFelsCode, String pEquipId, String pAttrName, String pAttrValue);
	
	int TEST_PMCS_CMD(String pFelsCode, String pEquipType, String pEquipId, String pAttrName, String pAttrValue);

	int waterMistControl(WmcsRemoteControlDto remoteControlDto);
	
	int lusRemoteControl(LusRemoteControlDto remoteControlDto);

	int lusSetupDimming(LusSetupDimmingDto lusSetupDimmingDto);

	int lusSetupFlashRate(LusSetupFlashRateDto lusSetupFlashRateDto);
}

