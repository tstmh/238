/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.caid;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stee.emas.cmh.service.CMHManager;
import com.stee.emas.common.dto.TrafficAlertClearDto;
import com.stee.emas.common.dto.TrafficAlertDto;
import com.stee.emas.common.entity.EquipConfig;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Builder class for Congestion Alert</p>
 * <p>This class is used to build caid logic and adding it in Congestion Queue
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Oct 23, 2013
 * @version 1.0
 * @since June 2, 2015
 * @version 2.0
 *
 */

@Component("congestionAlertBuilder")
public class CongestionAlertBuilder {
	
	private static Logger logger = LoggerFactory.getLogger(CongestionAlertBuilder.class);
	
	@Autowired
	CMHManager cmhManager;
	@Autowired
	CongestionBuffer congestionBuffer;

	private static CongestionAlertQueue congestionAlertQueue = CongestionAlertQueue.getInstance();
	
	public CongestionAlertBuilder() {
	}
	
	public void processCongestionAlert(TrafficAlertDto trafficAlertDto) {
		logger.info("Calling processCongestionAlert .....EquipId :: " + trafficAlertDto.getEquipId() + " .....LaneId :: " + trafficAlertDto.getLaneId());
		
		Map<CaidKeyBean, TrafficAlertDto> congestionTempMap	= congestionBuffer.getCongestionTempMap();
		Map<String, CaidLaneAlertObj> congestionHoldingMap	= congestionBuffer.getCongestionHoldingMap();
		Map<String, TrafficAlertDto> congestionMap			= congestionBuffer.getCongestionMap();
		
		String lEquipIdTemp = trafficAlertDto.getEquipId();
		int lLaneIdTemp = trafficAlertDto.getLaneId();
		CaidKeyBean lCaidKeyBean = new CaidKeyBean(lEquipIdTemp, lLaneIdTemp);
		
		//Check whether alert in congestionTempList
		if (congestionTempMap.containsKey(lCaidKeyBean)) {
			logger.info("EquipId + LaneId found in CongestionTempMap .....");
			congestionTempMap.remove(lCaidKeyBean);
		}
		
		//Check whether alert in congestionHoldingList
		if (congestionHoldingMap.containsKey(lEquipIdTemp)) {
			logger.info("CongestionHoldingMap contains EquipId ..... " + lEquipIdTemp);
			CaidLaneAlertObj lLaneAlertObj = congestionHoldingMap.get(lEquipIdTemp);
			logger.info("lLaneAlertObj :::: " + lLaneAlertObj);
			if (lLaneAlertObj != null) {
				LinkedHashMap<Integer, TrafficAlertDto> lLaneAlertMap = lLaneAlertObj.getLaneAlertMap();
				if (!lLaneAlertMap.containsKey(lLaneIdTemp)) {					
					lLaneAlertMap.put(lLaneIdTemp, trafficAlertDto);
					logger.info("lLaneAlertMap :: " + lLaneAlertMap);
				}
			}
		} else {
			logger.info("CongestionHoldingMap does not contains EquipId ..... " + lEquipIdTemp);
			LinkedHashMap<Integer, TrafficAlertDto> lLaneAlertMap = new LinkedHashMap<Integer, TrafficAlertDto>();
			lLaneAlertMap.put(lLaneIdTemp, trafficAlertDto);
			logger.info("lLaneAlertMap :: " + lLaneAlertMap);
			
			CaidLaneAlertObj lLaneAlertObj = new CaidLaneAlertObj(lLaneAlertMap);
			//Add Alert to CongestionHolding List
			congestionHoldingMap.put(lEquipIdTemp, lLaneAlertObj);
		}
		
		List<?> lAdjacentDetectorList = cmhManager.getAdjacentDetectors(lEquipIdTemp);
		
		String lFirstEquipId = new String();
		String lSecondEquipId = new String();
		
		for (Iterator<?> it=(Iterator<?>) lAdjacentDetectorList.iterator();it.hasNext();) {
			Object[] row = (Object[]) it.next();
			lFirstEquipId = (String)row[0];
			lSecondEquipId = (String)row[1];
		}
		if (lFirstEquipId == null) {
			lFirstEquipId = new String();
		}
		if (lSecondEquipId == null) {
			lSecondEquipId = new String();
		}
		
		logger.info("lFirstEquipId :: " + lFirstEquipId);
		logger.info("lSecondEquipId :: " + lSecondEquipId);
		
		logger.info("congestionHoldingMap :: " + congestionHoldingMap);
		
		if (congestionHoldingMap.containsKey(lFirstEquipId)) { //Alert's 1st equip in congestionHoldingList
			logger.info("Alert's 1st equip in congestionHoldingList.....");
			if (congestionHoldingMap.containsKey(lSecondEquipId)) { //Alert's 2nd equip in congestionHoldingList
				logger.info("Alert's 2nd equip in congestionHoldingList.....");
				if (!congestionMap.containsKey(lFirstEquipId)) { //Alert's 1st equip in congestion list
					logger.info("Alert's 1st equip in congestion list.....");
					CaidLaneAlertObj lLaneAlertObj_1Equip = congestionHoldingMap.get(lFirstEquipId);
					
					LinkedHashMap<Integer, TrafficAlertDto> lLinkedHashMap = lLaneAlertObj_1Equip.getLaneAlertMap();
					for (int key : lLinkedHashMap.keySet()) {
						logger.info("key :::::: " + key);
						TrafficAlertDto lTrafficAlertDto_1Equip = lLaneAlertObj_1Equip.getLaneAlertMap().get(key); //with earliest detection time
						logger.info("lTrafficAlertDto_1Equip :::::: " + lTrafficAlertDto_1Equip);
						congestionAlertQueue.add(lTrafficAlertDto_1Equip);
						congestionMap.put(lFirstEquipId, lTrafficAlertDto_1Equip);
						break;
					}
					//TrafficAlertDto lTrafficAlertDto_1Equip = lLaneAlertObj_1Equip.getLaneAlertMap().get(1); //with earliest detection time
					//logger.info("lTrafficAlertDto_1Equip :::::: " + lTrafficAlertDto_1Equip);
					//congestionAlertQueue.add(lTrafficAlertDto_1Equip);
					//congestionMap.put(lFirstEquipId, lTrafficAlertDto_1Equip);
				}
				if (!congestionMap.containsKey(lEquipIdTemp)) {
					logger.info("trafficAlertDto :::::: " + trafficAlertDto);
					congestionAlertQueue.add(trafficAlertDto);
					congestionMap.put(lEquipIdTemp, trafficAlertDto);
				}
			} else { //Alert's 2nd equip not in congestionHoldingList
				logger.info("Alert's 2nd equip not in congestionHoldingList.....");
				if (!congestionMap.containsKey(lFirstEquipId)) {
					CaidLaneAlertObj lLaneAlertObj_1Equip = congestionHoldingMap.get(lFirstEquipId);
					
					LinkedHashMap<Integer, TrafficAlertDto> lLinkedHashMap = lLaneAlertObj_1Equip.getLaneAlertMap();
					for (int key : lLinkedHashMap.keySet()) {
						logger.info("key :::::: " + key);
						TrafficAlertDto lTrafficAlertDto_1Equip = lLaneAlertObj_1Equip.getLaneAlertMap().get(key); //with earliest detection time
						logger.info("lTrafficAlertDto_1Equip :::::: " + lTrafficAlertDto_1Equip);
						congestionAlertQueue.add(lTrafficAlertDto_1Equip);
						congestionMap.put(lFirstEquipId, lTrafficAlertDto_1Equip);
						break;
					}					
					/*TrafficAlertDto lTrafficAlertDto_1Equip = lLaneAlertObj_1Equip.getLaneAlertMap().get(1); //with earliest detection time
					logger.info("lTrafficAlertDto_1Equip :::::: " + lTrafficAlertDto_1Equip);
					congestionAlertQueue.add(lTrafficAlertDto_1Equip);
					congestionMap.put(lFirstEquipId, lTrafficAlertDto_1Equip);*/
				}
			}
		} else if (congestionHoldingMap.containsKey(lSecondEquipId)) { //Alert's 2nd equip in congestionHoldingList
			logger.info("Alert's 2nd equip in congestionHoldingList.....");
			if (!congestionMap.containsKey(lEquipIdTemp)) {
				logger.info("trafficAlertDto :::::: " + trafficAlertDto);
				congestionAlertQueue.add(trafficAlertDto);
				congestionMap.put(lEquipIdTemp, trafficAlertDto);
			}
		}
		
		logger.info("congestionTempMap :: " + congestionTempMap);
		logger.info("CongestionHoldingMap :: " + congestionHoldingMap);
		logger.info("congestionMap :: " + congestionMap);
	}
	/*public void processCongestionAlert(TrafficAlertDto trafficAlertDto) {
		logger.info("Calling processCongestionAlert ....." + trafficAlertDto.getEquipId());

		Map<String, TrafficAlertDto>	congestionHoldingMap = congestionBuffer.getCongestionHoldingMap();	
		Map<String, Boolean> 			postHoldingFlagMap 		= congestionBuffer.getPostHoldingFlagMap();
		Map<String, Boolean> 			clearPendingFlagMap 	= congestionBuffer.getClearPendingFlagMap();		
		
		String lEquipId = trafficAlertDto.getEquipId();
		
		//Check whether alert in congestionHoldingList		
		if (congestionHoldingMap.containsKey(lEquipId)) {
			logger.info("EquipId found in CongestionHoldingMap .....");
			postHoldingFlagMap.put(lEquipId, true);
			//build adjacent buffer based on equipId from traffic alert querying database
			String lAdjacentBuffer = populateAdjacentEquipBuffer(lEquipId);
			logger.info("lAdjacentBuffer ....." + lAdjacentBuffer);
			if (lAdjacentBuffer.startsWith("0")) { //No Adjacent Alert
				congestionPostHoldingMap.put(trafficAlertDto.getEquipId(), trafficAlertDto);
			} else if (lAdjacentBuffer.startsWith("1")) { //One Adjacent Alert -> 1,1:equipId ; 1,2:equipId
				logger.info("Have One Adjacent Alert .....");
				StringTokenizer stok = new StringTokenizer(lAdjacentBuffer, ",");
				@SuppressWarnings("unused")
				String lFirstToken = stok.nextToken();
				String lSecondToken = stok.nextToken();
				
				StringTokenizer stok1 = new StringTokenizer(lSecondToken, ":");
				String lAlertPosition = stok1.nextToken();
				String lAdjacentEquipId = stok1.nextToken();
				if (lAlertPosition.equals("1")) {  	//Alert is 1st equip in group	
					logger.info("Alert is 1st Equip in Group ..... ");
					boolean lPostHoldingFlag = postHoldingFlagMap.get(lAdjacentEquipId);
					boolean lClearPendingFlag = clearPendingFlagMap.get(lAdjacentEquipId);
					if (lPostHoldingFlag || lClearPendingFlag) {
						trafficAlertDto.setAlertDate(new Date()); //Setting alertDate when adding it in queue						
						congestionAlertQueue.add(trafficAlertDto);
						logger.info("Adding Alert into CongestionAlertQueue .....AlertId :: " + trafficAlertDto.getAlertId() + "EquipId :: " + trafficAlertDto.getEquipId());
						postHoldingFlagMap.put(lEquipId, false);
						clearPendingFlagMap.put(lEquipId, true);
					}
				} else if (lAlertPosition.equals("2")) { //Alert is 2nd equip in group
					logger.info("Alert is 2nd Equip in Group ..... ");
					boolean lPostHoldingFlag = postHoldingFlagMap.get(lAdjacentEquipId);
					boolean lClearPendingFlag = clearPendingFlagMap.get(lAdjacentEquipId);
					if (lPostHoldingFlag || lClearPendingFlag) {
						TrafficAlertDto lAdjacentTrafficAlertDto = congestionHoldingMap.get(lAdjacentEquipId);
						lAdjacentTrafficAlertDto.setAlertDate(new Date()); //Setting alertDate when adding it in queue
						congestionAlertQueue.add(lAdjacentTrafficAlertDto);
						logger.info("Adding Adjacent Alert into CongestionAlertQueue .....AlertId :: " + lAdjacentTrafficAlertDto.getAlertId() + "EquipId :: " + lAdjacentTrafficAlertDto.getEquipId());
						postHoldingFlagMap.put(lAdjacentTrafficAlertDto.getEquipId(), false);
						clearPendingFlagMap.put(lAdjacentTrafficAlertDto.getEquipId(), true);
					}
				}
			} else if (lAdjacentBuffer.startsWith("2")) { //Two Adjacent Alert -> 2,equipId1,equipId2
				logger.info("Have Two Adjacent Alert ..... ");
				StringTokenizer stok = new StringTokenizer(lAdjacentBuffer, ",");
				stok.nextToken();
				String lFirstEquip1Group = stok.nextToken();
				String lSecondEquip2Group = stok.nextToken();
				
				logger.info("lFirstEquip1Group :: " + lFirstEquip1Group);
				logger.info("lSecondEquip2Group :: " + lSecondEquip2Group);
				
				boolean lPostHoldingFlag_1Grp = postHoldingFlagMap.get(lFirstEquip1Group);
				if (lPostHoldingFlag_1Grp) {
					TrafficAlertDto lAlert1Equip1Grp = congestionHoldingMap.get(lFirstEquip1Group);
					lAlert1Equip1Grp.setAlertDate(new Date()); //Setting alertDate when adding it in queue
					congestionAlertQueue.add(lAlert1Equip1Grp);
					logger.info("Adding Adjacent Alert into CongestionAlertQueue .....AlertId :: " + lAlert1Equip1Grp.getAlertId() + "EquipId :: " + lAlert1Equip1Grp.getEquipId());

					postHoldingFlagMap.put(lFirstEquip1Group, false);
					clearPendingFlagMap.put(lFirstEquip1Group, true);
				}
				boolean lPostHoldingFlag_2Grp = postHoldingFlagMap.get(lSecondEquip2Group);
				boolean lClearPendingFlag_2Grp = clearPendingFlagMap.get(lSecondEquip2Group);
				if (lPostHoldingFlag_2Grp || lClearPendingFlag_2Grp) {
					trafficAlertDto.setAlertDate(new Date()); //Setting alertDate when adding it in queue
					congestionAlertQueue.add(trafficAlertDto);
					logger.info("Adding Adjacent into CongestionAlertQueue .....AlertId :: " + trafficAlertDto.getAlertId() + "EquipId :: " + trafficAlertDto.getEquipId());

					postHoldingFlagMap.put(trafficAlertDto.getEquipId(), false);
					clearPendingFlagMap.put(trafficAlertDto.getEquipId(), true);
				}
			}
		}
		logger.info("CongestionHoldingMap :: " + congestionHoldingMap);
		logger.info("postHoldingFlagMap :: " + postHoldingFlagMap);
		logger.info("clearPendingFlagMap :: " + clearPendingFlagMap);
	}*/
	
