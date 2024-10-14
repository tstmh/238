/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stee.emas.cmh.service.TrafficAlertManager;
import com.stee.emas.common.dao.ConfigDao;
import com.stee.emas.common.dao.TrafficAlertDao;
import com.stee.emas.common.dto.ImageSequenceDto;
import com.stee.emas.common.dto.PreviewImageDto;
import com.stee.emas.common.dto.TrafficAlertAckDto;
import com.stee.emas.common.dto.TrafficAlertAckDtoList;
import com.stee.emas.common.dto.TrafficAlertClearDto;
import com.stee.emas.common.dto.TrafficAlertDto;
import com.stee.emas.common.entity.DetectorLaneConfig;
import com.stee.emas.common.entity.HistTrafficAlert;
import com.stee.emas.common.entity.TrafficAlert;
import com.stee.emas.common.entity.TrafficAlertImagePreview;
import com.stee.emas.common.entity.TrafficAlertImageSequence;
import com.stee.emas.common.util.DTOConverter;
import com.stee.emas.common.util.MessageConverterUtil;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Manager class for Traffic Alert</p>
 * <p>This class is used for traffic alert related functions
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Oct 17, 2012
 * @version 1.0
 *
 */
@Service
@Transactional
public class TrafficAlertManagerImpl implements TrafficAlertManager {
	
	private static Logger logger = LoggerFactory.getLogger(TrafficAlertManagerImpl.class);
	
	@Autowired
	TrafficAlertDao trafficAlertDao;
	@Autowired
	ConfigDao configDao;
	@Autowired
	MessageConverterUtil messageConverterUtil;

