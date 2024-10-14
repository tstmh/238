/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.service;

import java.util.List;

import com.stee.emas.common.entity.TrafficMeasure;

/**
 * 
 * @author Scindia
 * @since Oct 16, 2012
 * @version 1.0
 *
 */

public interface TrafficMeasureManager {
	
	String saveTrafficMeasure(TrafficMeasure pTrafficMeasure);
	
	String updateTrafficMeasure(TrafficMeasure pTrafficMeasure);
	
	String deleteTrafficMeasure(TrafficMeasure pTrafficMeasure);
	
	TrafficMeasure findTrafficMeasureByUniqueId(String pEquipId, int pLaneId, int pLaneType, String pDataType);
	
	List<TrafficMeasure> getAllTrafficMeasure();
	
	List<TrafficMeasure> getTrafficMeasureByDataType(String pDataType);
}