	public void processCongestionClear(TrafficAlertClearDto pTrafficAlertClearDto) {
		logger.info("Calling processCongestionClear .....EquipId :: " + pTrafficAlertClearDto.getEquipId() + " .....LaneId :: " + pTrafficAlertClearDto.getLaneId());
		
		Map<String, CaidLaneAlertObj> congestionHoldingMap	= congestionBuffer.getCongestionHoldingMap();
		Map<String, TrafficAlertDto> congestionMap			= congestionBuffer.getCongestionMap();
		
		String lEquipIdClear = pTrafficAlertClearDto.getEquipId();
		int lLaneIdClear = pTrafficAlertClearDto.getLaneId();
		//CaidKeyBean lCaidKeyBean = new CaidKeyBean(lEquipIdClear, lLaneIdClear);
		
		logger.info("congestionHoldingMap :: before remove ....." + congestionHoldingMap);
		
		if (congestionHoldingMap.containsKey(lEquipIdClear)) {
			CaidLaneAlertObj lLaneAlertObj = congestionHoldingMap.get(lEquipIdClear);
			logger.info("lLaneAlertObj :: " + lLaneAlertObj);
			if (lLaneAlertObj != null) {
				LinkedHashMap<Integer, TrafficAlertDto> lLaneAlertMap = lLaneAlertObj.getLaneAlertMap();
				logger.info("lLaneAlertMap :: " + lLaneAlertMap);
				if (lLaneAlertMap.containsKey(lLaneIdClear)) {
					lLaneAlertMap.remove(lLaneIdClear);
				}
				logger.info("lLaneAlertMap :: " + lLaneAlertMap.size());
				if (lLaneAlertMap.size() == 0) {
					congestionHoldingMap.remove(lEquipIdClear);
				}
			}
		}
		
		logger.info("congestionHoldingMap :: after remove ....." + congestionHoldingMap);
		
		if (!congestionHoldingMap.containsKey(lEquipIdClear)) {
			
			EquipConfig lEquipConfig = cmhManager.getEquipConfig(lEquipIdClear);
			String lNextEquipId = cmhManager.getNextEquipConfig(lEquipIdClear);
			if (lNextEquipId == null) {
				lNextEquipId = new String();
			}
			String lPrevEquipId = cmhManager.getPreviousEquipConfig(lEquipIdClear);
			if (lPrevEquipId == null) {
				lPrevEquipId = new String();
			}
			String lFirstEquipId = new String();
			String lSecondEquipId = new String();
			if (lEquipConfig.getDir() == 1) {
				lFirstEquipId = lNextEquipId;
				lSecondEquipId = lPrevEquipId;
			} else if (lEquipConfig.getDir() == 2) {
				lSecondEquipId = lNextEquipId;
				lFirstEquipId = lPrevEquipId;
			}
			
			logger.info("NextEquipId :: " + lFirstEquipId);
			logger.info("PrevEquipId :: " + lSecondEquipId);
			
			if (congestionHoldingMap.containsKey(lFirstEquipId)) { //Alert's 1st equip in congestionHoldingList
				logger.info("Alert's 1st equip in congestionHoldingList.....");
				if (!congestionHoldingMap.containsKey(lSecondEquipId)) { //Alert's 2nd equip in congestionHoldingList
					logger.info("Alert's 2nd equip not in congestionHoldingList.....");
					if (congestionMap.containsKey(lEquipIdClear)) {
						logger.info("1111111111");
						//CaidLaneAlertObj lLaneAlertObj = congestionHoldingMap.get(lEquipIdClear);
						//if (lLaneAlertObj != null) {
							//logger.info("2222222222");
							TrafficAlertDto lTrafficAlertDto = congestionMap.get(lEquipIdClear);
							lTrafficAlertDto.setEndDate(pTrafficAlertClearDto.getClearTime()); //TODO ::  what date to set here ?
							congestionAlertQueue.add(lTrafficAlertDto);
							logger.info("Adding Alert into CongestionAlertQueue .....AlertId :: " + lTrafficAlertDto.getAlertId() + " EquipId :: " + lTrafficAlertDto.getEquipId());
							congestionMap.remove(lEquipIdClear);
						//}
					}
				}
			} else { //Alert's 1st equip not in congestionHoldingList
				logger.info("Alert's 1st equip not in congestionHoldingList.....");
				if (congestionHoldingMap.containsKey(lSecondEquipId)) { //Alert's 2nd equip in congestionHoldingList
					logger.info("Alert's 2nd equip in congestionHoldingList.....");
					if (congestionMap.containsKey(lFirstEquipId)) {
						logger.info("3333333333");
						//CaidLaneAlertObj lLaneAlertObj_1Equip = congestionHoldingMap.get(lFirstEquipId);
						//if (lLaneAlertObj_1Equip != null) {
							//logger.info("4444444444");
							TrafficAlertDto lTrafficAlertDto_1Equip = congestionMap.get(lFirstEquipId);
							lTrafficAlertDto_1Equip.setEndDate(pTrafficAlertClearDto.getClearTime()); //TODO ::  what date to set here ?
							congestionAlertQueue.add(lTrafficAlertDto_1Equip);
							logger.info("Adding Alert into CongestionAlertQueue .....AlertId :: " + lTrafficAlertDto_1Equip.getAlertId() + " EquipId :: " + lTrafficAlertDto_1Equip.getEquipId());
							congestionMap.remove(lFirstEquipId);
						//}
					}
				} else { //Alert's 2nd equip not in congestionHoldingList
					logger.info("Alert's 2nd equip not in congestionHoldingList.....");
					if (congestionMap.containsKey(lEquipIdClear)) {
						logger.info("5555555555");
						//CaidLaneAlertObj lLaneAlertObj = congestionHoldingMap.get(lEquipIdClear);
						//if (lLaneAlertObj != null) {
							//logger.info("6666666666");
							TrafficAlertDto lTrafficAlertDto = congestionMap.get(lEquipIdClear);
							lTrafficAlertDto.setEndDate(pTrafficAlertClearDto.getClearTime()); //TODO ::  what date to set here ?
							congestionAlertQueue.add(lTrafficAlertDto);
							logger.info("Adding Alert into CongestionAlertQueue .....AlertId :: " + lTrafficAlertDto.getAlertId() + " EquipId :: " + lTrafficAlertDto.getEquipId());
							congestionMap.remove(lEquipIdClear);
						//}
					}
					
					if (congestionMap.containsKey(lFirstEquipId)) {
						logger.info("7777777777");
						//CaidLaneAlertObj lLaneAlertObj_1Equip = congestionHoldingMap.get(lFirstEquipId);
						//if (lLaneAlertObj_1Equip != null) {
							//logger.info("8888888888");
							TrafficAlertDto lTrafficAlertDto_1Equip = congestionMap.get(lFirstEquipId); //TODO :: do we have to get the earliest record or based on lane
							lTrafficAlertDto_1Equip.setEndDate(pTrafficAlertClearDto.getClearTime()); //TODO ::  what date to set here
							congestionAlertQueue.add(lTrafficAlertDto_1Equip);
							logger.info("Adding Alert into CongestionAlertQueue .....AlertId :: " + lTrafficAlertDto_1Equip.getAlertId() + " EquipId :: " + lTrafficAlertDto_1Equip.getEquipId());
							congestionMap.remove(lFirstEquipId);
						//}
					}
				}
			}
		}
		
		//logger.info("congestionTempMap :: " + congestionTempMap);
		logger.info("CongestionHoldingMap :: " + congestionHoldingMap);
		logger.info("congestionMap :: " + congestionMap);
	}
	
