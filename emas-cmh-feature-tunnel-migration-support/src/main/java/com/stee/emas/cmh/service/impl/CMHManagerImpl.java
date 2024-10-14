/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stee.emas.cmh.common.CMHKeyBean;
import com.stee.emas.cmh.common.StartupListener;
import com.stee.emas.cmh.integration.CMHMessageSender;
import com.stee.emas.cmh.service.CMHManager;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dao.ConfigDao;
import com.stee.emas.common.dao.EquipStatusDao;
import com.stee.emas.common.dao.PixelFailureBMPFileDao;
import com.stee.emas.common.dao.VmsPictogramConfigDao;
import com.stee.emas.common.dto.OpeFlagDto;
import com.stee.emas.common.dto.VmsPictogramConfigDto;
import com.stee.emas.common.entity.EquipConfig;
import com.stee.emas.common.entity.EquipStatus;
import com.stee.emas.common.entity.HistActivityLog;
import com.stee.emas.common.entity.PictogramDownloadStatus;
import com.stee.emas.common.entity.SystemParameter;
import com.stee.emas.common.entity.VmsPictogramConfig;
import com.stee.emas.common.util.DTOConverter;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : CMH Manager</p>
 * <p>This class is used by CMH to process the message/object
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Dec 3, 2012
 * @version 1.0
 *
 */

@Service("cmhManager")
@Transactional
public class CMHManagerImpl implements CMHManager {
	
	private static Logger logger = LoggerFactory.getLogger(CMHManagerImpl.class);
	
	public static Hashtable<CMHKeyBean, String> cmhHtable = new Hashtable<CMHKeyBean, String>();
	
	@Autowired
	CMHMessageSender cmhMessageSender;
	@Autowired
	VmsPictogramConfigDao vmsPictogramConfigDao;
	@Autowired
	EquipStatusDao equipStatusDao;	
	@Autowired
	PixelFailureBMPFileDao pixelFailureBmpFileDao;
	@Autowired
	ConfigDao configDao;
	@Autowired
	StartupListener startupListener;

