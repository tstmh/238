package com.stee.emasext.emaslus.utils;

import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.enums.AlarmCodeEnum;
import com.stee.emasext.emaslus.exceptions.LUSMessageEncodeException;
import com.stee.emasext.emaslus.exceptions.LusControlException;
import com.stee.emasext.emaslus.jms.LusJmsMessage;
import com.stee.emasext.emaslus.messages.BaseMessage;
import com.stee.emasext.emaslus.services.primary.LusEquipConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

/**
 * @author Wang Yu
 * Created at 2023/2/17
 */
@Slf4j
public class LusCommonUtils {
    private static final int MESSAGE_LENGTH = 8;

    private LusCommonUtils() {
    }

    public static String getByteHexString(byte[] bytes) {
        String[] hexStringArray = new String[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            hexStringArray[i] = Integer.toHexString(bytes[i] & 0xff);
        }
        return Arrays.toString(hexStringArray);
    }

    public static <R> List<R> getStatusParamListFromMessage(BaseMessage baseMessage,
                                                            int expectedNumber, Function<byte[], R> supplier) {
        byte[] optionalParameter = baseMessage.getOptionalParameter();
        if (optionalParameter.length == 0 || optionalParameter.length % 8 != 0) {
            throw new LUSMessageEncodeException("The data from LUS is invalid");
        }
        int numOfMessages = optionalParameter.length / MESSAGE_LENGTH;
        if (expectedNumber != numOfMessages) {
            log.info("The real number of equip is {}", expectedNumber);
        }

        List<R> statusList = new ArrayList<>();
        for (int i = 0; i < expectedNumber; i++) {
            byte[] messageBytes = new byte[MESSAGE_LENGTH];
            System.arraycopy(optionalParameter, i * MESSAGE_LENGTH, messageBytes, 0, MESSAGE_LENGTH);
            R message = supplier.apply(messageBytes);
            statusList.add(message);
        }
        return statusList;
    }

    public static String generateAlarmId(String equipType, String equipId, int alarmCode) {
        return String.format("emas_%s_%s_%d", equipType, equipId, alarmCode);
    }

    public static String generateAlarmId(LusEquipConfig lusEquipConfig, int alarmCode) {
        return generateAlarmId(lusEquipConfig.getEquipType(), lusEquipConfig.getEquipId(), alarmCode);
    }

    public static TechnicalAlarmDto createAlarmDtoRaise(LusEquipConfig equip, AlarmCodeEnum alarmCodeEnum) {
        return createAlarmDto(equip, alarmCodeEnum, Constants.ALARM_RAISED);
    }

    public static TechnicalAlarmDto createAlarmDtoClear(LusEquipConfig equip, AlarmCodeEnum alarmCodeEnum) {
        return createAlarmDto(equip, alarmCodeEnum, Constants.ALARM_CLEARED);
    }

    public static TechnicalAlarmDto createAlarmDto(LusEquipConfig equip, AlarmCodeEnum alarmCodeEnum, int status) {
        TechnicalAlarmDto dto = new TechnicalAlarmDto();
        dto.setAlarmId(LusCommonUtils.generateAlarmId(equip, alarmCodeEnum.getAlarmCode()));
        dto.setAlarmDescription(alarmCodeEnum.getDescription());
        dto.setEquipId(equip.getEquipId());
        dto.setEquipType(equip.getEquipType());
        dto.setAlarmCode(alarmCodeEnum.getAlarmCode());
        dto.setStartDate(new Date());
        dto.setSystemId(Constants.SYSTEM_ID);
        dto.setStatus(status);
        return dto;
    }

    public static void addToAlarmListConditionally(int paramFromLus, List<TechnicalAlarmDto> list,
                                                   LusEquipConfig equip, AlarmCodeEnum alarmCodeEnum) {
        if (list == null) {
            throw new IllegalArgumentException("TechnicalAlarmDtoList cannot be null");
        }
        TechnicalAlarmDto dto;
        if (paramFromLus == Constants.ALARM_RAISED) {
            // raise an alarm
            dto = LusCommonUtils.createAlarmDtoRaise(equip, alarmCodeEnum);
            // put into set
        } else {
            // clear it if it required
            dto = LusCommonUtils.createAlarmDtoClear(equip, alarmCodeEnum);
        }
        list.add(dto);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T getDataFromLusJmsMessage(LusJmsMessage<?> jmsMessage, Class<T> clazz) {
        return (T) jmsMessage.getData();
    }

    public static int convertAlarmValueToEmasSystem(int value) {
        Integer integer = LusConstants.LUS_ALARM_MAPPING.get(value);
        if (integer == null) {
            throw new IllegalArgumentException("convertAlarmValueToEmasSystem: No mapping for alarm value: " + value);
        }
        return integer;
    }

    public static String getTargetEquipController(String equipId, LusEquipConfigService lusEquipConfigService) {
        Optional<LusEquipConfig> equipOptional = lusEquipConfigService.getEquipById(equipId);
        if (!equipOptional.isPresent()) {
            log.error("Equip {} not found", equipId);
            throw new LusControlException("Equip not found: " + equipId);
        }
        LusEquipConfig lusEquip = equipOptional.get();
        String targetControllerId = lusEquip.getControllerId();
        if (StringUtils.isEmpty(targetControllerId)) {
            throw new LusControlException("equip is a controller or it doesn't have a controller: " + equipId);
        }
        return targetControllerId;
    }

}