	@Override
	public String saveTrafficAlert(TrafficAlert pTrafficAlert) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> saveTrafficAlert :: AlertId :: " + pTrafficAlert.getAlertId());
		}
		if (pTrafficAlert == null) {
			return null;
		}
		String lId = trafficAlertDao.saveTrafficAlert(pTrafficAlert);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> saveTrafficAlert :: AlertId :: " + pTrafficAlert.getAlertId());
		}
		return lId;
	}

	@Override
	public String updateTrafficAlert(TrafficAlert pTrafficAlert) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> updateTrafficAlert :: AlertId :: " + pTrafficAlert.getAlertId());
		}
		if (pTrafficAlert == null) {
			return null;
		}
		String lId = trafficAlertDao.updateTrafficAlert(pTrafficAlert);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> updateTrafficAlert :: AlertId :: " + pTrafficAlert.getAlertId());
		}
		return lId;
	}

	@Override
	public String deleteTrafficAlert(TrafficAlert pTrafficAlert) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> deleteTrafficAlert :: AlertId :: " + pTrafficAlert.getAlertId());
		}
		if (pTrafficAlert == null) {
			return null;
		}
		String lId = trafficAlertDao.deleteTrafficAlert(pTrafficAlert);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> deleteTrafficAlert :: AlertId :: " + pTrafficAlert.getAlertId());
		}
		return lId;
	}

	@Override
	public TrafficAlert findTrafficAlertById(String pAlertId) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> findTrafficAlertById :: AlertId :: " + pAlertId);
		}
		if (pAlertId == null || pAlertId.trim().length() == 0) {
			logger.info("AlertId is null or empty");
			return null;
		}
		TrafficAlert lTrafficAlert = trafficAlertDao.findTrafficAlertById(pAlertId);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> findTrafficAlertById :: AlertId :: " + pAlertId);
		}
		return lTrafficAlert;
	}

	@Override
	public List<TrafficAlert> getAllTrafficAlert() {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> getAllTrafficAlert ");
		}
		List<TrafficAlert> lTrafficAlertList = trafficAlertDao.getAllTrafficAlert();	
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> getAllTrafficAlert ");
		}
		return lTrafficAlertList;
	}

	@Override
	public List<TrafficAlert> getTrafficAlertByEquipId(String pEquipId) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> getTrafficAlertByEquipId :: EquipId " + pEquipId);
		}
		if (pEquipId == null || pEquipId.trim().length() == 0) {
			logger.info("EquipId is null or empty ... ");
			return null;
		}
		List<TrafficAlert> lTrafficAlertList = trafficAlertDao.getTrafficAlertByEquipId(pEquipId);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> getTrafficAlertByEquipId");
		}
		return lTrafficAlertList;
	}

	@Override
	public void processTrafficAlertAck(TrafficAlertAckDtoList pTrafficAlertAckDtoList) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager -----------> processTrafficAlertAck.....");
		}
		for (TrafficAlertAckDto lTrafficAlertAckDto : pTrafficAlertAckDtoList.getDtoList()) {
			TrafficAlert lTrafficAlert = trafficAlertDao.findTrafficAlertById(lTrafficAlertAckDto.getAlertId());
			if (lTrafficAlert != null) {
				trafficAlertDao.deleteTrafficAlert(lTrafficAlert);
				trafficAlertDao.saveHistTrafficAlert(messageConverterUtil.generateHistTrafficAlert(lTrafficAlert, lTrafficAlertAckDto));
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager -----------> processTrafficAlertAck");
		}
	}

	@Override
	public void processTrafficAlert(TrafficAlertDto pTrafficAlertDto) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager -----------> processTrafficAlert.....");
		}
		logger.info("Inside Dao :: findDetectorLaneConfigByUniqueId -> Start :: EquipId :: "
				+ pTrafficAlertDto.getEquipId() + " LaneId :: " + pTrafficAlertDto.getLaneId() + " LaneType :: "
				+ pTrafficAlertDto.getLaneType());

		DetectorLaneConfig lDetectorLaneConfig = configDao.findDetectorLaneConfigByUniqueId(
				pTrafficAlertDto.getEquipId(), pTrafficAlertDto.getLaneId(), pTrafficAlertDto.getLaneType());
		logger.info("...lDetectorLaneConfig..." + lDetectorLaneConfig);
		
		TrafficAlert lTrafficAlert = DTOConverter.convert(TrafficAlert.class, pTrafficAlertDto);
		lTrafficAlert.setDetectorLaneConfig(lDetectorLaneConfig);

		if (pTrafficAlertDto.getImageUrl() == null
				|| (pTrafficAlertDto.getImageUrl() != null && pTrafficAlertDto.getImageUrl().trim().length() == 0)) {
			String lImageUrl = processPreviewImageDelete(pTrafficAlertDto);
			lTrafficAlert.setImageUrl(lImageUrl);
		}

		if (pTrafficAlertDto.getVideoUrl() == null
				|| (pTrafficAlertDto.getVideoUrl() != null && pTrafficAlertDto.getVideoUrl().trim().length() == 0)) {
			String lVideoUrl = processPreviewSequenceDelete(pTrafficAlertDto);
			lTrafficAlert.setVideoUrl(lVideoUrl);
		}

		trafficAlertDao.saveTrafficAlert(lTrafficAlert);
		logger.info("Traffic Alert saved successfully for AlertId :: " + pTrafficAlertDto.getAlertId());

		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager -----------> processTrafficAlert.....");
		}
	}
	
	@Override
	public void processTrafficAlertClear(TrafficAlertDto pTrafficAlertDto) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager -----------> processTrafficAlertClear.....");
		}
		
		TrafficAlert lTrafficAlert = trafficAlertDao.findTrafficAlertById(pTrafficAlertDto.getAlertId());
		if (lTrafficAlert != null) {
			lTrafficAlert.setEndDate(pTrafficAlertDto.getEndDate());
			trafficAlertDao.updateTrafficAlert(lTrafficAlert);
			logger.info("Traffic Alert updated successfully for AlertId :: " + pTrafficAlertDto.getAlertId());
		} else {
			HistTrafficAlert lHistTrafficAlert = trafficAlertDao.findHistTrafficAlertById(pTrafficAlertDto.getAlertId());
			if (lHistTrafficAlert != null) {
				lHistTrafficAlert.setEndDate(pTrafficAlertDto.getEndDate());
				trafficAlertDao.updateHistTrafficAlert(lHistTrafficAlert);
				logger.info("HistTraffic Alert updated successfully for AlertId :: " + pTrafficAlertDto.getAlertId());
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager -----------> processTrafficAlertClear.....");
		}
	}

	@Override
	public void processPreviewImage(PreviewImageDto pPreviewImageDto) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager -----------> processPreviewImage.....");
		}
		TrafficAlert lTrafficAlert = trafficAlertDao.findTrafficAlertById(pPreviewImageDto.getAlertId());
		if (lTrafficAlert != null) {
			lTrafficAlert.setImageUrl(pPreviewImageDto.getImageURL());
			trafficAlertDao.updateTrafficAlert(lTrafficAlert);
		} else {
			HistTrafficAlert lHistTrafficAlert = trafficAlertDao.findHistTrafficAlertById(pPreviewImageDto.getAlertId());
			if (lHistTrafficAlert != null) {
				lHistTrafficAlert.setImageUrl(pPreviewImageDto.getImageURL());
				trafficAlertDao.updateHistTrafficAlert(lHistTrafficAlert);
			} else {
				TrafficAlertImagePreview lTrafficAlertImagePreview = new TrafficAlertImagePreview();
				lTrafficAlertImagePreview.setAlertId(pPreviewImageDto.getAlertId());
				lTrafficAlertImagePreview.setImageUrl(pPreviewImageDto.getImageURL());
				lTrafficAlertImagePreview.setImageDate(new Date());
				trafficAlertDao.saveTrafficAlertImagePreview(lTrafficAlertImagePreview);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager -----------> processPreviewImage.....");
		}
	}
	
	@Override
	public void processImageSequence(ImageSequenceDto pImageSequenceDto) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager -----------> processImageSequence.....");
		}
		TrafficAlert lTrafficAlert = trafficAlertDao.findTrafficAlertById(pImageSequenceDto.getAlertId());
		if (lTrafficAlert != null) {
			lTrafficAlert.setVideoUrl(pImageSequenceDto.getVideoURL());
			trafficAlertDao.updateTrafficAlert(lTrafficAlert);
		} else {
			HistTrafficAlert lHistTrafficAlert = trafficAlertDao.findHistTrafficAlertById(pImageSequenceDto.getAlertId());
			if (lHistTrafficAlert != null) {
				lHistTrafficAlert.setImageUrl(pImageSequenceDto.getVideoURL());
				trafficAlertDao.updateHistTrafficAlert(lHistTrafficAlert);
			} else {
				TrafficAlertImageSequence lTrafficAlertImageSequence = new TrafficAlertImageSequence();
				lTrafficAlertImageSequence.setAlertId(pImageSequenceDto.getAlertId());
				lTrafficAlertImageSequence.setVideoUrl(pImageSequenceDto.getVideoURL());
				lTrafficAlertImageSequence.setVideoDate(new Date());
				trafficAlertDao.saveTrafficAlertImageSequence(lTrafficAlertImageSequence);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager -----------> processImageSequence.....");
		}
	}

	@Override
	public String processPreviewSequenceDelete(TrafficAlertDto pTrafficAlertDto) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager -----------> processPreviewSequenceDelete.....");
		}
		String lVideoUrl = null;
		TrafficAlertImageSequence llTrafficAlertImageSequence = trafficAlertDao.findTrafficAlertImageSequenceById(pTrafficAlertDto.getAlertId());
		if (llTrafficAlertImageSequence != null) {
			lVideoUrl = llTrafficAlertImageSequence.getVideoUrl();
			trafficAlertDao.deleteTrafficAlertImageSequence(llTrafficAlertImageSequence);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager -----------> processPreviewSequenceDelete.....");
		}
		return lVideoUrl;
	}

	@Override
	public String processPreviewImageDelete(TrafficAlertDto pTrafficAlertDto) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager -----------> processPreviewImageDelete.....");
		}
		String lImageUrl = null;
		TrafficAlertImagePreview llTrafficAlertImagePreview = trafficAlertDao.findTrafficAlertImagePreviewById(pTrafficAlertDto.getAlertId());
		if (llTrafficAlertImagePreview != null) {
			lImageUrl = llTrafficAlertImagePreview.getImageUrl();
			trafficAlertDao.deleteTrafficAlertImagePreview(llTrafficAlertImagePreview);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager -----------> processPreviewImageDelete.....");
		}
		return lImageUrl;
	}
	
	@Override
	public List<TrafficAlertImagePreview> getAllPreviewImage(Date pDate) {
		/*if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager -----------> getAllPreviewImage....." + pDate);
		}
		List<TrafficAlertImagePreview> lPreviewImageList = trafficAlertDao.getAllPreviewImageByDate(pDate);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager -----------> getAllPreviewImage.....");
		}
		return lPreviewImageList;*/
		return null;
	}

	@Override
	public boolean processPendingImages(TrafficAlertImagePreview pTrafficAlertImagePreview) {
		boolean trafficAlertUpdated = false;
		try {
			TrafficAlert lTrafficAlert = trafficAlertDao.findTrafficAlertById(pTrafficAlertImagePreview.getAlertId());
			if (lTrafficAlert != null) {
				lTrafficAlert.setImageUrl(pTrafficAlertImagePreview.getImageUrl());
				trafficAlertDao.updateTrafficAlert(lTrafficAlert);
				trafficAlertUpdated = true;
			} else {
				HistTrafficAlert lHistTrafficAlert = trafficAlertDao.findHistTrafficAlertById(pTrafficAlertImagePreview.getAlertId());
				if (lHistTrafficAlert != null) {
					lHistTrafficAlert.setImageUrl(pTrafficAlertImagePreview.getImageUrl());
					trafficAlertDao.updateHistTrafficAlert(lHistTrafficAlert);
					trafficAlertUpdated = true;
				}
			}
			if (trafficAlertUpdated) {
				trafficAlertDao.deleteTrafficAlertImagePreview(pTrafficAlertImagePreview);
			}
		} catch (Exception e) {
			trafficAlertUpdated = false;
    		logger.error("Error processing processPendingImages ... ", e);
		}
		return trafficAlertUpdated;
	}

	@Override
	public TrafficAlertDto processSmokeClear(TrafficAlertClearDto lTrafficAlertClearDto) {
		logger.info(".....Calling processSmokeClear.....");
		TrafficAlertDto lTrafficAlertDto = null;
		TrafficAlert lTrafficAlert = trafficAlertDao.findTrafficAlertById(lTrafficAlertClearDto.getAlertId());
		logger.info("TrafficAlert......." + lTrafficAlert);
		if (lTrafficAlert != null) {
			lTrafficAlert.setEndDate(lTrafficAlertClearDto.getClearTime());
			trafficAlertDao.updateTrafficAlert(lTrafficAlert);
			lTrafficAlertDto = buildTrafficAlertDto(lTrafficAlert);
		} else {
			HistTrafficAlert lHistTrafficAlert = trafficAlertDao.findHistTrafficAlertById(lTrafficAlertClearDto.getAlertId());
			if (lHistTrafficAlert != null) {
				lHistTrafficAlert.setEndDate(lTrafficAlertClearDto.getClearTime());
				trafficAlertDao.updateHistTrafficAlert(lHistTrafficAlert);
				lTrafficAlertDto = buildTrafficAlertDto(lHistTrafficAlert);
			}
		}
		return lTrafficAlertDto;
	}
	
	private TrafficAlertDto buildTrafficAlertDto(TrafficAlert pTrafficAlert) {
		TrafficAlertDto lTrafficAlertDto = new TrafficAlertDto();
		lTrafficAlertDto.setAlertId(pTrafficAlert.getAlertId());
		lTrafficAlertDto.setAlertCode(pTrafficAlert.getAlertCode());
		lTrafficAlertDto.setAlertDate(pTrafficAlert.getAlertDate());
		lTrafficAlertDto.setEquipId(pTrafficAlert.getDetectorLaneConfig().getEquipId());
		lTrafficAlertDto.setLaneId(pTrafficAlert.getDetectorLaneConfig().getLaneId());
		lTrafficAlertDto.setLaneType(pTrafficAlert.getDetectorLaneConfig().getLaneType());
		lTrafficAlertDto.setEndDate(pTrafficAlert.getEndDate());
		lTrafficAlertDto.setImageUrl(pTrafficAlert.getImageUrl());
		lTrafficAlertDto.setImageFileName(pTrafficAlert.getImageFileName());
		lTrafficAlertDto.setVideoUrl(pTrafficAlert.getVideoUrl());
		lTrafficAlertDto.setOutputResult(pTrafficAlert.getOutputResult());
		lTrafficAlertDto.setOutputReason(pTrafficAlert.getOutputReason());
		
		return lTrafficAlertDto;
	}
	
	private TrafficAlertDto buildTrafficAlertDto(HistTrafficAlert pTrafficAlert) {
		TrafficAlertDto lTrafficAlertDto = new TrafficAlertDto();
		lTrafficAlertDto.setAlertId(pTrafficAlert.getAlertId());
		lTrafficAlertDto.setAlertCode(pTrafficAlert.getAlertCode());
		lTrafficAlertDto.setAlertDate(pTrafficAlert.getAlertDate());
		//20/04/2020 -> Grace:: Breaking up HistTrafficAlert from EquipConfig
		//lTrafficAlertDto.setEquipId(pTrafficAlert.getEquipConfig().getEquipId());
		lTrafficAlertDto.setEquipId(pTrafficAlert.getEquipId());
		lTrafficAlertDto.setLaneId(pTrafficAlert.getLaneAffected());
		lTrafficAlertDto.setLaneType(pTrafficAlert.getLaneType());
		lTrafficAlertDto.setEndDate(pTrafficAlert.getEndDate());
		lTrafficAlertDto.setImageUrl(pTrafficAlert.getImageUrl());
		lTrafficAlertDto.setImageFileName(pTrafficAlert.getImageFileName());
		lTrafficAlertDto.setVideoUrl(pTrafficAlert.getVideoUrl());
		lTrafficAlertDto.setOutputResult(pTrafficAlert.getOutputResult());
		lTrafficAlertDto.setOutputReason(pTrafficAlert.getOutputReason());
		
		return lTrafficAlertDto;
	}
}