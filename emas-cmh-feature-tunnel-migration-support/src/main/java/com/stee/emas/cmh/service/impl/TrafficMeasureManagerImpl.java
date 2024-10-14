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

import com.stee.emas.cmh.service.TrafficMeasureManager;
import com.stee.emas.common.dao.ConfigDao;
import com.stee.emas.common.dao.TrafficMeasureDao;
import com.stee.emas.common.entity.DetectorLaneConfig;
import com.stee.emas.common.entity.TrafficMeasure;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Manager class for Traffic Measure</p>
 * <p>This class is used for traffic measure related functions
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Oct 16, 2012
 * @version 1.0
 *
 */

@Service
@Transactional
public class TrafficMeasureManagerImpl implements TrafficMeasureManager {
	
	private static Logger logger = LoggerFactory.getLogger(TrafficMeasureManagerImpl.class);
	
	@Autowired
	TrafficMeasureDao trafficMeasureDao;
	@Autowired
	ConfigDao configDao;

	@Override
	public String saveTrafficMeasure(TrafficMeasure pTrafficMeasure) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> saveTrafficMeasure :: EquipId :: " + pTrafficMeasure.getDetectorLaneConfig().getEquipId() 
					+ "LaneId :: " + pTrafficMeasure.getDetectorLaneConfig().getLaneId());
		}
		if (pTrafficMeasure == null) {
			return null;
		}
		if (logger.isInfoEnabled()) {
			logger.info("EquipId :: " + pTrafficMeasure.getDetectorLaneConfig().getEquipId() + "LaneId :: " + pTrafficMeasure.getDetectorLaneConfig().getLaneId());
		}
		String lId = trafficMeasureDao.saveTrafficMeasure(pTrafficMeasure);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> saveTrafficMeasure :: EquipId :: " + pTrafficMeasure.getDetectorLaneConfig().getEquipId()
					+ "LaneId :: " + pTrafficMeasure.getDetectorLaneConfig().getLaneId());
		}
		return lId;
	}

	@Override
	public String updateTrafficMeasure(TrafficMeasure pTrafficMeasure) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> updateTrafficMeasure :: EquipId :: " + pTrafficMeasure.getDetectorLaneConfig().getEquipId() 
					+ "LaneId :: " + pTrafficMeasure.getDetectorLaneConfig().getLaneId());
		}
		if (pTrafficMeasure == null) {
			return null;
		}
		if (logger.isInfoEnabled()) {
			logger.info("EquipId :: " + pTrafficMeasure.getDetectorLaneConfig().getEquipId() + "LaneId :: " + pTrafficMeasure.getDetectorLaneConfig().getLaneId());
		}
		String lId = trafficMeasureDao.updateTrafficMeasure(pTrafficMeasure);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> updateTrafficMeasure :: EquipId :: " + pTrafficMeasure.getDetectorLaneConfig().getEquipId()
					+ "LaneId :: " + pTrafficMeasure.getDetectorLaneConfig().getLaneId());
		}
		return lId;
	}

	@Override
	public String deleteTrafficMeasure(TrafficMeasure pTrafficMeasure) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> deleteTrafficMeasure :: EquipId :: " + pTrafficMeasure.getDetectorLaneConfig().getEquipId()
					+ "LaneId :: " + pTrafficMeasure.getDetectorLaneConfig().getLaneId());
		}
		if (pTrafficMeasure == null) {
			return null;
		}
		if (logger.isInfoEnabled()) {
			logger.info("EquipId :: " + pTrafficMeasure.getDetectorLaneConfig().getEquipId() + "LaneId :: " + pTrafficMeasure.getDetectorLaneConfig().getLaneId());
		}
		String lId = trafficMeasureDao.deleteTrafficMeasure(pTrafficMeasure);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> deleteTrafficMeasure :: EquipId :: " + pTrafficMeasure.getDetectorLaneConfig().getEquipId()
					+ "LaneId :: " + pTrafficMeasure.getDetectorLaneConfig().getLaneId());
		}
		return lId;
	}

	@Override
	public TrafficMeasure findTrafficMeasureByUniqueId(String pEquipId, int pLaneId, int pLaneType, String pDataType) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager ----------> findTrafficMeasureById :: EquipId " + pEquipId + "LaneId :: " + pLaneId + " DataType :: " + pDataType);
		}
		if (pEquipId == null || pEquipId.trim().length() == 0) {
			logger.info("EquipId is null or empty ..... ");
			return null;
		}
		TrafficMeasure lTrafficMeasure = null;
		DetectorLaneConfig lDetectorLaneConfig = configDao.findDetectorLaneConfigByUniqueId(pEquipId, pLaneId, pLaneType);
		if (lDetectorLaneConfig != null) {
			lTrafficMeasure = trafficMeasureDao.findTrafficMeasureByUniqueId(lDetectorLaneConfig, pDataType);
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager ----------> findEquipStatusById :: EquipId " + pEquipId + "LaneId :: " + pLaneId + " DataType :: " + pDataType);
		}
		return lTrafficMeasure;
	}

	@Override
	public List<TrafficMeasure> getAllTrafficMeasure() {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager ----------> getAllTrafficMeasure ");
		}
		List<TrafficMeasure> lTrafficMeasureList = trafficMeasureDao.getAllTrafficMeasure();
		if (lTrafficMeasureList != null) {
			logger.info("TrafficMeasureList Size :: " + lTrafficMeasureList.size());
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager ----------> getAllTrafficMeasure ");
		}
		return lTrafficMeasureList;
	}

	@Override
	public List<TrafficMeasure> getTrafficMeasureByDataType(String pDataType) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager ----------> getAllTrafficMeasure ");
		}
		if (pDataType == null || pDataType.trim().length() == 0) {
			logger.info("Data Type is null or empty ..... ");
			return null;
		}
		List<TrafficMeasure> lTrafficMeasureList = trafficMeasureDao.getTrafficMeasureByDataType(pDataType);
		if (lTrafficMeasureList != null) {
			logger.info("TrafficMeasureList Size :: " + lTrafficMeasureList.size());
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager ----------> getAllTrafficMeasure ");
		}
		return lTrafficMeasureList;
	}
}