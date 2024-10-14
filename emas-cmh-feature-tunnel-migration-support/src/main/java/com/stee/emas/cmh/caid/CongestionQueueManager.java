/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */
package com.stee.emas.cmh.caid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.stee.emas.cmh.integration.CMHMessageSender;
import com.stee.emas.cmh.service.TrafficAlertManager;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.TrafficAlertDto;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Manager class for Congestion Queue</p>
 * <p>This class is used for processing alert holding in the Queue
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Nov 12, 2013
 * @version 1.0
 *
 */

@Component("congestionQueueManager")
public class CongestionQueueManager {
	
	private static Logger logger = LoggerFactory.getLogger(CongestionQueueManager.class);
	
	CongestionAlertQueue congestionAlertQueue = CongestionAlertQueue.getInstance();
	
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	TrafficAlertManager trafficAlertManager;
	@Autowired
	CMHMessageSender cmhMessageSender;
	
	public CongestionQueueManager() {
    }
	
	public void init() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				processCongestionAlertQueue();
			}
		});
	}

    /** main process of the thread */
    public void processCongestionAlertQueue() {
    	logger.info("CongestionQueue Thread Started ..... ");
        while(true) {
        	try {
        		TrafficAlertDto lTrafficAlertDto = congestionAlertQueue.get();
        		logger.info("Processing Object from Queue..... ");
        		if (lTrafficAlertDto.getImageUrl() == null || (lTrafficAlertDto.getImageUrl() != null && lTrafficAlertDto.getImageUrl().trim().length() == 0)) {
        			String lImageUrl = trafficAlertManager.processPreviewImageDelete(lTrafficAlertDto);
            		lTrafficAlertDto.setImageUrl(lImageUrl);
        		}
        		if (lTrafficAlertDto.getEndDate() != null) {
        			trafficAlertManager.processTrafficAlertClear(lTrafficAlertDto);
        		} else {
        			trafficAlertManager.processTrafficAlert(lTrafficAlertDto);
        		}
        		logger.info("Object Sent to the Queue " + lTrafficAlertDto);
    			cmhMessageSender.sendAWJmsMessage("TrafficAlert", MessageConstants.TRAFFIC_ALERT_ID);
    			cmhMessageSender.sendEmasCcsJmsMessage(lTrafficAlertDto, MessageConstants.TRAFFIC_ALERT_ID);
    			// Added 04/03/22 GH - To implement new queues to ITPT-INTF
    			cmhMessageSender.sendEmasItptJmsMessage(lTrafficAlertDto, MessageConstants.TRAFFIC_ALERT_ID);
        	} catch (Exception e) {        		
        		logger.error("Error processing CongestionAlertThread ... ", e);
        	}
        }
    }
}