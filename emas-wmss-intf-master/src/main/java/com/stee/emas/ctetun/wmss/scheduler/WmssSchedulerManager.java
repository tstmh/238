package com.stee.emas.ctetun.wmss.scheduler;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.ctetun.wmss.handler.WmssEquipStatusHandler;
import com.stee.emas.ctetun.wmss.service.WmssInterfaceStatusService;

@Component
public class WmssSchedulerManager {

	private static final Logger logger = LoggerFactory.getLogger(WmssSchedulerManager.class);

	@Value("${pollInterval}")
	private long pollInterval;
	
	@Value("${updateInterfaceInterval}")
	private long updateInterfaceInterval;

	@Autowired
	@Qualifier("fixedThreadPool")
	private ScheduledExecutorService executorService;

	@Autowired
	private WmssEquipStatusHandler wmssEquipStatusHandler;
	
	@Autowired
	private WmssInterfaceStatusService interfaceStatusServiceImpl;

	public void executePollEquipStatus() {
		logger.info("Calling executePollEquipmentStatus.....SchedulerManager for Equipment Status----->");
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				logger.info("Poll Equipment Status Timer Started ....." + new Date());
				wmssEquipStatusHandler.getAllEquipStatus();
			}
		}, 0, pollInterval, TimeUnit.SECONDS);
	}
	
	public void executeUpdateInterfaceStatus() {		
		logger.info("Calling executeUpdateInterfaceStatus.....SchedulerManager for WMSS Interface----->");
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				logger.info("Update Interface Status Timer started ....." + new Date());
				interfaceStatusServiceImpl.updateInterfaceStatus(Constants.WMSS_INTERFACE_ID, Constants.EQUIP_STATUS_NORMAL);
			}
		}, 0, updateInterfaceInterval, TimeUnit.SECONDS);
    }
}
