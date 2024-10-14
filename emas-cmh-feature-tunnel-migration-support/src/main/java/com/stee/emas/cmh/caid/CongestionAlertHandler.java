/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.caid;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stee.emas.common.dto.TrafficAlertClearDto;
import com.stee.emas.common.dto.TrafficAlertDto;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Handler class for CAID</p>
 * <p>This class is used to handle caid related functions
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Oct 21, 2013
 * @version 1.0
 * @since June 2, 2015
 * @version 2.0
 *
 */

@Component("congestionAlertHandler")
public class CongestionAlertHandler {
	
	private static Logger logger = LoggerFactory.getLogger(CongestionAlertHandler.class);
	
	Timer caidTimer = new Timer();
	
	@Autowired
	CongestionBuffer congestionBuffer;
	@Autowired
	CongestionAlertBuilder congestionAlertBuilder;
	
	Map<CaidKeyBean, TrafficAlertDto> congestionTempMap;
	Map<String, CaidLaneAlertObj> congestionHoldingMap;
	Map<String, TrafficAlertDto> congestionMap;
	
	public CongestionAlertHandler() {
	}
	
	public void initCongestionBuffer() {
		congestionTempMap		= congestionBuffer.getCongestionTempMap();
		congestionHoldingMap	= congestionBuffer.getCongestionHoldingMap();
		congestionMap			= congestionBuffer.getCongestionMap();
	}
		
	public boolean processLOSBegin(final TrafficAlertDto pTrafficAlertDto) {
		logger.info("Inside CongestionAlertHandler :: processLOSBegin -> Start .....AlertId :: " + pTrafficAlertDto.getAlertId() + ".....LaneId :: " + pTrafficAlertDto.getLaneId());
		initCongestionBuffer();
		String lEquipId = pTrafficAlertDto.getEquipId();
		int lLaneId  = pTrafficAlertDto.getLaneId();
		CaidKeyBean lCaidKeyBean = new CaidKeyBean(lEquipId, lLaneId);
		logger.info("congestionTempMap :: " + congestionTempMap);
		if (congestionTempMap.containsKey(lCaidKeyBean)) {
			return false;
		}
		congestionTempMap.put(lCaidKeyBean, pTrafficAlertDto);
		
		caidTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				//added try 2017-04-28: dongyun to avoid "timer already cancelled" error
				try {	
					congestionAlertBuilder.processCongestionAlert(pTrafficAlertDto);
				}
				catch (Exception e)
				{
					logger.error("exception in Timer function(processCongestionAlert):"+e.toString());
				}
			}
		}, 30000); 
		
		logger.info("Exit CongestionAlertHandler :: processLOSBegin .....");
		return true;
	}
	
	public boolean processLOSEnd(TrafficAlertClearDto pTrafficAlertClearDto) {
		logger.info("Inside CongestionAlertHandler :: processLOSEnd -> Start .....AlertId :: " + pTrafficAlertClearDto.getAlertId() + ".....LaneId :: " + pTrafficAlertClearDto.getLaneId());
		initCongestionBuffer();
		
		String lEquipId = pTrafficAlertClearDto.getEquipId();
		int lLaneId  = pTrafficAlertClearDto.getLaneId();
		CaidKeyBean lCaidKeyBean = new CaidKeyBean(lEquipId, lLaneId);
		
		logger.info("congestionTempMap :: " + congestionTempMap);
		if (congestionTempMap.containsKey(lCaidKeyBean)) {
			logger.info("EquipId + LaneId found in CongestionTempMap .....");
			congestionTempMap.remove(lCaidKeyBean);
		} else {
			congestionAlertBuilder.processCongestionClear(pTrafficAlertClearDto);
		}
		return true;
	}
	
	/*public boolean processLOSEnd(TrafficAlertClearDto pTrafficAlertClearDto) {
		logger.info("Inside CongestionAlertHandler :: processLOSEnd -> Start .....AlertId :: " + pTrafficAlertClearDto.getAlertId() + ".....LaneId :: " + pTrafficAlertClearDto.getLaneId());
		initCongestionBuffer();
		String lEquipId = alertEquipMap.get(pTrafficAlertClearDto.getAlertId());
		logger.info("lEquipId :: " + lEquipId);
		
		if (lEquipId != null && congestionHoldingMap.containsKey(lEquipId)) {
			TrafficAlertDto lTrafficAlertDto = congestionHoldingMap.get(lEquipId);
			congestionAlertBuilder.processCongestionClear(lTrafficAlertDto);
		}
		logger.info("Exit CongestionAlertHandler :: processLOSEnd .....");
		return true;
	}*/
}