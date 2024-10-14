package com.stee.emas.ctetun.wmss.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.EquipStatusDto;
import com.stee.emas.common.dto.EquipStatusDtoList;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emas.common.util.CommonUtil;
import com.stee.emas.ctetun.wmss.dto.WmssBufferDto;
import com.stee.emas.ctetun.wmss.dto.WmssEquipConfigDto;
import com.stee.emas.ctetun.wmss.dto.WmssEquipStatusDto;
import com.stee.emas.ctetun.wmss.message.MessageSender;
import com.stee.emas.ctetun.wmss.service.impl.WmssAttributeStatusServiceImpl;


@Component
public class WmssDataProcess {
	
	private static final Logger logger = LoggerFactory.getLogger(WmssDataProcess.class);
	
	@Autowired
	private WmssBufferDto wmssBufferDto;
	@Autowired
	private WmssAttributeStatusServiceImpl attributeStatusServiceImpl;
	@Autowired
	MessageSender messageSender;	

	
	public void handleEquipStatus(WmssEquipConfigDto pWmssEquipConfigDto, WmssEquipStatusDto pWmssEquipStatusDto, String pAttributeCode, int pEquipStatus) {
		
		//updateBuffer
		wmssBufferDto.getWmssEquipStatusMap().put(pWmssEquipStatusDto.getEquipId(), pAttributeCode, pEquipStatus);
		//update DB		
		attributeStatusServiceImpl.updateEquipAttributeValue(pWmssEquipStatusDto.getEquipId(), pAttributeCode, pEquipStatus);		
	
		int lAlarmStatus = 0;
		int alarmCode = 0;
		if(pWmssEquipStatusDto.getEquipStatus().equalsIgnoreCase("1")) {
			lAlarmStatus = Constants.ALARM_CLEARED;
		}
		else if(pWmssEquipStatusDto.getEquipStatus().equalsIgnoreCase("0")) {
			lAlarmStatus = Constants.ALARM_RAISED;
		}
		if(pWmssEquipConfigDto.getEquipType().equalsIgnoreCase(Constants.WMC_EQUIP_TYPE)) {
			//sendToQueue		
			EquipStatusDtoList equipStatusDtoList = buildWmcsEquipStatusDto(pWmssEquipConfigDto, pAttributeCode, pEquipStatus);
			messageSender.sendCmhJmsMessage(equipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
			logger.info("EquipStatus sent to Queue :: " + equipStatusDtoList);
			alarmCode = wmssBufferDto.getWmssAlarmCodeMappingMap().get(pWmssEquipConfigDto.getEquipType(), Constants.COMMUNICATION_DOWN_ALARM);
		}
		else
		{
			alarmCode = wmssBufferDto.getWmssAlarmCodeMappingMap().get(pWmssEquipConfigDto.getEquipType(), pAttributeCode);
		}
		ArrayList<TechnicalAlarmDto> lTechAlarmList = new ArrayList<TechnicalAlarmDto>();
		TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();
		
		TechnicalAlarmDto lTechnicalAlarmDto = generateTechnicalAlarm(pWmssEquipConfigDto.getEquipId(), pWmssEquipConfigDto.getEquipType(), alarmCode, lAlarmStatus);
		lTechAlarmList.add(lTechnicalAlarmDto);
		
		lTechAlarmDtoList.setDtoList(lTechAlarmList);
		messageSender.sendCmhJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
		logger.info("TechAlarm sent to Queue :: " + lTechnicalAlarmDto);
	}
	
	public void handleRestServiceStatus(int opeStatus, int pAlarmStatus) {
		logger.info("Calling handleRestServiceStatus .....");

		WmssEquipConfigDto lWmssEquipConfigDto = wmssBufferDto.getWmssEquipConfigMap().get(Constants.WMSS_REST_EQUIP_ID);

		int previousValue = wmssBufferDto.getWmssEquipStatusMap().get(lWmssEquipConfigDto.getEquipId(), Constants.OPE_STATUS);

		if (previousValue != opeStatus) {
			// updateBuffer
			wmssBufferDto.getWmssEquipStatusMap().put(lWmssEquipConfigDto.getEquipId(), Constants.OPE_STATUS, opeStatus);
			// update DB
			attributeStatusServiceImpl.updateEquipAttributeValue(lWmssEquipConfigDto.getEquipId(), Constants.OPE_STATUS, opeStatus);

			// Send alarm to queue
			int alarmCode = wmssBufferDto.getWmssAlarmCodeMappingMap().get(lWmssEquipConfigDto.getEquipType(), Constants.COMMUNICATION_DOWN_ALARM);
			ArrayList<TechnicalAlarmDto> lTechAlarmList = new ArrayList<TechnicalAlarmDto>();
			TechAlarmDtoList lTechAlarmDtoList = new TechAlarmDtoList();

			TechnicalAlarmDto lTechnicalAlarmDto = generateTechnicalAlarm(lWmssEquipConfigDto.getEquipId(), lWmssEquipConfigDto.getEquipType(), alarmCode, pAlarmStatus);
			lTechAlarmList.add(lTechnicalAlarmDto);

			lTechAlarmDtoList.setDtoList(lTechAlarmList);
			messageSender.sendCmhJmsMessage(lTechAlarmDtoList, MessageConstants.TECH_ALARM_ID);
			logger.info("TechAlarm sent to Queue :: " + lTechnicalAlarmDto);
		}
	}
	
	private EquipStatusDtoList buildWmcsEquipStatusDto(WmssEquipConfigDto pWmssEquipConfigDto, String pAttributeCode, int pEquipStatus) {
		EquipStatusDto equipStatusDto = new EquipStatusDto();
		List<EquipStatusDto> equipStatusList = new ArrayList<>();
		EquipStatusDtoList lEquipStatusDtoList = new EquipStatusDtoList();
		equipStatusDto.setEquipType(pWmssEquipConfigDto.getEquipType());
		equipStatusDto.setEquipId(pWmssEquipConfigDto.getEquipType() + pWmssEquipConfigDto.getEquipId());
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
		lTechAlarmDto.setAlarmId(CommonUtil.generateAlarmId(Constants.SYSTEM_ID, pEquipType+ "_"+ pEquipId, pAlarmCode));
		lTechAlarmDto.setSystemId(Constants.SYSTEM_ID);
		lTechAlarmDto.setStartDate(new Date());
		lTechAlarmDto.setEquipId(pEquipId);
		lTechAlarmDto.setEquipType(pEquipType);
		lTechAlarmDto.setStatus(pStatus);
		
		return lTechAlarmDto;
	}
}
