package com.stee.emasext.emaslus.messages.processor.responses;

import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.CmdRespDto;
import com.stee.emasext.emaslus.config.PropertiesConfig;
import com.stee.emasext.emaslus.dao.interfaces.primary.LusEquipConfigRepository;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.entities.primary.TunnelEquipStatus;
import com.stee.emasext.emaslus.enums.EmasControlCommandEnum;
import com.stee.emasext.emaslus.enums.EmasLusAttrCodeEnum;
import com.stee.emasext.emaslus.enums.StatusCodeEnum;
import com.stee.emasext.emaslus.messages.BaseMessage;
import com.stee.emasext.emaslus.services.primary.TunnelEquipStatusService;
import com.stee.emasext.emaslus.utils.ExecuteCommandUtils;
import com.stee.emasext.emaslus.utils.MessageTransmitUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Wang Yu
 * Created at 2023/5/4
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RespParameterProcessor implements LusRespMessageProcessor {
    private final LusEquipConfigRepository lusEquipConfigRepository;
    private final TunnelEquipStatusService tunnelEquipStatusService;

    private final PropertiesConfig propertiesConfig;
    private final JmsTemplate queueTemplate;

    @Override
    public void process(String controllerId, BaseMessage baseMessage) {
        log.info("Processing Response Parameter Command: {} ", baseMessage);
        String onTimeAttr = EmasLusAttrCodeEnum.FLASH_RATE_ON.getAttrCode();
        String offTimeAttr = EmasLusAttrCodeEnum.FLASH_RATE_OFF.getAttrCode();

        if (!baseMessage.getStatusCodeEnum().equals(StatusCodeEnum.RESPONSE_OK)) {
            log.error("RespParameterProcessor: received status {}: {}",
                    baseMessage.getStatusCodeEnum().name(),
                    baseMessage);
            return;
        }

        byte[] optionalParameter = baseMessage.getOptionalParameter();
        byte flashRateOn = optionalParameter[0];
        byte flashRateOff = optionalParameter[1];

        List<LusEquipConfig> lusEquipList = lusEquipConfigRepository.getLusEquipConfigsByControllerId(controllerId);

        StatusCodeEnum statusCodeEnum = baseMessage.getStatusCodeEnum();
        Optional<CmdRespDto> cmdRespDtoOptional = ExecuteCommandUtils.checkExecutingMapThenCreateCmdRespDto(controllerId,
                statusCodeEnum, propertiesConfig.getLusResponseValidTime());
        if (cmdRespDtoOptional.isPresent()) {
            MessageTransmitUtils.sendJms(queueTemplate, propertiesConfig.getJmsLusCmhQueue(),
                    cmdRespDtoOptional.get(), MessageConstants.CMD_RESP_ID);
        } else {
            log.info("RespParameterProcessor: No Valid Executing command: {}", controllerId);
        }

        if (StatusCodeEnum.RESPONSE_OK.equals(statusCodeEnum)) {
            // update all equip under this Controller
            for (LusEquipConfig lus : lusEquipList) {
                TunnelEquipStatus onStatus = TunnelEquipStatus.builder()
                        .equipId(lus.getEquipId())
                        .attrCode(onTimeAttr)
                        .attrValue(flashRateOn & 0xff)
                        .build();
                TunnelEquipStatus offStatus = TunnelEquipStatus.builder()
                        .equipId(lus.getEquipId())
                        .attrCode(offTimeAttr)
                        .attrValue(flashRateOff & 0xff)
                        .build();

                tunnelEquipStatusService.saveStatus(onStatus);
                tunnelEquipStatusService.saveStatus(offStatus);
            }
        } else {
            log.error("RespParameterProcessor.process: The response didn't indicate SUCCESS. controllerId {}, status {}",
                    controllerId, statusCodeEnum.getDescription());
        }
    }
}
