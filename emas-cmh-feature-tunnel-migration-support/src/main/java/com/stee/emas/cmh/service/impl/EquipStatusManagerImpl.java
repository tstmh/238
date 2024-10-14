/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.service.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/*import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import com.stee.emas.cmh.common.ApplicationContextProvider;
import com.stee.emas.cmh.monitor.SchedulerConfig;
import com.stee.emas.cmh.monitor.SchedulerConfigNetwork;
import com.stee.emas.cmh.monitor.SchedulerConfigUps;
import com.stee.emas.cmh.service.EquipStatusManager;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.dao.EquipStatusDao;
import com.stee.emas.common.dao.TechnicalAlarmDao;
import com.stee.emas.common.dto.EquipStatusDto;
import com.stee.emas.common.dto.EquipStatusDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emas.common.entity.EquipStatus;
import com.stee.emas.common.entity.EquipStatusId;
import com.stee.emas.common.entity.UpsArgentStatus;
import com.stee.emas.common.util.MessageConverterUtil;

/***** Changed by Grace 18/12/19 *****/
import com.stee.emas.cmh.tpnd.TPNDManager;
/*************************************/

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Manager class for Equip Status </p>
 * <p>This class is used for Equip Status related functions
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Oct 16, 2012
 * @version 1.0
 *
 */

@Service
@Transactional
public class EquipStatusManagerImpl implements EquipStatusManager {

	private static Logger logger = LoggerFactory.getLogger(EquipStatusManagerImpl.class);

	@Autowired
	EquipStatusDao equipStatusDao;
	@Autowired
	TechnicalAlarmDao technicalAlarmDao;
	@Autowired
	MessageConverterUtil messageConverterUtil;
	/***** Changed by Grace 18/12/19 *****/
	@Autowired
	TPNDManager tpndManager;
	/*************************************/