	@Override
	public int processAwCfelsPictogramSet(VmsPictogramConfigDto pVmsPictogramConfigDto) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager :: processAwCfelsPictogramSet -> PictogramId::"+pVmsPictogramConfigDto.getPictogramId());
		}
		int lSuccess;
		if (pVmsPictogramConfigDto.getPictogramId() == 0) {
			logger.info("PictogramId is null, hence inserting to Vms Pictogram Config table ..... ");
			lSuccess = savePictogram(pVmsPictogramConfigDto);
		} else {
			logger.info("Updating Vms Pictogram Config table ..... ");
			if (pVmsPictogramConfigDto.getEquipIdList() == null || pVmsPictogramConfigDto.getEquipIdList().size() == 0) {
				lSuccess = updatePictogram(pVmsPictogramConfigDto);
				
				List<String> lEquipIdList = vmsPictogramConfigDao.getEquipIdListForPictogramId(pVmsPictogramConfigDto.getPictogramId(), null);
				List<EquipConfig> lAllEquipIdList = configDao.getEquipConfigByEquipType(Constants.VMS_EQUIP_TYPE_ARRAY);

				int lAllSize = lAllEquipIdList.size();
				if (lAllSize == lEquipIdList.size()) {
					pVmsPictogramConfigDto.setEquipIdList(null);
				} else {
					pVmsPictogramConfigDto.setEquipIdList(lEquipIdList);
				}
			} else {
				lSuccess = downloadPictogramToOtherEquipment(pVmsPictogramConfigDto);
			}
		}
		if (lSuccess == 0) {
			String lActionDetail = new String();
			if (pVmsPictogramConfigDto.getEquipIdList() != null && pVmsPictogramConfigDto.getEquipIdList().size() > 0) {
				lActionDetail = "Pictogram downloaded to All Equipments ";
			} else {
				lActionDetail = "Pictogram downloaded to few Equipments ";
			}
			createAuditTrail(pVmsPictogramConfigDto.getSender(), "VMS Pictogram", lActionDetail);
			
			logger.info("Object sent to the queue " + pVmsPictogramConfigDto);
			cmhMessageSender.sendTicssJmsMessage(pVmsPictogramConfigDto, MessageConstants.PICTOGRAM_ID);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager :: processAwCfelsPictogramSet.....");
		}
		return lSuccess;
	}
	
	@Override
	public int savePictogram(VmsPictogramConfigDto pVmsPictogramConfigDto) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager ----------> savePictogram.....");
		}
		int lSuccess = 0;
		try {
			VmsPictogramConfig lVmsPictogramConfig = DTOConverter.convert(VmsPictogramConfig.class, pVmsPictogramConfigDto);
			
			int lMaxPictogramId = vmsPictogramConfigDao.getMaxOfPictogramId(pVmsPictogramConfigDto.getHeight(), pVmsPictogramConfigDto.getWidth());
			logger.info("Max PictogramId :: " +lMaxPictogramId);
			if (lMaxPictogramId < 0) {
				logger.info("Given Height and Width not in the database, hence cannot insert into VMS_PICTOGRAM_CONFIG");
				lSuccess = 1;
			}
			lVmsPictogramConfig.setPictogramId(lMaxPictogramId + 1);
			VmsPictogramConfig lVmsPictogramConfigDb = vmsPictogramConfigDao.getVmsPictogramByPictogramId(pVmsPictogramConfigDto.getPictogramId());
			logger.info("PictogramId :: " + pVmsPictogramConfigDto.getPictogramId());
			if (lVmsPictogramConfigDb != null) {
				logger.error("Pictogram Id already exists in the database, hence cannot insert into VMS_PICTOGRAM_CONFIG");
				lSuccess = 1;
			}
			Integer lPictogramId = vmsPictogramConfigDao.savePictogram(lVmsPictogramConfig);
			logger.info("Pictogram saved successfully ..... ");
			if (lPictogramId != null) {
				if (pVmsPictogramConfigDto.getEquipIdList() != null && pVmsPictogramConfigDto.getEquipIdList().size() > 0) {
					for (String lEquipId : pVmsPictogramConfigDto.getEquipIdList()) {
						PictogramDownloadStatus lPictogramDownloadStatus = new PictogramDownloadStatus();
						lPictogramDownloadStatus.setEquipId(lEquipId);
						lPictogramDownloadStatus.setPictogramId(lPictogramId);
						lPictogramDownloadStatus.setDownloadTime(Calendar.getInstance().getTime());
						lPictogramDownloadStatus.setStatus(Constants.FAIL);
						vmsPictogramConfigDao.savePictogramDownloadStatus(lPictogramDownloadStatus);
						logger.info("Data saved successfully in Pictogram Download Status..... ");
					}
				} else {
					List<EquipConfig> lEquipConfigList = configDao.getEquipConfigByEquipType(Constants.VMS_EQUIP_TYPE_ARRAY);
					for (EquipConfig lEquipConfig : lEquipConfigList) {
						PictogramDownloadStatus lPictogramDownloadStatus = new PictogramDownloadStatus();
						lPictogramDownloadStatus.setEquipId(lEquipConfig.getEquipId());
						lPictogramDownloadStatus.setPictogramId(lPictogramId);
						lPictogramDownloadStatus.setDownloadTime(Calendar.getInstance().getTime());
						lPictogramDownloadStatus.setStatus(Constants.FAIL);
						vmsPictogramConfigDao.savePictogramDownloadStatus(lPictogramDownloadStatus);
						logger.info("Data saved successfully in Pictogram Download Status..... ");
					}
				}
				pVmsPictogramConfigDto.setPictogramId(lPictogramId);
			}
		} catch (Exception e) {
			logger.error("Error in saving Pictogram Config", e);
			lSuccess = 1;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager ----------> savePictogram.....");
		}
		return lSuccess;
	}
	
	@Override
	public int updatePictogram(VmsPictogramConfigDto pVmsPictogramConfigDto) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager ----------> updatePictogram.....");
		}
		VmsPictogramConfig lVmsPictogramConfig = DTOConverter.convert(VmsPictogramConfig.class, pVmsPictogramConfigDto);
		int lSuccess = 0;
		try {
			vmsPictogramConfigDao.updatePictogram(lVmsPictogramConfig);
			logger.info("Pictogram updated successfully ..... ");
		} catch (Exception e) {
			lSuccess = 1;
			logger.error("Problem in updating pictogram .....", e);
		}
		try {
			if (lSuccess == 0) {
				List<PictogramDownloadStatus> lPictogramDownloadStatusList = vmsPictogramConfigDao.getPictogramDownLoadStatusForPictogramId(lVmsPictogramConfig.getPictogramId());
				if (lPictogramDownloadStatusList != null && lPictogramDownloadStatusList.size() > 0) {
					for (PictogramDownloadStatus lPictogramDownloadStatus : lPictogramDownloadStatusList) {
						lPictogramDownloadStatus.setDownloadTime(Calendar.getInstance().getTime());
						lPictogramDownloadStatus.setStatus(Constants.FAIL);
						vmsPictogramConfigDao.updatePictogramDownloadStatus(lPictogramDownloadStatus);
						logger.info("Data updated successfully in Pictogram Download Status..... ");
					}
				}
			}
		} catch (Exception e) {
			lSuccess = 1;
			logger.error("Problem in updating pictogram .....", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager ----------> updatePictogram.....");
		}
		return lSuccess;
	}
	
	@Override
	public int downloadPictogramToOtherEquipment(VmsPictogramConfigDto pVmsPictogramConfigDto) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager ----------> downloadExistingPictogramToOtherEquipment.....");
		}
		VmsPictogramConfig lVmsPictogramConfig = DTOConverter.convert(VmsPictogramConfig.class, pVmsPictogramConfigDto);
		int lIsSuccess = 0;
		try {
			for (String lEquipId : pVmsPictogramConfigDto.getEquipIdList()) {
				PictogramDownloadStatus lPictogramDownloadStatus = new PictogramDownloadStatus();
				lPictogramDownloadStatus.setEquipId(lEquipId);
				lPictogramDownloadStatus.setPictogramId(lVmsPictogramConfig.getPictogramId());
				lPictogramDownloadStatus.setDownloadTime(Calendar.getInstance().getTime());
				lPictogramDownloadStatus.setStatus(Constants.FAIL);
				vmsPictogramConfigDao.savePictogramDownloadStatus(lPictogramDownloadStatus);
				logger.info("Data saved successfully in Pictogram Download Status..... ");
			}
		} catch (Exception e) {
			logger.error("Error in saving in PictogramDownload Status .....", e);
			lIsSuccess = 1;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager ----------> downloadExistingPictogramToOtherEquipment.....");
		}
		return lIsSuccess;
	}
	
	@Override
	public void handleAWCfelsOpeFlag(OpeFlagDto pOpeFlagDto) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside Manager ----------> handleAWCfelsOpeFlag .....");
		}
		SystemParameter lSystemParameter = configDao.findSystemParameterByName(Constants.OPE_ENABLE_FLAG);
		if (lSystemParameter != null) {
			lSystemParameter.setValue(Integer.toString(pOpeFlagDto.getFlag()));
			lSystemParameter.setSetBy(pOpeFlagDto.getUserId());
			lSystemParameter.setDateTime(new Date());
			configDao.updateSystemParameter(lSystemParameter);
			logger.info("System Parameter for OPE FLAG updated successfully .....");
			
			String lActionDetail = new String();
			if (Integer.toString(pOpeFlagDto.getFlag()).equals(Constants.ENABLE_OPE_FLAG)) {
				lActionDetail = "OPE Flag Enabled";
			} else if (Integer.toString(pOpeFlagDto.getFlag()).equals(Constants.DISABLE_OPE_FLAG)) {
				lActionDetail = "OPE Flag Disabled";
			}
			createAuditTrail(pOpeFlagDto.getUserId(), "OPE FLAG", lActionDetail);
			startupListener.processOPEFlag();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Exit Manager ----------> handleAWCfelsOpeFlag .....");
		}
	}
	
	@Override
	public SystemParameter findSystemParameterByName(String pName) {
		SystemParameter lSystemParameter = configDao.findSystemParameterByName(pName);
		return lSystemParameter;
	}
	
	@Override
	public EquipConfig getEquipConfig(String pEquipId) {
		EquipConfig lEquipConfig = configDao.findEquipConfigById(pEquipId);
		return lEquipConfig;
	}
	
	@Override
	public List<?> getAdjacentDetectors(String pEquipId) {
		List<?> lAdjacentDetectorList = configDao.getAdjacentDetectors(pEquipId);
		return lAdjacentDetectorList;
	}
	
	@Override
	public String getNextEquipConfig(String pEquipId) {
		EquipConfig lEquipConfig = configDao.findEquipConfigById(pEquipId);
		if (lEquipConfig != null) {
			String lNextEquipId = configDao.getNextEquipConfig(lEquipConfig);
			return lNextEquipId;
		}
		return null;
	}
	
	@Override
	public String getPreviousEquipConfig(String pEquipId) {
		EquipConfig lEquipConfig = configDao.findEquipConfigById(pEquipId);
		if (lEquipConfig != null) {
			String lPrevEquipId = configDao.getPreviousEquipConfig(lEquipConfig);
			return lPrevEquipId;
		}
		return null;
	}
	
	@Override
	public boolean canProcessTrafficAlert(String pEquipId) {
		EquipConfig lEquipConfig = configDao.findEquipConfigById(pEquipId);
		boolean lTrafficDataEnabled = false;
		if (lEquipConfig != null) {
			lTrafficDataEnabled = lEquipConfig.getTrafficDataEnabled();
		}
		logger.info("lTrafficDataEnabled :: "+ lTrafficDataEnabled);
		return lTrafficDataEnabled;
	}

	@Override
	public void createAuditTrail(String pSender, String pAction, String pActionDetail) {
		logger.info("Inside CMHManager -> Start .....Sender :: " + pSender + ".....Action ::" + pAction + ".....ActionDetail :: " + pActionDetail);
		HistActivityLog lHistActivityLog = new HistActivityLog();
		lHistActivityLog.setUserId(pSender);
		lHistActivityLog.setAction(pAction);
		lHistActivityLog.setActionDetail(pActionDetail);
		lHistActivityLog.setDateTime(new Date());
		configDao.saveHistActivityLog(lHistActivityLog);
		logger.info("Activity Log created successfully ..... ");
	}
	
	@Override
	public EquipStatus findEquipStatusByEquipIdAndStatusCode(String pEquipId, String pStatusCode) {		
		if (pEquipId == null || pEquipId.trim().length() == 0 || pStatusCode == null || pStatusCode.trim().length() == 0) {
			return null;
		}
		EquipStatus lEquipStatus = equipStatusDao.findEquipStatusByEquipIdAndStatusCode(pEquipId, pStatusCode);		
		return lEquipStatus;
	}
	
	@Override
	public String updateEquipStatus(EquipStatus pEquipStatus) {
		if (pEquipStatus == null) {
			return null;
		}
		String lId = equipStatusDao.updateEquipStatus(pEquipStatus);
		return lId;
	}
}