	/*public void processCongestionClear(TrafficAlertDto pTrafficAlertDto) {
		logger.info("Calling processCongestionClear ....." + pTrafficAlertDto.getEquipId());
		
		Map<String, TrafficAlertDto>	congestionHoldingMap    = congestionBuffer.getCongestionHoldingMap();
		Map<String, Boolean> 			postHoldingFlagMap 		= congestionBuffer.getPostHoldingFlagMap();
		Map<String, Boolean> 			clearPendingFlagMap 	= congestionBuffer.getClearPendingFlagMap();
		
		String lEquipId = pTrafficAlertDto.getEquipId();
		
		boolean lPostHoldingFlag = postHoldingFlagMap.get(lEquipId);
		boolean lClearPendingFlag = clearPendingFlagMap.get(lEquipId);
		if (!lPostHoldingFlag && !lClearPendingFlag) {
			String lAdjacentBuffer = populateAdjacentEquipBuffer(pTrafficAlertDto.getEquipId());
			logger.info("lAdjacentBuffer ....." + lAdjacentBuffer);
			if (lAdjacentBuffer.startsWith("0")) {//No Adjacent Alert
				logger.info("No Adjacent Alert .....remove alert from congestion holding list.....");
				removeMapEntry(lEquipId, pTrafficAlertDto.getAlertId());
				//congestionHoldingMap.remove(lEquipId);
			} else {
				logger.info("Have Adjacent Alert .....Do nothing.....");
			}
		} else {
			if (lPostHoldingFlag) {
				lPostHoldingFlag = false;
				postHoldingFlagMap.put(lEquipId, false);
			}
			String lAdjacentBuffer = populateAdjacentEquipBuffer(pTrafficAlertDto.getEquipId());
			logger.info("lAdjacentBuffer ....." + lAdjacentBuffer);
			if (lAdjacentBuffer.startsWith("0")) {//No Adjacent Alert
				logger.info("Have No Adjacent Alert .....");
				if (lClearPendingFlag) {
					pTrafficAlertDto.setEndDate(new Date());//Setting endDate when adding it in queue
					congestionAlertQueue.add(pTrafficAlertDto);
					logger.info("Adding Alert into CongestionAlertQueue .....AlertId :: " + pTrafficAlertDto.getAlertId() + " EquipId :: " + pTrafficAlertDto.getEquipId());
				}
				removeMapEntry(lEquipId, pTrafficAlertDto.getAlertId());
				//congestionHoldingMap.remove(lEquipId);
			} else if (lAdjacentBuffer.startsWith("1")) {//One Adjacent Alert -> 1,1:equipId ; 1,2:equipId
				logger.info("Have One Adjacent Alert .....");
				StringTokenizer stok = new StringTokenizer(lAdjacentBuffer, ",");
				@SuppressWarnings("unused")
				String lFirstToken = stok.nextToken();
				String lSecondToken = stok.nextToken();
					
				StringTokenizer stok1 = new StringTokenizer(lSecondToken, ":");
				String lAlertPosition = stok1.nextToken();
				String lAdjacentEquipId = stok1.nextToken();
				if (lAlertPosition.equals("1")) {	//Alert is 1st equip in group
					logger.info("Alert is 1st Equip in Group ..... ");
					boolean lAdjClearPendingFlag = clearPendingFlagMap.get(lAdjacentEquipId);
					boolean lAdjPostHoldingFlag = postHoldingFlagMap.get(lAdjacentEquipId);
					if (lAdjClearPendingFlag || lAdjPostHoldingFlag) {
						clearPendingFlagMap.put(lEquipId, false);
					} else {
						removeMapEntry(lEquipId, pTrafficAlertDto.getAlertId());
						//congestionHoldingMap.remove(lEquipId);
						pTrafficAlertDto.setEndDate(new Date()); //Setting endDate when adding it in queue
						congestionAlertQueue.add(pTrafficAlertDto);
						logger.info("Adding Alert into CongestionAlertQueue .....AlertId :: " + pTrafficAlertDto.getAlertId() + "EquipId :: " + pTrafficAlertDto.getEquipId());
						String lPrevAAdjEquipId = cmhManager.getPreviousEquipConfig(lAdjacentEquipId);
						String lNextAAdjEquipId = cmhManager.getNextEquipConfig(lAdjacentEquipId);

						if (!congestionHoldingMap.containsKey(lPrevAAdjEquipId) && !congestionHoldingMap.containsKey(lNextAAdjEquipId)) {
							if (!lAdjClearPendingFlag && !lAdjPostHoldingFlag) {
								TrafficAlertDto lAdjTrafficAlertDto = congestionHoldingMap.get(lAdjacentEquipId);
								removeMapEntry(lAdjacentEquipId, lAdjTrafficAlertDto.getAlertId());
							}
						}
					}
				} else if (lAlertPosition.equals("2")) { //Alert is 2nd equip in group
					logger.info("Alert is 2nd Equip in Group ..... ");
					boolean lAdjClearPendingFlag = clearPendingFlagMap.get(lAdjacentEquipId);
					if (!lAdjClearPendingFlag) {
						TrafficAlertDto lAdjacentTrafficAlertDto = congestionHoldingMap.get(lAdjacentEquipId);
						removeMapEntry(lAdjacentEquipId, lAdjacentTrafficAlertDto.getAlertId());
						//congestionHoldingMap.remove(lAdjacentEquipId);
						lAdjacentTrafficAlertDto.setEndDate(new Date()); //Setting endDate when adding it in queue
						congestionAlertQueue.add(lAdjacentTrafficAlertDto);
						logger.info("Adding Adjacent Alert into CongestionAlertQueue .....AlertId :: " + lAdjacentTrafficAlertDto.getAlertId() + " EquipId :: " + lAdjacentTrafficAlertDto.getEquipId());
						if (!lPostHoldingFlag && !lClearPendingFlag) {
							removeMapEntry(lEquipId, pTrafficAlertDto.getAlertId());
							//congestionHoldingMap.remove(lEquipId);
						}
					} else {
						clearPendingFlagMap.put(lEquipId, false);
					}
				}
			} else if (lAdjacentBuffer.startsWith("2")) { //Two Adjacent Alert e.g., 2,dtt_716030,dtt_716022
				logger.info("Have Two Adjacent Alert .....");
				StringTokenizer stok = new StringTokenizer(lAdjacentBuffer, ",");
				stok.nextToken();
				String lFirstEquip1Group = stok.nextToken();
				String lSecondEquip2Group = stok.nextToken();
					
				logger.info("lFirstEquip1Group :: " + lFirstEquip1Group);
				logger.info("lSecondEquip2Group :: " + lSecondEquip2Group);
				
				boolean clearPendingFlag1Equip1Grp = clearPendingFlagMap.get(lFirstEquip1Group);
				if (!clearPendingFlag1Equip1Grp) {
					clearPendingFlagMap.put(lEquipId, false);
					TrafficAlertDto lAlert1Equip1Grp = congestionHoldingMap.get(lFirstEquip1Group);
					removeMapEntry(lFirstEquip1Group, lAlert1Equip1Grp.getAlertId());
					//congestionHoldingMap.remove(lFirstEquip1Group);
					lAlert1Equip1Grp.setEndDate(new Date()); //Setting endDate when adding it in queue
					congestionAlertQueue.add(lAlert1Equip1Grp);
					logger.info("Adding Adjacent Alert into CongestionAlertQueue .....AlertId :: " + lAlert1Equip1Grp.getAlertId() + "EquipId :: " + lAlert1Equip1Grp.getEquipId());
				} else {
					clearPendingFlagMap.put(lEquipId, false);
				}
				boolean lClearPendingFlag2Equip2Grp = clearPendingFlagMap.get(lSecondEquip2Group);
				boolean lPostHoldingFlag2Equip2Grp = postHoldingFlagMap.get(lSecondEquip2Group);
				
				if (!lClearPendingFlag2Equip2Grp && !lPostHoldingFlag2Equip2Grp) {
					removeMapEntry(lEquipId, pTrafficAlertDto.getAlertId());
					//congestionHoldingMap.remove(lEquipId);
					pTrafficAlertDto.setEndDate(new Date()); //Setting endDate when adding it in queue
					congestionAlertQueue.add(pTrafficAlertDto);
					logger.info("Adding Alert into CongestionAlertQueue .....AlertId :: " + pTrafficAlertDto.getAlertId() + "EquipId :: " + pTrafficAlertDto.getEquipId());
					if (!lPostHoldingFlag2Equip2Grp) {
						TrafficAlertDto lAlert2Equip2Grp = congestionHoldingMap.get(lSecondEquip2Group);
						removeMapEntry(lSecondEquip2Group, lAlert2Equip2Grp.getAlertId());
						//congestionHoldingMap.remove(lSecondEquip2Group);
					}
				}
			}
		}
		logger.info("CongestionHoldingMap :: " + congestionHoldingMap);
		logger.info("postHoldingFlagMap :: " + postHoldingFlagMap);
		logger.info("clearPendingFlagMap :: " + clearPendingFlagMap);
	}*/
	
