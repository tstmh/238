package com.stee.emas.ctetun.pmcs.scheduler;

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
import com.stee.emas.ctetun.pmcs.dto.PMCSBufferDto;
import com.stee.emas.ctetun.pmcs.handler.ModbusConnector;
import com.stee.emas.ctetun.pmcs.handler.PMCSEquipStatusHandler;
import com.stee.emas.ctetun.pmcs.service.PMCSInterfaceStatusService;

@Component
public class PMCSSchedulerManager {

	private static final Logger logger = LoggerFactory.getLogger(PMCSSchedulerManager.class);

	@Value("${pollInterval}")
	private long pollInterval;
	
	@Value("${plcInterval}")
	private long plcInterval;	
	
	@Value("${updateInterfaceInterval}")
	private long updateInterfaceInterval;

	@Autowired
	@Qualifier("fixedThreadPool")
	private ScheduledExecutorService executorService;
	
	@Autowired
	private PMCSEquipStatusHandler pmcsEquipStatusHandler;
	
	@Autowired
	private ModbusConnector modbusConnector;
	
	@Autowired
	PMCSBufferDto pmcsBufferDto;
	
	@Autowired
	private PMCSInterfaceStatusService interfaceStatusServiceImpl;

	public void connectToPLC() {		
		logger.info("Calling connectToPLC.....SchedulerManager for PLC----->");
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				logger.info("PLC Connect Timer started ....." + new Date());
				pmcsBufferDto.getPmcsHostConfigDtoList().forEach(e -> {
		    		modbusConnector.connect(e);
		    	});
			}
		}, 0, plcInterval, TimeUnit.SECONDS);
    }
	
	public void executePollEquipment() {		
		logger.info("Calling executePollEquipment.....SchedulerManager for PMCS Equipment----->");
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				logger.info("Poll Equipment Timer started ....." + new Date());
				pmcsBufferDto.getPmcsHostConfigDtoList().forEach(e -> {
					pmcsEquipStatusHandler.pollEquipment(e);
				});
			}
		}, 0, pollInterval, TimeUnit.SECONDS);
    }
	
	public void executePollWatchDogEquipment() {		
		logger.info("Calling executePollWatchDogEquipment.....SchedulerManager for PMCS Equipment----->");
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				logger.info("Poll WatchDog Equipment Timer started ....." + new Date());
				pmcsBufferDto.getPmcsHostConfigDtoList().forEach(e -> {
					pmcsEquipStatusHandler.pollWatchDogEquipment(e);
				});
			}
		}, 0, pollInterval, TimeUnit.SECONDS);
    }
	
	public void executeUpdateInterfaceStatus() {		
		logger.info("Calling executeUpdateInterfaceStatus.....SchedulerManager for PMCS Interface----->");
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				logger.info("Update Interface Status Timer started ....." + new Date());
				interfaceStatusServiceImpl.updateInterfaceStatus(Constants.PMCS_INTERFACE_ID, Constants.EQUIP_STATUS_NORMAL);
			}
		}, 0, updateInterfaceInterval, TimeUnit.SECONDS);  
    }
}
