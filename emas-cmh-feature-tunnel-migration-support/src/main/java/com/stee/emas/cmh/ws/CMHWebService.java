/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.ws;

import com.stee.emas.common.dto.DimmingDto;
import com.stee.emas.common.dto.FanOpeModeDto;
import com.stee.emas.common.dto.FlashingTimeDto;
import com.stee.emas.common.dto.OpeFlagDto;
import com.stee.emas.common.dto.ResetDto;
import com.stee.emas.common.dto.TechAlarmAckDtoList;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TrafficAlertAckDtoList;
import com.stee.emas.common.dto.VmsMsgDto;
import com.stee.emas.common.dto.VmsPictogramConfigDto;
import com.stee.emas.common.dto.VmsTimetableConfigDto;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : WebService interface for AW </p>
 * <p>This interface is used by AW to send message to other interfaces
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Dec 3, 2012
 * @version 1.0
 *
 */

public interface CMHWebService {
	
	int AW_CFELS_PictogramSet(VmsPictogramConfigDto pVmsPictogramConfigDto);
	
	int AW_CFELS_VMSTimetable(VmsTimetableConfigDto pVmsTimetableConfigDto);
	
	int AW_CFELS_Dimming(DimmingDto pDimmingDto);
	
	int AW_CFELS_VMS(VmsMsgDto pVmsMsgDto);
	
	int AW_CFELS_Reset(ResetDto pResetDto);
	
	int AW_CFELS_FanOpeMode(FanOpeModeDto pFanOpeModeDto);	
	
	int AW_CFELS_TechAlarmAck(TechAlarmAckDtoList pTechnicalAlarmAckDtoList);
	
	int AW_CFELS_TrafficAlertAck(TrafficAlertAckDtoList pTrafficAlertAckDtoList);
	
	int AW_CFELS_TechAlarmClear(TechAlarmDtoList pTechAlarmDtoList, String pClearBy);

	int AW_CFELS_PixelFailureBMPFile(String pEquipId, String pSender);
	
	int AW_CFELS_FlashingTime(FlashingTimeDto pFlashingTimeDto);
	
	int AW_CFELS_OPE_FLAG(OpeFlagDto pOpeFlagDto);
	
	int AW_CFELS_UploadPictogram(String pEquipId, String pPictogramId, String pSender);
}