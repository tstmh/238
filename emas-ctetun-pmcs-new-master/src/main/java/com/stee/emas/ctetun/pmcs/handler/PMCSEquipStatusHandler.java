package com.stee.emas.ctetun.pmcs.handler;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.focus_sw.fieldtalk.MbusSlaveFailureException;
import com.focus_sw.fieldtalk.MbusTcpMasterProtocol;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.ctetun.pmcs.dto.PMCSBufferDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSEquipConfigDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSHostConfigDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSWatchDogConfigDto;

@Component
public class PMCSEquipStatusHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(PMCSEquipStatusHandler.class);
	
	@Autowired
	ModbusConnector modbusConnector;
	@Autowired
	PMCSBufferDto pmcsBufferDto;
	@Autowired
	PMCSDataProcess pmcsDataProcess;
	
	@Async
	public void pollEquipment(PMCSHostConfigDto e) {
		logger.info("Calling pollEquipment .....");
		try {
			logger.info(".....PLC Host....." + e.getPlcHost() + ".....PLC Status....." + e.getPlcStatus());
			if (e.getPlcStatus() == 1) {
				Iterator<PMCSWatchDogConfigDto> ite = pmcsBufferDto.getPmcsWatchDogConfigDtoList().iterator();
				while (ite.hasNext()) {
					if (ite.next().getPlcHost().equalsIgnoreCase(e.getPlcHost())) {
						PMCSWatchDogConfigDto pmcsWatchDogConfigDto = ite.next();
						if (pmcsWatchDogConfigDto.getAlarmStatus() == Constants.ALARM_RAISED) {
							return;
						}
					}
				}
				List<PMCSEquipConfigDto> pmcsEquipConfigDtoList = pmcsBufferDto.getPmcsEquipConfigMap().get(e.getPlcHost());
				MbusTcpMasterProtocol tcpMbus = pmcsBufferDto.getConnectedPLCMap().get(e.getPlcHost());

				if (pmcsEquipConfigDtoList != null) {
					for (PMCSEquipConfigDto pm : pmcsEquipConfigDtoList) {
						if (!pm.getEquipType().equalsIgnoreCase(Constants.PWD_EQUIP_TYPE)) {
							short[] status = new short[pm.getMaxReadRegister()];
							try {
								tcpMbus.readMultipleRegisters(e.getSlaveAddress(), pm.getStatusAddress(), status);
								logger.info("equipment_id:" + pm.getEquipId() + ", equipment_type:" + pm.getEquipType() + ", hostName:" + pm.getPlcHost() + ", value:" + Arrays.toString(status));
								for (short value = 0; value < status.length; value++) {
									String binaryData = Integer.toBinaryString((int) status[value]);
									binaryData = StringUtils.leftPad(binaryData, 16, '0');
									binaryData = binaryData.substring(0, 16);
									logger.info("Binary Data :: " + binaryData);
									logger.info("call updateEquipment......the register position is::" + String.valueOf(value + 1));
									pmcsDataProcess.processEquipmentStatus(e, pm, binaryData, String.valueOf(value + 1));
								}
							} catch (MbusSlaveFailureException ex) {
								logger.error("plc address error: ", ex);
								logger.info("plc address error, host::" + pm.getPlcHost() + ",equipment id::" + pm.getEquipId() + ",equipment type::" + pm.getEquipType() + ",equipment address::" + pm.getStatusAddress());
							}
						}
					}
				}
			}
			logger.info("End of if/else polling .....");
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("pollEquipment occurs error: for FB :: " + e.getPlcHost());
			logger.error("pollEquipment occurs error: ", ex);
			modbusConnector.disconnect(e);
		}
		logger.info("Exit Poll Equipment ..........");
	}
	
	@Async
	public void pollWatchDogEquipment(PMCSHostConfigDto e) {
		logger.info("Calling pollWatchDogEquipment .....");
		try {
			logger.info(".....PLC Host....." + e.getPlcHost() + ".....PLC Status....." + e.getPlcStatus());
			if (e.getPlcStatus() == 1) {
				List<PMCSEquipConfigDto> pmcsEquipConfigDtoList = pmcsBufferDto.getPmcsEquipConfigMap().get(e.getPlcHost());
				MbusTcpMasterProtocol tcpMbus = pmcsBufferDto.getConnectedPLCMap().get(e.getPlcHost());

				if (pmcsEquipConfigDtoList != null) {
					for (PMCSEquipConfigDto pm : pmcsEquipConfigDtoList) {
						if (pm.getEquipType().equalsIgnoreCase(Constants.PWD_EQUIP_TYPE)) {
							short[] status = new short[pm.getMaxReadRegister()];
							try {
								tcpMbus.readMultipleRegisters(e.getSlaveAddress(), pm.getStatusAddress(), status);
								logger.info("equipment_id:" + pm.getEquipId() + ", equipment_type:" + pm.getEquipType() + ", hostName:" + pm.getPlcHost() + ", value:" + Arrays.toString(status));
								pmcsDataProcess.processWatchDogStatus(pm, status);
							} catch (MbusSlaveFailureException ex) {
								logger.error("plc address error: ", ex);
								logger.info("plc address error, host::" + pm.getPlcHost() + ",equipment id::" + pm.getEquipId() + ",equipment type::" + pm.getEquipType() + ",equipment address::" + pm.getStatusAddress());
							}
						}
					}
				}
			}
			logger.info("End of if/else polling .....");
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("pollWatchDogEquipment occurs error: for FB :: " + e.getPlcHost());
			logger.error("pollWatchDogEquipment occurs error: ", ex);
			modbusConnector.disconnect(e);
		}
		logger.info("Exit Poll WatchDog Equipment ..........");
	}	
}
