package com.stee.emas.ctetun.pmcs.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.focus_sw.fieldtalk.MbusTcpMasterProtocol;
import com.stee.emas.ctetun.pmcs.dto.PMCSBufferDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSHostConfigDto;

@Service
@PropertySource("classpath:application.properties")
public class ModbusConnector {
	
    private static final Logger logger = LoggerFactory.getLogger(ModbusConnector.class);

    @Autowired
    PMCSDataProcess pmcsDataProcess;
    
    @Autowired
    private PMCSBufferDto pmcsBufferDto;    
    
    @Value("${plcInterval}")
    private long plcInterval;
    
    @Value("${retryCount}")
    private int retryCount;
    
    @Async
    public void connect(PMCSHostConfigDto pPmcsHostConfigDto) {
    	logger.info("Connect to Modbus .....");

    	String lHostName 	 = pPmcsHostConfigDto.getPlcHost();
    	String lHostIP   	 = pPmcsHostConfigDto.getHostIp();
    	String lPort 	 	 = pPmcsHostConfigDto.getPort();
    	
        logger.info("Connect to Host Name ::" + lHostName + ", Host IP ::" + lHostIP + ", Port ::" + lPort);
    	
        MbusTcpMasterProtocol tcpMbus = new MbusTcpMasterProtocol();
        tcpMbus.setRetryCnt(retryCount);
        if (pmcsBufferDto.getConnectedPLCMap().containsKey(lHostName)) {
        	return;
        }
		int previousPLCStatus = pPmcsHostConfigDto.getPlcStatus();
        try {
            tcpMbus.setPort(Integer.parseInt(lPort));
            logger.info(".....tcpMbus.isOpen()....." + tcpMbus.isOpen());
            if(!tcpMbus.isOpen()) {
            	tcpMbus.openProtocol(lHostIP);
            }
            if (tcpMbus.isOpen()) {
                logger.info("hostName is::" + lHostName + ",hostIp is ::" + lHostIP + " connected succefssfully!");
                pmcsBufferDto.getConnectedPLCMap().put(lHostName, tcpMbus);
                pPmcsHostConfigDto.setPlcStatus(1);
            }
        } catch (Exception e) {          
            disconnect(pPmcsHostConfigDto);
            e.printStackTrace();
            logger.error(lHostName + " , " + lHostIP + " is connected failed, connect next PLC in " + plcInterval + " seconds....", e);
        }
        
		logger.info("previousPLCStatus .... " + previousPLCStatus);
		logger.info("pWmcsHostConfigDto.getPlcStatus() .... " + pPmcsHostConfigDto.getPlcStatus());		
        if (previousPLCStatus != pPmcsHostConfigDto.getPlcStatus()) {
        	pmcsDataProcess.updatePLCStatus(pPmcsHostConfigDto);
        }
    }
    
    public void disconnect(PMCSHostConfigDto pPmcsHostConfigDto) {
    	logger.info("DisConnect to Modbus .....");
    	try {
    		MbusTcpMasterProtocol tcpMbus = pmcsBufferDto.getConnectedPLCMap().get(pPmcsHostConfigDto.getPlcHost());
    		if (tcpMbus != null) {
    			tcpMbus.closeProtocol();
    			tcpMbus = null;
    		}			
    		pmcsBufferDto.getConnectedPLCMap().remove(pPmcsHostConfigDto.getPlcHost());
    		pPmcsHostConfigDto.setPlcStatus(0);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("There was a problem when close Modbus connector.Exception - ", e);
		} catch (StackOverflowError s) {
			s.printStackTrace();
			logger.error("There was a problem when close Modbus connector.StackOverflowError - ", s);
		}
	}     
}
