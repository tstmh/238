package com.stee.emas.ctetun.pmcs;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.ctetun.pmcs.dto.PMCSBufferDto;
import com.stee.emas.ctetun.pmcs.scheduler.PMCSSchedulerManager;
import com.stee.emas.ctetun.pmcs.service.PMCSInterfaceStatusService;
import com.stee.emas.ctetun.pmcs.util.PMCSInititator;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@EnableJms
@EnableAsync
public class PMCSApplication {

	private static final Logger logger = LoggerFactory.getLogger(PMCSApplication.class);
	
	@Autowired
	private PMCSInititator pmcsInititator;
	
	@Autowired
    private PMCSBufferDto pmcsBufferDto;
		
	@Autowired
	private PMCSInterfaceStatusService interfaceStatusServiceImpl;
	
	public static void main(String[] args) {

		logger.info("*************************");
		logger.info("Copyrights-ST Engineering");
		logger.info(".....PMCS Interface Initialized.....");
		logger.info("*************************");

		ApplicationContext ctx = SpringApplication.run(PMCSApplication.class, args);
		logger.info("PMCS Interface application start....");

		PMCSSchedulerManager sc = ctx.getBean(PMCSSchedulerManager.class);
		sc.connectToPLC();
		sc.executePollEquipment();
		sc.executePollWatchDogEquipment();
		sc.executeUpdateInterfaceStatus();
	}
	
	@PostConstruct
	private void init() {
		logger.info("PMCS init called ....");	
		pmcsInititator.initHostConfig();
		pmcsInititator.initEquipmentConfig();
		pmcsInititator.initWatchDogEquipmentConfig();
		pmcsInititator.initEquipAttributeConfig();
		pmcsInititator.initEquipAttributeStatus();;	
		pmcsInititator.initAlarmCodeMapping();
	}	 
	
	@Bean
	public ModelMapper modelMapper() {		
		return new ModelMapper();
	}
	
	@PreDestroy
	private void destroy() {
		logger.info("destroy called ....");
		pmcsBufferDto.connectedPLCMap.entrySet().forEach(e -> {
			try {
				e.getValue().closeProtocol();				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		interfaceStatusServiceImpl.updateInterfaceStatus(Constants.PMCS_INTERFACE_ID, Constants.EQUIP_STATUS_NG);
	}
}
