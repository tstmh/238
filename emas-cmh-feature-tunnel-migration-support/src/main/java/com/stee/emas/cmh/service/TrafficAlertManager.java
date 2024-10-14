/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.service;

import java.util.Date;
import java.util.List;

import com.stee.emas.common.dto.ImageSequenceDto;
import com.stee.emas.common.dto.PreviewImageDto;
import com.stee.emas.common.dto.TrafficAlertAckDtoList;
import com.stee.emas.common.dto.TrafficAlertClearDto;
import com.stee.emas.common.dto.TrafficAlertDto;
import com.stee.emas.common.entity.TrafficAlert;
import com.stee.emas.common.entity.TrafficAlertImagePreview;

/**
 * 
 * @author Scindia
 * @since Oct 17, 2012
 * @version 1.0
 *
 */

public interface TrafficAlertManager {
	
	String saveTrafficAlert(TrafficAlert pTrafficAlert);
	
	String updateTrafficAlert(TrafficAlert pTrafficAlert);
	
	String deleteTrafficAlert(TrafficAlert pTrafficAlert);
	
	TrafficAlert findTrafficAlertById(String pAlertId);
	
	List<TrafficAlert> getAllTrafficAlert();
	
	List<TrafficAlert> getTrafficAlertByEquipId(String pEquipId);

	void processTrafficAlertAck(TrafficAlertAckDtoList pTrafficAlertAckDtoList);

	void processTrafficAlert(TrafficAlertDto pTrafficAlertDto);
	
	void processTrafficAlertClear(TrafficAlertDto pTrafficAlertDto);

	void processPreviewImage(PreviewImageDto pPreviewImageDto);
	
	// Added by Grace on 02/07/2020 to hand new image sequence
	void processImageSequence(ImageSequenceDto pImageSequenceDto);

	String processPreviewImageDelete(TrafficAlertDto pTrafficAlertDto);

	// Added by Grace on 02/07/2020 to hand new image sequence
	String processPreviewSequenceDelete(TrafficAlertDto pTrafficAlertDto);

	List<TrafficAlertImagePreview> getAllPreviewImage(Date pDate);

	boolean processPendingImages(TrafficAlertImagePreview pTrafficAlertImagePreview);

	TrafficAlertDto processSmokeClear(TrafficAlertClearDto lTrafficAlertClearDto);
}

