package com.stee.emasext.emaslus.messages.processor.responses;

import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emasext.emaslus.config.PropertiesConfig;
import com.stee.emasext.emaslus.dao.interfaces.primary.LusEquipConfigRepository;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.entities.primary.TunnelEquipStatus;
import com.stee.emasext.emaslus.enums.AlarmCodeEnum;
import com.stee.emasext.emaslus.enums.EmasLusAttrCodeEnum;
import com.stee.emasext.emaslus.enums.StatusCodeEnum;
import com.stee.emasext.emaslus.messages.BaseMessage;
import com.stee.emasext.emaslus.messages.parameters.LusStatusParam;
import com.stee.emasext.emaslus.services.primary.TunnelEquipStatusService;
import com.stee.emasext.emaslus.utils.LusCommonUtils;
import com.stee.emasext.emaslus.utils.LusConstants;
import com.stee.emasext.emaslus.utils.MessageTransmitUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Wang Yu
 * Created at 2023/5/4
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RespLusStatusProcessor implements LusRespMessageProcessor {

    private final LusEquipConfigRepository lusEquipConfigRepository;
    private final TunnelEquipStatusService tunnelEquipStatusService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final PropertiesConfig propertiesConfig;
    private final Optional<JmsTemplate> queueTemplate;

    @Override
    public void process(String controllerId, BaseMessage baseMessage) {
        final StatusCodeEnum statusCodeEnum = baseMessage.getStatusCodeEnum();
        if (!statusCodeEnum.equals(StatusCodeEnum.RESPONSE_OK)) {
            log.error("RespLusStatusProcessor.process: The response didn't indicate SUCCESS. controllerId {}, status {}",
                    controllerId, statusCodeEnum.getDescription());
            return;
        }

        int numberOfEquips = lusEquipConfigRepository.countByControllerId(controllerId);

        List<LusStatusParam> statusList = LusCommonUtils.getStatusParamListFromMessage(baseMessage, numberOfEquips,
                LusStatusParam::new);
        List<LusEquipConfig> lusEquips = lusEquipConfigRepository.getLusEquipConfigsByControllerId(controllerId);

        // save it to DB
        for (int i = 0; i < statusList.size(); ++i) {
            final LusStatusParam lusStatusParam = statusList.get(i);

            final LusEquipConfig equip = lusEquips.get(i);
            TunnelEquipStatus opeStatus = TunnelEquipStatus.builder()
                    .equipId(equip.getEquipId())
                    .attrCode(EmasLusAttrCodeEnum.OPERATION_STATUS.getAttrCode())
                    .attrValue(lusStatusParam.getCommStatus() == LusConstants.LUS_EQUIP_ABNORMAL ?
                            LusConstants.LUS_OPE_ABNORMAL : LusConstants.LUS_OPE_NORMAL)
                    .build();

            TunnelEquipStatus displayMessage = TunnelEquipStatus.builder()
                    .equipId(equip.getEquipId())
                    .attrCode(EmasLusAttrCodeEnum.DISPLAY_MESSAGE.getAttrCode())
                    .attrValue((int) lusStatusParam.getCurrentMessage())
                    .build();

            TunnelEquipStatus dimmingLevel = TunnelEquipStatus.builder()
                    .equipId(equip.getEquipId())
                    .attrCode(EmasLusAttrCodeEnum.DIMMING_LEVEL.getAttrCode())
                    .attrValue((int) lusStatusParam.getDimmingLevel())
                    .build();

            // alarms
            TunnelEquipStatus commStatusAlarm = TunnelEquipStatus.builder()
                    .equipId(equip.getEquipId())
                    .attrCode(AlarmCodeEnum.COMMUNICATION_DOWN.getAttrCode())
                    .attrValue(LusCommonUtils.convertAlarmValueToEmasSystem(lusStatusParam.getCommStatus()))
                    .build();
            TunnelEquipStatus temperatureAlarm = TunnelEquipStatus.builder()
                    .equipId(equip.getEquipId())
                    .attrCode(AlarmCodeEnum.SIGN_TEMPERATURE_THRESHOLD.getAttrCode())
                    .attrValue(LusCommonUtils.convertAlarmValueToEmasSystem(lusStatusParam.getTemperature()))
                    .build();
            TunnelEquipStatus photoSensorAlarm = TunnelEquipStatus.builder()
                    .equipId(equip.getEquipId())
                    .attrCode(AlarmCodeEnum.PHOTO_SENSOR_FAILURE.getAttrCode())
                    .attrValue(LusCommonUtils.convertAlarmValueToEmasSystem(lusStatusParam.getPhotoSensor()))
                    .build();
            TunnelEquipStatus pixelFailureAlarm = TunnelEquipStatus.builder()
                    .equipId(equip.getEquipId())
                    .attrCode(AlarmCodeEnum.PIXEL_FAILURE_ALARM.getAttrCode())
                    .attrValue(LusCommonUtils.convertAlarmValueToEmasSystem(lusStatusParam.getPixelFailureAlarm()))
                    .build();
            TunnelEquipStatus pixelFailureOfFullDisplay = TunnelEquipStatus.builder()
                    .equipId(equip.getEquipId())
                    .attrCode(AlarmCodeEnum.PIXEL_FAILURE_OF_FULL_DISPLAY.getAttrCode())
                    .attrValue(LusCommonUtils
                            .convertAlarmValueToEmasSystem(
                                    lusStatusParam.getPixelFailureOfFullDisplay() != 100 ?
                                            LusConstants.LUS_EQUIP_ABNORMAL : LusConstants.LUS_EQUIP_NORMAL
                            ))
                    .build();
            TunnelEquipStatus ioTestFailureAlarm = TunnelEquipStatus.builder()
                    .equipId(equip.getEquipId())
                    .attrCode(AlarmCodeEnum.IO_TEST_FAILURE.getAttrCode())
                    .attrValue(LusCommonUtils.convertAlarmValueToEmasSystem(lusStatusParam.getIoTestFailure()))
                    .build();

            tunnelEquipStatusService.saveStatus(opeStatus);
            tunnelEquipStatusService.saveStatus(displayMessage);
            tunnelEquipStatusService.saveStatus(dimmingLevel);
            tunnelEquipStatusService.saveStatus(commStatusAlarm);
            tunnelEquipStatusService.saveStatus(temperatureAlarm);
            tunnelEquipStatusService.saveStatus(photoSensorAlarm);
            tunnelEquipStatusService.saveStatus(pixelFailureAlarm);
            tunnelEquipStatusService.saveStatus(pixelFailureOfFullDisplay);
            tunnelEquipStatusService.saveStatus(ioTestFailureAlarm);
        } // end of for: statusList
    }
}
