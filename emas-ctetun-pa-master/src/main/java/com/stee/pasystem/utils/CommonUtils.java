package com.stee.pasystem.utils;

import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.pasystem.enums.AlarmCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;

import java.io.Serializable;
import java.util.Date;

@Slf4j
public class CommonUtils {

    private CommonUtils() {
    }

    public static String generateAlarmId(String equipType, String equipId, int alarmCode) {
        return String.format("emas_%s_%s_%d", equipType, equipId, alarmCode);
    }

    public static TechnicalAlarmDto createAlarmDtoRaise(String equipId, String equipType, AlarmCodeEnum alarmCodeEnum) {
        return createAlarmDto(equipId, equipType, alarmCodeEnum, Constants.ALARM_RAISED);
    }

    public static TechnicalAlarmDto createAlarmDtoClear(String equipId, String equipType, AlarmCodeEnum alarmCodeEnum) {
        return createAlarmDto(equipId, equipType, alarmCodeEnum, Constants.ALARM_CLEARED);
    }

    public static TechnicalAlarmDto createAlarmDto(String equipId, String equipType, AlarmCodeEnum alarmCodeEnum, int status) {
        TechnicalAlarmDto dto = new TechnicalAlarmDto();
        dto.setAlarmId(generateAlarmId(equipType, equipId, alarmCodeEnum.getCode()));
        dto.setAlarmDescription(alarmCodeEnum.getDescription());
        dto.setEquipId(equipId);
        dto.setEquipType(equipType);
        dto.setAlarmCode(alarmCodeEnum.getCode());
        dto.setStartDate(new Date());
        dto.setSystemId(Constants.SYSTEM_ID);
        dto.setStatus(status);
        dto.setAckDate(new Date());
        return dto;
    }

    public static <T extends Serializable> void sendJms(JmsTemplate jmsTemplate,
                                                        String destination, T msg, String correlationId) {
        PaJmsMessage<T> lusJmsMessage = new PaJmsMessage<>(msg, correlationId);
        jmsTemplate.convertAndSend(destination, lusJmsMessage);
        log.info("Sent JMS msg {} to {}", msg, destination);
    }
}