	/*public String populateAdjacentEquipBuffer(String pEquipId) {
		Map<String, TrafficAlertDto> congestionHoldingMap = congestionBuffer.getCongestionHoldingMap();
		EquipConfig lEquipConfig = cmhManager.getEquipConfig(pEquipId);
		
		StringBuffer lAdjacentUrl = new StringBuffer();
		try {
			String lNextEquipId = cmhManager.getNextEquipConfig(pEquipId);
			if (lNextEquipId == null) {
				lNextEquipId = new String();
			}
			String lPrevEquipId = cmhManager.getPreviousEquipConfig(pEquipId);
			if (lPrevEquipId == null) {
				lPrevEquipId = new String();
			}
			logger.info("lNextEquipId :: " + lNextEquipId);
			logger.info("lPrevEquipId :: " + lPrevEquipId);
			
			if (lEquipConfig.getDir() == 1) {
				if (congestionHoldingMap.containsKey(lNextEquipId) && congestionHoldingMap.containsKey(lPrevEquipId)) {
					lAdjacentUrl.append("2");
					lAdjacentUrl.append(",");
					lAdjacentUrl.append(lNextEquipId);
					lAdjacentUrl.append(",");
					lAdjacentUrl.append(lPrevEquipId);
				} else if (!congestionHoldingMap.containsKey(lNextEquipId) && (!congestionHoldingMap.containsKey(lPrevEquipId))) {
					lAdjacentUrl.append("0");
					lAdjacentUrl.append(",");
					lAdjacentUrl.append(0);
				} else {
					if (congestionHoldingMap.containsKey(lNextEquipId)) {
						lAdjacentUrl.append("1");
						lAdjacentUrl.append(",");
						lAdjacentUrl.append("2:");
						lAdjacentUrl.append(lNextEquipId);
					} else if (congestionHoldingMap.containsKey(lPrevEquipId)) {
						lAdjacentUrl.append("1");
						lAdjacentUrl.append(",");
						lAdjacentUrl.append("1:");
						lAdjacentUrl.append(lPrevEquipId);
					}
				}
			} else if (lEquipConfig.getDir() == 2) {
				if (congestionHoldingMap.containsKey(lNextEquipId) && congestionHoldingMap.containsKey(lPrevEquipId)) {
					lAdjacentUrl.append("2");
					lAdjacentUrl.append(",");
					lAdjacentUrl.append(lPrevEquipId);
					lAdjacentUrl.append(",");
					lAdjacentUrl.append(lNextEquipId);
				} else if (!congestionHoldingMap.containsKey(lNextEquipId) && (!congestionHoldingMap.containsKey(lPrevEquipId))) {
					lAdjacentUrl.append("0");
					lAdjacentUrl.append(",");
					lAdjacentUrl.append(0);
				} else {
					if (congestionHoldingMap.containsKey(lNextEquipId)) {
						lAdjacentUrl.append("1");
						lAdjacentUrl.append(",");
						lAdjacentUrl.append("1:");
						lAdjacentUrl.append(lNextEquipId);
					} else if (congestionHoldingMap.containsKey(lPrevEquipId)) {
						lAdjacentUrl.append("1");
						lAdjacentUrl.append(",");
						lAdjacentUrl.append("2:");
						lAdjacentUrl.append(lPrevEquipId);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error in bulding Adjacent Buffer " , e);
		}
		return lAdjacentUrl.toString();
	}*/
	
	/*public void removeMapEntry(String pEquipId, String pAlertId) {		
		congestionBuffer.getCongestionHoldingMap().remove(pEquipId);
		congestionBuffer.getPostHoldingFlagMap().remove(pEquipId);
		congestionBuffer.getClearPendingFlagMap().remove(pEquipId);
		congestionBuffer.getAlertEquipMap().remove(pAlertId); //review this
	}	*/
}