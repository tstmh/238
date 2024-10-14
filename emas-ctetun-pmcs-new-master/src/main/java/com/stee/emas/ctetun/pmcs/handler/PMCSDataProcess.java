package com.stee.emas.ctetun.pmcs.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.EquipStatusDto;
import com.stee.emas.common.dto.EquipStatusDtoList;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emas.common.util.CommonUtil;
import com.stee.emas.ctetun.pmcs.dto.PMCSBufferDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSEquipAttributeDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSEquipConfigDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSHostConfigDto;
import com.stee.emas.ctetun.pmcs.dto.PMCSWatchDogConfigDto;
import com.stee.emas.ctetun.pmcs.message.MessageSender;
import com.stee.emas.ctetun.pmcs.service.PMCSAttributeStatusService;

@Component
public class PMCSDataProcess {

	private static final Logger logger = LoggerFactory.getLogger(PMCSDataProcess.class);

	@Autowired
	private PMCSBufferDto pmcsBufferDto;
	@Autowired
	private PMCSAttributeStatusService attributeStatusServiceImpl;
	@Autowired
	MessageSender messageSender;
	
    @Value("${retryCount}")
    private int retryCount;

	public void updatePLCStatus(PMCSHostConfigDto pPmcsHostConfigDto) {

		int lEquipStatusValue = 0;
		int lAlarmStatus = 0;

		String lHostName = pPmcsHostConfigDto.getPlcHost();
		logger.info("Calling updatePLCStatus -----> Start {}, {}", lHostName, pPmcsHostConfigDto.getHostIp());

		Optional<PMCSEquipConfigDto> pmcsEquipConfigDtoOp;
		if (pPmcsHostConfigDto.getPlcStatus() == 1) {
			lEquipStatusValue = Constants.EQUIP_STATUS_NORMAL;
			lAlarmStatus = Constants.ALARM_CLEARED;
		} else if (pPmcsHostConfigDto.getPlcStatus() == 0) {
			lEquipStatusValue = Constants.EQUIP_STATUS_NG;
			lAlarmStatus = Constants.ALARM_RAISED;
		}
		pmcsEquipConfigDtoOp = pmcsBufferDto.getPmcsEquipConfigMap().get(lHostName).stream().filter(ec -> ec.getEquipType().equals(Constants.PWD_EQUIP_TYPE)).findFirst();

		if (pmcsEquipConfigDtoOp.isPresent()) {
			PMCSEquipConfigDto lPmcsEquipConfigDto = pmcsEquipConfigDtoOp.get();

			pmcsBufferDto.getPmcsEquipStatusMap().put(lPmcsEquipConfigDto.getEquipId(), Constants.OPE_STATUS, lEquipStatusValue);
			// update DB
			attributeStatusServiceImpl.updateAttributeValue(lPmcsEquipConfigDto.getEquipId(), Constants.OPE_STATUS, lEquipStatusValue);
			// sendToQueue
			EquipStatusDtoList equipStatusDtoList = buildPmcsEquipStatusDto(lPmcsEquipConfigDto, Constants.OPE_STATUS, lEquipStatusValue);
			messageSender.sendCmhJmsMessage(equipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			logger.info("EquipStatus sent to Queue :: " + equipStatusDtoList);

			// Send alarm to queue
			int alarmCode = pmcsBufferDto.getPmcsAlarmCodeMappingMap().get(lPmcsEquipConfigDto.getEquipType(), Constants.COMMUNICATION_DOWN_ALARM);
			ArrayList<TechnicalAlarmDto> lTechAlarmList = new ArrayList<TechnicalAlarmDto>();
			TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();

			TechnicalAlarmDto lTechnicalAlarmDto = generateTechnicalAlarm(lPmcsEquipConfigDto.getEquipId(), lPmcsEquipConfigDto.getEquipType(), alarmCode, lAlarmStatus);
			lTechAlarmList.add(lTechnicalAlarmDto);

			lTechAlarmDtoList.setDtoList(lTechAlarmList);
			messageSender.sendCmhJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
			logger.info("TechAlarm sent to Queue :: " + lTechnicalAlarmDto);
		}
	}

	public void processEquipmentStatus(PMCSHostConfigDto pmcsHostConfigDto, PMCSEquipConfigDto pmcsEquipConfigDto, String binaryData, String registerPosition) {

		List<PMCSEquipAttributeDto> pmcsEquipAttributeDtoList = pmcsBufferDto.getPmcsAttributeConfigMap().get(pmcsEquipConfigDto.getEquipType(), registerPosition);
		if (pmcsEquipAttributeDtoList.isEmpty() || pmcsEquipAttributeDtoList == null) {
			return;
		}
		binaryData = StringUtils.reverse(binaryData);

		for (PMCSEquipAttributeDto pmcsEquipAttributeDto : pmcsEquipAttributeDtoList) {
			String tempData = StringUtils.reverse(binaryData.substring(pmcsEquipAttributeDto.getBitStart(), pmcsEquipAttributeDto.getBitStart() + pmcsEquipAttributeDto.getBitLength()));
			int attributeValue = Integer.parseInt(tempData, 2);

			logger.info("hostName is: " + pmcsEquipConfigDto.getPlcHost() + ", equipType is: " + pmcsEquipConfigDto.getEquipType() + ", equipmentId is: " + pmcsEquipConfigDto.getEquipId()
					+ ", attributeId is: " + pmcsEquipAttributeDto.getAttributeCode() + ", register Position is: "
					+ registerPosition + ", attributeValue is: " + attributeValue);

			// attribute is equipmnet status
			if (pmcsEquipAttributeDto.getAttrTypeId().equals(Constants.EQUIP_ATTRIBUTE_STATUS)) {

				handleAttributeData(pmcsEquipConfigDto, pmcsEquipAttributeDto, attributeValue);
			}
			// attribute is alarm
			else if (pmcsEquipAttributeDto.getAttrTypeId().equals(Constants.EQUIP_ALARM_TYPE_ID)) {
				handleAlarmData(pmcsEquipConfigDto, pmcsEquipAttributeDto, attributeValue);
			} else {
				logger.info("Invalid attribute, equipType is: " + pmcsEquipConfigDto.getEquipType() + ", equipmentId is: " + pmcsEquipConfigDto.getEquipId() + ", attributeId is: "
						+ pmcsEquipAttributeDto.getAttributeCode());
			}
		}
	}
	
	public void processWatchDogStatus(PMCSEquipConfigDto pmcsEquipConfigDto, short[] status) {

		Iterator<PMCSWatchDogConfigDto> ite = pmcsBufferDto.getPmcsWatchDogConfigDtoList().iterator();
		while (ite.hasNext()) {
			if (ite.next().getPlcHost().equalsIgnoreCase(pmcsEquipConfigDto.getPlcHost())) {
				PMCSWatchDogConfigDto pmcsWatchDogConfigDto = ite.next();
				if (pmcsWatchDogConfigDto.getValue() == (int) status[0]) {
					pmcsWatchDogConfigDto.setCount(pmcsWatchDogConfigDto.getCount() + 1);
					if (pmcsWatchDogConfigDto.getCount() == retryCount) {
						if (pmcsWatchDogConfigDto.getAlarmStatus() == Constants.ALARM_CLEARED) {
							int alarmCode = pmcsBufferDto.getPmcsAlarmCodeMappingMap().get(pmcsEquipConfigDto.getEquipType(), Constants.PLC_ERROR_ALARM);
							ArrayList<TechnicalAlarmDto> lTechAlarmList = new ArrayList<TechnicalAlarmDto>();
							TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
							TechnicalAlarmDto lTechnicalAlarmDto = generateTechnicalAlarm(pmcsEquipConfigDto.getEquipId(), pmcsEquipConfigDto.getEquipType(), alarmCode, Constants.ALARM_RAISED);
							lTechAlarmList.add(lTechnicalAlarmDto);

							lTechAlarmDtoList.setDtoList(lTechAlarmList);
							messageSender.sendCmhJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
							logger.info("TechAlarm sent to Queue :: " + lTechnicalAlarmDto);
							pmcsWatchDogConfigDto.setCount(0);
							pmcsWatchDogConfigDto.setAlarmStatus(Constants.ALARM_RAISED);
						}
					}
				} else {
					pmcsWatchDogConfigDto.setValue(status[0]);
					pmcsWatchDogConfigDto.setCount(0);
					if (pmcsWatchDogConfigDto.getAlarmStatus() == Constants.ALARM_RAISED) {
						int alarmCode = pmcsBufferDto.getPmcsAlarmCodeMappingMap().get(pmcsEquipConfigDto.getEquipType(), Constants.PLC_ERROR_ALARM);
						ArrayList<TechnicalAlarmDto> lTechAlarmList = new ArrayList<TechnicalAlarmDto>();
						TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
						TechnicalAlarmDto lTechnicalAlarmDto = generateTechnicalAlarm(pmcsEquipConfigDto.getEquipId(), pmcsEquipConfigDto.getEquipType(), alarmCode, Constants.ALARM_CLEARED);
						lTechAlarmList.add(lTechnicalAlarmDto);

						lTechAlarmDtoList.setDtoList(lTechAlarmList);
						messageSender.sendCmhJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
						logger.info("TechAlarm sent to Queue :: " + lTechnicalAlarmDto);
						pmcsWatchDogConfigDto.setAlarmStatus(Constants.ALARM_CLEARED);
					}
				}
			}
		}
	}

	private void handleAttributeData(PMCSEquipConfigDto pmcsEquipConfigDto, PMCSEquipAttributeDto pmcsEquipAttributeDto, int attributeValue) {
		logger.info("Calling handlePmcsAttributeData .....");

		logger.info("Equip Id ...." + pmcsEquipConfigDto.getEquipId());
		logger.info("Attribute Code ...." + pmcsEquipAttributeDto.getAttributeCode());

		int previousValue = pmcsBufferDto.getPmcsEquipStatusMap().get(pmcsEquipConfigDto.getEquipId(), pmcsEquipAttributeDto.getAttributeCode());

		if (previousValue != attributeValue) {
			logger.info("***** Received Change in Attribute Status for *****");
			logger.info("Equip Id: " + pmcsEquipConfigDto.getEquipId() + ", Host Name: " + pmcsEquipConfigDto.getPlcHost() + ", Attribute Code :" + pmcsEquipAttributeDto.getAttributeCode());
			logger.info("Attribute current Value -----> " + previousValue + "New Value -----> " + attributeValue);

			// updateBuffer
			pmcsBufferDto.getPmcsEquipStatusMap().put(pmcsEquipConfigDto.getEquipId(), pmcsEquipAttributeDto.getAttributeCode(), attributeValue);
			// update DB
			attributeStatusServiceImpl.updateAttributeValue(pmcsEquipConfigDto.getEquipId(), pmcsEquipAttributeDto.getAttributeCode(), attributeValue);
			// sendToQueue
			EquipStatusDtoList equipStatusDtoList = buildPmcsEquipStatusDto(pmcsEquipConfigDto, pmcsEquipAttributeDto.getAttributeCode(), attributeValue);
			messageSender.sendCmhJmsMessage(equipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			logger.info("EquipStatus sent to Queue :: " + equipStatusDtoList);

			if (pmcsEquipAttributeDto.getAttributeCode().equalsIgnoreCase("ope")) {

				int lAlarmStatus = 0;

				if (attributeValue == 2) {
					lAlarmStatus = Constants.ALARM_CLEARED;
				} else if (attributeValue == 0) {
					lAlarmStatus = Constants.ALARM_RAISED;
				}
				// Send alarm to queue
				int alarmCode = pmcsBufferDto.getPmcsAlarmCodeMappingMap().get(pmcsEquipConfigDto.getEquipType(), Constants.COMMUNICATION_DOWN_ALARM);
				ArrayList<TechnicalAlarmDto> lTechAlarmList = new ArrayList<TechnicalAlarmDto>();
				TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
				TechnicalAlarmDto lTechnicalAlarmDto = generateTechnicalAlarm(pmcsEquipConfigDto.getEquipId(), pmcsEquipConfigDto.getEquipType(), alarmCode, lAlarmStatus);
				lTechAlarmList.add(lTechnicalAlarmDto);

				lTechAlarmDtoList.setDtoList(lTechAlarmList);
				messageSender.sendCmhJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
				logger.info("TechAlarm sent to Queue :: " + lTechnicalAlarmDto);
			}
			logger.info("**********************");
		}
	}

	private void handleAlarmData(PMCSEquipConfigDto pmcsEquipConfigDto, PMCSEquipAttributeDto pmcsEquipAttributeDto, int attributeValue) {
		logger.info("Calling handlePmcsAlarmData .....");

		int previousValue = pmcsBufferDto.getPmcsEquipStatusMap().get(pmcsEquipConfigDto.getEquipId(), pmcsEquipAttributeDto.getAttributeCode());

		if (previousValue != attributeValue) {
			logger.info("***** Received Change in Alarm Data for *****");
			logger.info("Equip Id: " + pmcsEquipConfigDto.getEquipId() + ", Host Name: " + pmcsEquipConfigDto.getPlcHost() + ", Attribute Code :" + pmcsEquipAttributeDto.getAttributeCode());
			logger.info("Alarm current Value -----> " + previousValue + "New Value -----> " + attributeValue);

			// updateBuffer
			pmcsBufferDto.getPmcsEquipStatusMap().put(pmcsEquipConfigDto.getEquipId(), pmcsEquipAttributeDto.getAttributeCode(), attributeValue);
			// update DB
			attributeStatusServiceImpl.updateAttributeValue(pmcsEquipConfigDto.getEquipId(), pmcsEquipAttributeDto.getAttributeCode(), attributeValue);
			// sendToQueue
			int lAlarmStatus = 0;
			if (attributeValue == 0) {
				lAlarmStatus = Constants.ALARM_CLEARED;
			} else if (attributeValue == 1) {
				lAlarmStatus = Constants.ALARM_RAISED;
			}
			int alarmCode = pmcsBufferDto.getPmcsAlarmCodeMappingMap().get(pmcsEquipConfigDto.getEquipType(), pmcsEquipAttributeDto.getAttributeCode());

			ArrayList<TechnicalAlarmDto> lTechAlarmList = new ArrayList<TechnicalAlarmDto>();
			TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();

			TechnicalAlarmDto lTechnicalAlarmDto = generateTechnicalAlarm(pmcsEquipConfigDto.getEquipId(), pmcsEquipConfigDto.getEquipType(), alarmCode, lAlarmStatus);
			lTechAlarmList.add(lTechnicalAlarmDto);

			lTechAlarmDtoList.setDtoList(lTechAlarmList);
			messageSender.sendCmhJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
			logger.info("TechAlarm sent to Queue :: " + lTechnicalAlarmDto);

			logger.info("**********************");
		}
	}

	private EquipStatusDtoList buildPmcsEquipStatusDto(PMCSEquipConfigDto pPmcsEquipConfigDto, String pAttributeCode, int pEquipStatus) {
		EquipStatusDto equipStatusDto = new EquipStatusDto();
		List<EquipStatusDto> equipStatusList = new ArrayList<>();
		EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList();
		equipStatusDto.setEquipType(pPmcsEquipConfigDto.getEquipType());
		equipStatusDto.setEquipId(pPmcsEquipConfigDto.getEquipType() + pPmcsEquipConfigDto.getEquipId());
		equipStatusDto.setSystemId(Constants.SYSTEM_ID);
		equipStatusDto.setStatusCode(pAttributeCode);
		equipStatusDto.setStatus(pEquipStatus);
		equipStatusDto.setDateTime(new Date());
		equipStatusList.add(equipStatusDto);
		lEquipStatusDtoList.setDtoList(equipStatusList);
		return lEquipStatusDtoList;
	}

	public static TechnicalAlarmDto generateTechnicalAlarm(String pEquipId, String pEquipType, int pAlarmCode, int pStatus) {
		TechnicalAlarmDto lTechAlarmDto = new TechnicalAlarmDto();
		lTechAlarmDto.setAlarmCode(pAlarmCode);
		lTechAlarmDto.setAlarmId(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, pEquipType + "_" + pEquipId, pAlarmCode));
		lTechAlarmDto.setSystemId(Constants.SYSTEM_ID);
		lTechAlarmDto.setStartDate(new Date());
		lTechAlarmDto.setEquipId(pEquipId);
		lTechAlarmDto.setEquipType(pEquipType);
		lTechAlarmDto.setStatus(pStatus);

		return lTechAlarmDto;
	}
}