	@Override
	public String saveEquipStatus(EquipStatus pEquipStatus) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> saveEquipStatus ");
		}
		if (pEquipStatus == null) {
			return null;
		}
		if (logger.isInfoEnabled()) {
			logger.info("EquipId :: " + pEquipStatus.getEquipConfig().getEquipId() + " StatusCode :: " + pEquipStatus.getStatusCode());
		}
		String lId = equipStatusDao.saveEquipStatus(pEquipStatus);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> saveEquipStatus :: EquipId :: " + pEquipStatus.getEquipConfig().getEquipId() + "statusCode :: " + pEquipStatus.getStatusCode());
		}
		return lId;
	}

	@Override
	public String updateEquipStatus(EquipStatus pEquipStatus) {
		if (pEquipStatus == null) {
			return null;
		}
		if (logger.isInfoEnabled()) {
			logger.info("EquipId :: " + pEquipStatus.getEquipConfig().getEquipId() + " StatusCode :: " + pEquipStatus.getStatusCode());
		}
		String lId = equipStatusDao.updateEquipStatus(pEquipStatus);
		return lId;
	}

	@Override
	public String deleteEquipStatus(EquipStatus pEquipStatus) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager -----------> deleteEquipStatus ");
		}
		if (pEquipStatus == null) {
			return null;
		}
		if (logger.isInfoEnabled()) {
			logger.info("EquipId :: " + pEquipStatus.getEquipConfig().getEquipId() + "statusCode :: " + pEquipStatus.getStatusCode());
		}
		String lId = equipStatusDao.deleteEquipStatus(pEquipStatus);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager -----------> deleteEquipStatus :: EquipId :: " + pEquipStatus.getEquipConfig().getEquipId() + "statusCode :: " + pEquipStatus.getStatusCode());
		}
		return lId;
	}

	@Override
	public EquipStatus findEquipStatusById(String pId) {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager ----------> findEquipStatusById :: pId " + pId);
		}
		if (pId == null || pId.trim().length() == 0) {
			return null;
		}
		EquipStatus lEquipStatus = equipStatusDao.findEquipStatusById(pId);
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager ----------> findEquipStatusById :: pId " + pId);
		}
		return lEquipStatus;
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
	public List<EquipStatus> getAllEquipStatus() {
		if (logger.isInfoEnabled()) {
			logger.info("Inside Manager ----------> getAllEquipStatus ");
		}
		List<EquipStatus> lEquipStatusList = equipStatusDao.getAllEquipStatus();
		if (lEquipStatusList != null) {
			if (logger.isInfoEnabled()) {
				logger.info("EquipStatusList Size :: " + lEquipStatusList.size());
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("Exit Manager ----------> getAllEquipStatus ");
		}
		return lEquipStatusList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EquipStatusDto generateEquipStatusForClearStatus(TechnicalAlarmDto pTechAlarmDto) throws Exception {
		logger.debug("Inside Manager ----------> generateEquipStatusForClearStatus :: EquipType :: " + pTechAlarmDto.getEquipType());
		logger.info("Alarm Code :: " + pTechAlarmDto.getAlarmCode());

		WebApplicationContext springContext = (WebApplicationContext)ApplicationContextProvider.getApplicationContext();
		//Hashtable<String, String> lEquipStatusConfigHtable = (Hashtable<String, String>)springContext.getServletContext().getAttribute("EQUIP_STATUS_CONFIG");
		String lOpeFlag = (String)springContext.getServletContext().getAttribute("OPE_FLAG");

		EquipStatusDto lEquipStatusDto = null;
		String lEquipType = pTechAlarmDto.getEquipType();
		/*if (lEquipStatusConfigHtable == null) {
			lEquipStatusConfigHtable = equipStatusDao.getEquipStatusConfig();
			springContext.getServletContext().setAttribute("EQUIP_STATUS_CONFIG", lEquipStatusConfigHtable);
		}*/
		String lStatusCode = Constants.OPE_STATE;//(String)lEquipStatusConfigHtable.get(lEquipType);
		int lTechAlarmCount = technicalAlarmDao.findTechnicalAlarmCount(pTechAlarmDto.getEquipId());


		boolean lGenerateEquipStatus = false;
		if (lOpeFlag.equals(Constants.DISABLE_OPE_FLAG)) {
			String[] lEquipTypeArray = Constants.OPE_EQUIP_TYPE_ARRAY;
			//int[] lAlarmCodeArray = {1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1};
			int[] lAlarmCodeArray = Constants.OPE_EQUIP_STATUS_ARRAY;
			
			for (int k = 0; k < lEquipTypeArray.length; k++) {
				logger.debug("lEquipType :: " + lEquipType);
				if (lEquipTypeArray[k].equals(lEquipType)) {
					logger.debug("lAlarmCodeArray[k] :: " + lAlarmCodeArray[k]);
					logger.debug("pTechAlarmDto.getAlarmCode() :: " + pTechAlarmDto.getAlarmCode());
					if (lAlarmCodeArray[k] == pTechAlarmDto.getAlarmCode()) {
						lGenerateEquipStatus = true;
						break;
					} else {
						lGenerateEquipStatus = false;
					}
					break;
				} else {
					lGenerateEquipStatus = true;
				}
			}
		} else {
			if (lTechAlarmCount == 0) {
				lGenerateEquipStatus = true;
			}
		}
		logger.info("GenerateEquipStatus :: " + lGenerateEquipStatus);

		if (lGenerateEquipStatus) {
			logger.info("No Technical Alarm Raised available for the Equipment... So Generating Equipment Status as Normal.....");

			EquipStatus lEquipStatus = equipStatusDao.findEquipStatusByEquipIdAndStatusCode(pTechAlarmDto.getEquipId(), lStatusCode);
			/***** Changed by Grace 18/12/19 *****/
			if (lEquipStatus != null) { // Added as lEquipStatus may be null - Grace 18/12/19
				lEquipStatus.setStatus(Constants.EQUIP_STATUS_NORMAL);
				lEquipStatus.setDateTime(pTechAlarmDto.getStartDate());
				equipStatusDao.updateEquipStatus(lEquipStatus);
			} else { // Added to log down database error - Grace 18/12/19
				logger.error("EquipStatus for equipID :"+pTechAlarmDto.getEquipId()+" is not found in database");	
			}		
			/*************************************/

			if (pTechAlarmDto.getEquipId().equals(Constants.NMS_EQUIP_ID)) {
				for (String lArgStatusCode : Constants.NMS_STATE_CODE_ARRY) {
					EquipStatus lEquipStatusArg = equipStatusDao.findEquipStatusByEquipIdAndStatusCode(Constants.NMS_EQUIP_ID, lArgStatusCode);
					lEquipStatusArg.setStatus(Constants.EQUIP_STATUS_NORMAL);
					lEquipStatusArg.setDateTime(pTechAlarmDto.getStartDate());
					equipStatusDao.updateEquipStatus(lEquipStatusArg);
				}
				SchedulerConfig schedulerConfig = SchedulerConfig.getInstance();
				schedulerConfig.setNmsAlarmRaised(false);
			}

			/***** Changed by Grace 18/12/19 *****/
			if (lEquipStatus != null) { // Added as lEquipStatus may be null - Grace 18/12/19
				lEquipStatusDto = new EquipStatusDto(pTechAlarmDto.getSystemId(), lEquipStatus.getDateTime(), lEquipType, pTechAlarmDto.getEquipId(), lStatusCode, lEquipStatus.getStatus());
			}		
			/*************************************/

			//NOTE :: TRIGGER WILL BE CALLED TO SAVE HISTEQUIPSTATUS WHEN THERE IS CHANGE IN THE STATUS OF EQUIPMENT
			//equipStatusDao.saveHistEquipStatus(messageConverterUtil.generateHistEquipStatusEntity(lEquipStatus));

			/*CacheManager cacheManager = CacheManager.create();
			Cache cache = cacheManager.getCache("equipStatusCache");
			logger.info("Cache :: " + cache);
			logger.info("Cache :: " + cache.getSize());*/

			Hashtable<Object, Object> lEquipStatusHtable = (Hashtable<Object, Object>)springContext.getServletContext().getAttribute("EQUIP_STATUS");
			if (lEquipStatusHtable == null) {
				lEquipStatusHtable = equipStatusDao.getEquipStatus();
			}
			EquipStatusId lEquipStatusId = new EquipStatusId(pTechAlarmDto.getEquipId(), lStatusCode);
			lEquipStatusHtable.put(lEquipStatusId, Constants.EQUIP_STATUS_NORMAL);
			springContext.getServletContext().setAttribute("EQUIP_STATUS", lEquipStatusHtable);

			SchedulerConfigNetwork schedulerConfigNetwork = SchedulerConfigNetwork.getInstance();
			Map<String, Integer> pingStateMap	= schedulerConfigNetwork.getPingStateMap();
			if (pingStateMap.containsKey(pTechAlarmDto.getEquipId())) {
				pingStateMap.put(pTechAlarmDto.getEquipId(), Constants.EQUIP_STATUS_NORMAL);
			}
		} else {
			logger.info("Other Technical Alarm raised available for the Equipment ... Hence not generateing Equipment Status.....");
		}

		SchedulerConfigUps schedulerConfigUps = SchedulerConfigUps.getInstance();
		Map<String, Integer> upsMap = schedulerConfigUps.getUpsMap();
		String lKey = pTechAlarmDto.getEquipId()+"_"+pTechAlarmDto.getAlarmCode();
		if (upsMap.containsKey(lKey)) {
			upsMap.put(lKey, Constants.EQUIP_STATUS_NORMAL);
			if (pTechAlarmDto.getEquipId().equals(Constants.UPS_01_EQUIP_ID) || pTechAlarmDto.getEquipId().equals(Constants.FOU_01_EQUIP_ID)) {
				UpsArgentStatus lUpsArgentStatus = new UpsArgentStatus();
				lUpsArgentStatus.setEquipId(pTechAlarmDto.getEquipId());
				lUpsArgentStatus.setAlarmCode(pTechAlarmDto.getAlarmCode());
				lUpsArgentStatus.setDateTime(pTechAlarmDto.getStartDate());
				lUpsArgentStatus.setStatus(Constants.EQUIP_STATUS_NORMAL);
				equipStatusDao.updateUpsArgentStatus(lUpsArgentStatus);
			}
		}

		logger.debug("Exit Manager ----------> generateEquipStatusForClearStatus.....");
		return lEquipStatusDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EquipStatusDto generateEquipStatusForAlarmRaised(TechnicalAlarmDto pTechAlarmDto) throws Exception {
		logger.info("Inside Manager ----------> generateEquipStatusForAlarmRaised :: EquipType :: " + pTechAlarmDto.getEquipType());

		WebApplicationContext springContext = (WebApplicationContext)ApplicationContextProvider.getApplicationContext();
		//Hashtable<String, String> lEquipStatusConfigHtable = (Hashtable<String, String>)springContext.getServletContext().getAttribute("EQUIP_STATUS_CONFIG");		
		String lOpeFlag = (String)springContext.getServletContext().getAttribute("OPE_FLAG");

		EquipStatusDto lEquipStatusDto = null;

		String lEquipType = pTechAlarmDto.getEquipType();
		/*if (lEquipStatusConfigHtable == null) {
			lEquipStatusConfigHtable = equipStatusDao.getEquipStatusConfig();
			springContext.getServletContext().setAttribute("EQUIP_STATUS_CONFIG", lEquipStatusConfigHtable);
		}*/
		//logger.debug("lEquipStatusConfigHtable :: " + lEquipStatusConfigHtable);
		String lStatusCode = Constants.OPE_STATE;//(String)lEquipStatusConfigHtable.get(lEquipType);
		boolean lGenerateEquipStatus = false;
		if (lOpeFlag.equals(Constants.DISABLE_OPE_FLAG)) {
			String[] lEquipTypeArray = Constants.OPE_EQUIP_TYPE_ARRAY;
			//int[] lAlarmCodeArray = {1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1};
			int[] lAlarmCodeArray = Constants.OPE_EQUIP_STATUS_ARRAY;
			
			for (int k = 0; k < lEquipTypeArray.length; k++) {
				logger.debug("lEquipType :: " + lEquipType);
				if (lEquipTypeArray[k].equals(lEquipType)) {
					logger.debug("lAlarmCodeArray[k] :: " + lAlarmCodeArray[k]);
					logger.debug("pTechAlarmDto.getAlarmCode() :: " + pTechAlarmDto.getAlarmCode());
					if (lAlarmCodeArray[k] == pTechAlarmDto.getAlarmCode()) {
						lGenerateEquipStatus = true;
						break;
					} else {
						lGenerateEquipStatus = false;
					}
					break;
				} else {
					lGenerateEquipStatus = true;
				}
			}
		} else {
			lGenerateEquipStatus = true;
		}		
		logger.info("GenerateEquipStatus :: " + lGenerateEquipStatus);
		if (lGenerateEquipStatus) {
			logger.info("EquipId :: " + pTechAlarmDto.getEquipId() + " StatusCode :: " + lStatusCode);
			EquipStatus lEquipStatus = equipStatusDao.findEquipStatusByEquipIdAndStatusCode(pTechAlarmDto.getEquipId(), lStatusCode);
			if (lEquipStatus != null) {
				logger.info("New Technical Alarm Raised for the Equipment... So Generating Equipment Status.....");
				int lStatus = lEquipStatus.getStatus();
				logger.info("Status :: " + lStatus);
				if (lStatus == Constants.EQUIP_STATUS_NORMAL) {
					Hashtable<Object, Object> lEquipStatusHtable = (Hashtable<Object, Object>)springContext.getServletContext().getAttribute("EQUIP_STATUS");
					if (lEquipStatusHtable == null) {
						lEquipStatusHtable = equipStatusDao.getEquipStatus();
					}
					EquipStatusId lEquipStatusId = new EquipStatusId(pTechAlarmDto.getEquipId(), lStatusCode);
					lEquipStatusHtable.put(lEquipStatusId, Constants.EQUIP_STATUS_NG);
					springContext.getServletContext().setAttribute("EQUIP_STATUS", lEquipStatusHtable);

					lEquipStatus.setStatus(Constants.EQUIP_STATUS_NG);
					lEquipStatus.setDateTime(pTechAlarmDto.getStartDate());
					equipStatusDao.updateEquipStatus(lEquipStatus);

					lEquipStatusDto = new EquipStatusDto(pTechAlarmDto.getSystemId(), lEquipStatus.getDateTime(), lEquipType, pTechAlarmDto.getEquipId(), lStatusCode, lEquipStatus.getStatus());
					//NOTE :: TRIGGER WILL BE CALLED TO SAVE HISTEQUIPSTATUS WHEN THERE IS CHANGE IN THE STATUS OF EQUIPMENT
					//equipStatusDao.saveHistEquipStatus(messageConverterUtil.generateHistEquipStatusEntity(lEquipStatus));
				}
				/***** Changed by Grace 18/12/19 *****/
			} else { // Added to log down database error - Grace 18/12/19
				logger.error("EquipStatus for equipID :"+pTechAlarmDto.getEquipId()+" is not found in database");	
				/*************************************/
			}
		} else {
			logger.info("OPE Flag disbaled ... Hence not generateing Equipment Status.....");
		}
		logger.debug("Exit Manager----------> generateEquipStatusForAlarmRaised.....");
		return lEquipStatusDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EquipStatusDto> processEquipStatus(EquipStatusDtoList pEquipStatusDtoList) {
		logger.debug("Inside Manager ----------> processEquipStatus :: EquipStatusDtoList size :: " + pEquipStatusDtoList.getDtoList().size());
		if (pEquipStatusDtoList.getDtoList().size() == 0) {
			return null;
		}
		WebApplicationContext springContext = (WebApplicationContext)ApplicationContextProvider.getApplicationContext();
		Hashtable<Object, Object> lEquipStatusHtable = (Hashtable<Object, Object>)springContext.getServletContext().getAttribute("EQUIP_STATUS");
		if (lEquipStatusHtable == null) {
			lEquipStatusHtable = equipStatusDao.getEquipStatus();
			springContext.getServletContext().setAttribute("EQUIP_STATUS", lEquipStatusHtable);
		}
		logger.debug(":: :: lEquipStatusHtable :: ::" + lEquipStatusHtable);
		List<EquipStatusDto> lChangedEquipStatusList = new ArrayList<EquipStatusDto>();
		try {
			for (EquipStatusDto lEquipStatusDto : pEquipStatusDtoList.getDtoList()) {
				String lEquipId = lEquipStatusDto.getEquipId();
				String lStatusCode = lEquipStatusDto.getStatusCode();
				int lStatus = lEquipStatusDto.getStatus();
				if (lStatusCode.equals(Constants.VMS_STAT)) {
					lEquipStatusDto.setStatusCode(Constants.OPE_STATE);
					lStatusCode = Constants.OPE_STATE;
					processVmsStat(lEquipStatusDto);
				}
				EquipStatusId lKey = new EquipStatusId(lEquipId, lStatusCode);
				Integer lTempStatus = (Integer)lEquipStatusHtable.get(lKey);
				if (lTempStatus == null) {
					EquipStatus lEquipStatus = messageConverterUtil.convertEquipStatusDtoToEntity(lEquipStatusDto);
					equipStatusDao.saveEquipStatus(lEquipStatus);
					equipStatusDao.saveHistEquipStatus(messageConverterUtil.generateHistEquipStatusEntity(lEquipStatus));
					lChangedEquipStatusList.add(lEquipStatusDto);

					lEquipStatusHtable.put(lKey, lStatus);
					springContext.getServletContext().setAttribute("EQUIP_STATUS", lEquipStatusHtable);
				} else {					
					if (lTempStatus != lStatus) {
						EquipStatus lEquipStatus = equipStatusDao.findEquipStatusByEquipIdAndStatusCode(lEquipId, lStatusCode);
						lEquipStatus.setStatus(lStatus);
						lEquipStatus.setDateTime(lEquipStatusDto.getDateTime());
						equipStatusDao.updateEquipStatus(lEquipStatus);
						//NOTE :: TRIGGER WILL BE CALLED TO SAVE HISTEQUIPSTATUS WHEN THERE IS CHANGE IN THE STATUS OF EQUIPMENT

						lEquipStatusHtable.put(lKey, lStatus);
						springContext.getServletContext().setAttribute("EQUIP_STATUS", lEquipStatusHtable);

						lChangedEquipStatusList.add(lEquipStatusDto);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception in processing Equip Status", e);
		}
		logger.debug("Exit Manager ----------> processEquipStatus :: Changed EquipStatusDtoList size :: " + lChangedEquipStatusList.size());
		return lChangedEquipStatusList;
	}

	public void processVmsStat(EquipStatusDto pEquipStatusDto) {
		if (pEquipStatusDto.getStatus() == 2) {
			int lCount = technicalAlarmDao.findTechnicalAlarmCount(pEquipStatusDto.getEquipId());
			if (lCount != 0) {
				pEquipStatusDto.setStatus(0);
			}
		}
	}

	/***** Changed by Grace 18/12/19 *****/
	@SuppressWarnings("unchecked")
	@Override
	public EquipStatusDto generateTPNDEquipStatusForClearStatus(TechnicalAlarmDto pTechAlarmDto) throws Exception {
		logger.debug("Inside Manager ----------> generateTPNDEquipStatusForClearStatus :: EquipType :: " + pTechAlarmDto.getEquipType());
		logger.info("Alarm Code :: " + pTechAlarmDto.getAlarmCode());

		WebApplicationContext springContext = (WebApplicationContext)ApplicationContextProvider.getApplicationContext();
		String lOpeFlag = (String)springContext.getServletContext().getAttribute("OPE_FLAG");

		EquipStatusDto lEquipStatusDto = null;
		String lEquipType = pTechAlarmDto.getEquipType();
		String lStatusCode = Constants.OPE_STATE;
		int lTechAlarmCount = technicalAlarmDao.findTechnicalAlarmCount(pTechAlarmDto.getEquipId());

		boolean lGenerateEquipStatus = false;
		if (lOpeFlag.equals(Constants.DISABLE_OPE_FLAG)) {
			lGenerateEquipStatus = true;
		} else {
			if (lTechAlarmCount == 0) {
				lGenerateEquipStatus = true;
			}
		}
		logger.info("generateTPNDEquipStatusForClearStatus :: " + lGenerateEquipStatus);

		if (lGenerateEquipStatus) {
			logger.info("No Technical Alarm Raised available for the Equipment... So Generating Equipment Status as Normal.....");

			EquipStatus lEquipStatus = equipStatusDao.findEquipStatusByEquipIdAndStatusCode(pTechAlarmDto.getEquipId(), lStatusCode);
			if ((lEquipStatus != null) && (lEquipStatus.getStatus() != Constants.EQUIP_STATUS_NORMAL)) {
				lEquipStatus.setStatus(Constants.EQUIP_STATUS_NORMAL);
				lEquipStatus.setDateTime(pTechAlarmDto.getStartDate());
				equipStatusDao.updateEquipStatus(lEquipStatus);

				lEquipStatusDto = new EquipStatusDto(pTechAlarmDto.getSystemId(), lEquipStatus.getDateTime(), lEquipType, pTechAlarmDto.getEquipId(), lStatusCode, lEquipStatus.getStatus());

				Hashtable<Object, Object> lEquipStatusHtable = (Hashtable<Object, Object>)springContext.getServletContext().getAttribute("EQUIP_STATUS");
				if (lEquipStatusHtable == null) {
					lEquipStatusHtable = equipStatusDao.getEquipStatus();
				}
				EquipStatusId lEquipStatusId = new EquipStatusId(pTechAlarmDto.getEquipId(), lStatusCode);
				lEquipStatusHtable.put(lEquipStatusId, Constants.EQUIP_STATUS_NORMAL);
				springContext.getServletContext().setAttribute("EQUIP_STATUS", lEquipStatusHtable);

				SchedulerConfigNetwork schedulerConfigNetwork = SchedulerConfigNetwork.getInstance();
				Map<String, Integer> pingStateMap	= schedulerConfigNetwork.getPingStateMap();
				if (pingStateMap.containsKey(pTechAlarmDto.getEquipId())) {
					pingStateMap.put(pTechAlarmDto.getEquipId(), Constants.EQUIP_STATUS_NORMAL);
				}
			} else {
				logger.info("generateTPNDEquipStatusForClearStatus :: Equipment Status not generated :"+pTechAlarmDto.getEquipId());
			}
		} else {
			logger.info("Other Technical Alarm raised available for the Equipment ... Hence not generateing Equipment Status.....");
		}

		SchedulerConfigUps schedulerConfigUps = SchedulerConfigUps.getInstance();
		Map<String, Integer> upsMap = schedulerConfigUps.getUpsMap();
		String lKey = pTechAlarmDto.getEquipId()+"_"+pTechAlarmDto.getAlarmCode();
		if (upsMap.containsKey(lKey)) {
			upsMap.put(lKey, Constants.EQUIP_STATUS_NORMAL);
		}

		logger.debug("Exit Manager ----------> generateTPNDEquipStatusForClearStatus.....");
		return lEquipStatusDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EquipStatusDto generateTPNDEquipStatusForAlarmRaised(TechnicalAlarmDto pTechAlarmDto) throws Exception {
		logger.info("Inside Manager ----------> generateTPNDEquipStatusForAlarmRaised :: EquipType :: " + pTechAlarmDto.getEquipType());

		WebApplicationContext springContext = (WebApplicationContext)ApplicationContextProvider.getApplicationContext();
		String lOpeFlag = (String)springContext.getServletContext().getAttribute("OPE_FLAG");

		EquipStatusDto lEquipStatusDto = null;

		String lEquipType = pTechAlarmDto.getEquipType();
		String lStatusCode = Constants.OPE_STATE;
		boolean lGenerateEquipStatus = false;
		if (lOpeFlag.equals(Constants.DISABLE_OPE_FLAG)) {
			//Do not need the below codes as TPN, MDB and TEL only has 1 alarm code
			//String[] lEquipTypeArray = TPNDConstants.OPE_TPND_TYPE_ARRAY;
			//int[] lAlarmCodeArray = {1, 1, 1};

			//for (int k = 0; k < lEquipTypeArray.length; k++) {
			//	logger.debug("lEquipType :: " + lEquipType);
			//	if (lEquipTypeArray[k].equals(lEquipType)) {
			//		logger.debug("lAlarmCodeArray[k] :: " + lAlarmCodeArray[k]);
			//		logger.debug("pTechAlarmDto.getAlarmCode() :: " + pTechAlarmDto.getAlarmCode());
			//		if (lAlarmCodeArray[k] == pTechAlarmDto.getAlarmCode()) {
			//			lGenerateEquipStatus = true;
			//			break;
			//		} else {
			//			lGenerateEquipStatus = false;
			//		}
			//		break;
			//	} else {
			//		lGenerateEquipStatus = true;
			//	}
			//}
			lGenerateEquipStatus = true;
		} else {
			lGenerateEquipStatus = true;
		}		
		logger.info("GenerateEquipStatus :: " + lGenerateEquipStatus);

		if (lGenerateEquipStatus) {			
			logger.info("EquipId :: " + pTechAlarmDto.getEquipId() + " StatusCode :: " + lStatusCode);
			EquipStatus lEquipStatus = equipStatusDao.findEquipStatusByEquipIdAndStatusCode(pTechAlarmDto.getEquipId(), lStatusCode);
			if (lEquipStatus != null) {
				logger.info("New Technical Alarm Raised for the Equipment... So Generating Equipment Status.....");
				int lStatus = lEquipStatus.getStatus();
				logger.info("Status :: " + lStatus);
				if (lStatus == Constants.EQUIP_STATUS_NORMAL) {
					Hashtable<Object, Object> lEquipStatusHtable = (Hashtable<Object, Object>)springContext.getServletContext().getAttribute("EQUIP_STATUS");
					if (lEquipStatusHtable == null) {
						lEquipStatusHtable = equipStatusDao.getEquipStatus();
					}
					EquipStatusId lEquipStatusId = new EquipStatusId(pTechAlarmDto.getEquipId(), lStatusCode);
					lEquipStatusHtable.put(lEquipStatusId, Constants.EQUIP_STATUS_NG);
					springContext.getServletContext().setAttribute("EQUIP_STATUS", lEquipStatusHtable);

					lEquipStatus.setStatus(Constants.EQUIP_STATUS_NG);
					lEquipStatus.setDateTime(pTechAlarmDto.getStartDate());
					equipStatusDao.updateEquipStatus(lEquipStatus);

					lEquipStatusDto = new EquipStatusDto(pTechAlarmDto.getSystemId(), lEquipStatus.getDateTime(), lEquipType, pTechAlarmDto.getEquipId(), lStatusCode, lEquipStatus.getStatus());
				}
			} else {
				logger.error("EquipStatus for equipID :"+pTechAlarmDto.getEquipId()+" is not found in database");
			}
		} else {
			logger.info("OPE Flag disbaled ... Hence not generateing Equipment Status.....");
		}
		logger.debug("Exit Manager----------> generateTPNDEquipStatusForAlarmRaised.....");
		return lEquipStatusDto;
	}
	/*************************************/

}