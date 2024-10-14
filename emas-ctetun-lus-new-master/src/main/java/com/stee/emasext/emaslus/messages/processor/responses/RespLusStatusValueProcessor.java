package com.stee.emasext.emaslus.messages.processor.responses;

import com.stee.emasext.emaslus.dao.interfaces.primary.LusEquipConfigRepository;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.entities.primary.TunnelEquipStatus;
import com.stee.emasext.emaslus.enums.AlarmCodeEnum;
import com.stee.emasext.emaslus.enums.EmasLusAttrCodeEnum;
import com.stee.emasext.emaslus.enums.StatusCodeEnum;
import com.stee.emasext.emaslus.messages.BaseMessage;
import com.stee.emasext.emaslus.messages.parameters.LusStatusValueParam;
import com.stee.emasext.emaslus.services.primary.TunnelEquipStatusService;
import com.stee.emasext.emaslus.utils.LusCommonUtils;
import com.stee.emasext.emaslus.utils.LusConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Wang Yu
 * Created at 2023/5/4
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RespLusStatusValueProcessor implements LusRespMessageProcessor {

    private final LusEquipConfigRepository lusEquipConfigRepository;
    private final TunnelEquipStatusService tunnelEquipStatusService;

    @Override
    @Transactional
    public void process(String controllerId, BaseMessage baseMessage) {

        final StatusCodeEnum statusCodeEnum = baseMessage.getStatusCodeEnum();
        if (!statusCodeEnum.equals(StatusCodeEnum.RESPONSE_OK)) {
            log.error("RespLusStatusValueProcessor.process: The response didn't indicate SUCCESS. controllerId {}, status {}",
                    controllerId, statusCodeEnum.getDescription());
            return;
        }

        int numberOfEquips = lusEquipConfigRepository.countByControllerId(controllerId);

        List<LusStatusValueParam> statusList = LusCommonUtils.getStatusParamListFromMessage(baseMessage, numberOfEquips, LusStatusValueParam::new);
        List<LusEquipConfig> lusEquips = lusEquipConfigRepository.getLusEquipConfigsByControllerId(controllerId);

        // save it to DB
        for (int i = 0; i < statusList.size(); ++i) {
            TunnelEquipStatus photoSensor = TunnelEquipStatus.builder()
                    .equipId(lusEquips.get(i).getEquipId())
                    .attrCode(EmasLusAttrCodeEnum.PHOTO_SENSOR.getAttrCode())
                    .attrValue((int)statusList.get(i).getPhotoSensorValue())
                    .build();

            TunnelEquipStatus temperature = TunnelEquipStatus.builder()
                    .equipId(lusEquips.get(i).getEquipId())
                    .attrCode(EmasLusAttrCodeEnum.TEMPERATURE.getAttrCode())
                    .attrValue((int)statusList.get(i).getTemperature())
                    .build();

            TunnelEquipStatus dimmingValue = TunnelEquipStatus.builder()
                    .equipId(lusEquips.get(i).getEquipId())
                    .attrCode(EmasLusAttrCodeEnum.DIMMING_VALUE.getAttrCode())
                    .attrValue((int)statusList.get(i).getCurrentDimmingValue())
                    .build();

            // alarms
            TunnelEquipStatus pixelFailureOfRed = TunnelEquipStatus.builder()
                    .equipId(lusEquips.get(i).getEquipId())
                    .attrCode(AlarmCodeEnum.RED_PIXEL_FAILURE.getAttrCode())
                    .attrValue((int)statusList.get(i).getRedPixelFailure() > 0 ?
                            LusConstants.LUS_EQUIP_ABNORMAL : LusConstants.LUS_EQUIP_NORMAL)
                    .build();
            TunnelEquipStatus pixelFailureOfGreen = TunnelEquipStatus.builder()
                    .equipId(lusEquips.get(i).getEquipId())
                    .attrCode(AlarmCodeEnum.GREEN_PIXEL_FAILURE.getAttrCode())
                    .attrValue((int)statusList.get(i).getGreenPixelFailure() > 0 ?
                            LusConstants.LUS_EQUIP_ABNORMAL : LusConstants.LUS_EQUIP_NORMAL)
                    .build();
            TunnelEquipStatus pixelFailureOfAmber = TunnelEquipStatus.builder()
                    .equipId(lusEquips.get(i).getEquipId())
                    .attrCode(AlarmCodeEnum.AMBER_PIXEL_FAILURE.getAttrCode())
                    .attrValue((int)statusList.get(i).getAmberPixelFailure() > 0 ?
                            LusConstants.LUS_EQUIP_ABNORMAL : LusConstants.LUS_EQUIP_NORMAL)
                    .build();

            tunnelEquipStatusService.saveStatus(photoSensor);
            tunnelEquipStatusService.saveStatus(temperature);
            tunnelEquipStatusService.saveStatus(dimmingValue);
            tunnelEquipStatusService.saveStatus(pixelFailureOfRed);
            tunnelEquipStatusService.saveStatus(pixelFailureOfGreen);
            tunnelEquipStatusService.saveStatus(pixelFailureOfAmber);

        } // end of for: statusList
    }
}
