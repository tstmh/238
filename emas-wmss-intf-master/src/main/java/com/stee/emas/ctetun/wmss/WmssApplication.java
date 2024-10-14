package com.stee.emas.ctetun.wmss;

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
import com.stee.emas.ctetun.wmss.scheduler.WmssSchedulerManager;
import com.stee.emas.ctetun.wmss.service.WmssInterfaceStatusService;
import com.stee.emas.ctetun.wmss.util.WmssInititator;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@EnableJms
@EnableAsync
public class WmssApplication {

	private static final Logger logger = LoggerFactory.getLogger(WmssApplication.class);
	
	@Autowired
	private WmssInititator wmssInititator;
	
	@Autowired
	private WmssInterfaceStatusService interfaceStatusServiceImpl;
	
	public static void main(String[] args) {

		logger.info("*************************");
		logger.info("Copyrights-ST Engineering");
		logger.info(".....WMSS Interface Initialized.....");
		logger.info("*************************");

		ApplicationContext ctx = SpringApplication.run(WmssApplication.class, args);
		logger.info("WMSS Interface application start....");

		WmssSchedulerManager sc = ctx.getBean(WmssSchedulerManager.class);
		sc.executePollEquipStatus();
		sc.executeUpdateInterfaceStatus();
	}
	
	@PostConstruct
	private void init() {
		logger.info("WMSS init called ....");	
		wmssInititator.initWmssEquipConfig();
		wmssInititator.initEquipAttributeStatus();;	
		wmssInititator.initAlarmCodeMapping();
	}	 
	
	@Bean
	public ModelMapper modelMapper() {		
		return new ModelMapper();
	}
	
	@PreDestroy
	private void destroy() {
		logger.info("destroy called ....");
		interfaceStatusServiceImpl.updateInterfaceStatus(Constants.WMSS_INTERFACE_ID, Constants.EQUIP_STATUS_NG);
	}
}
