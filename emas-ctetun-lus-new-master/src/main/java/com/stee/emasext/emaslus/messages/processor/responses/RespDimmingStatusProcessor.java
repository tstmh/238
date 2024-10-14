package com.stee.emasext.emaslus.messages.processor.responses;

import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.CmdRespDto;
import com.stee.emasext.emaslus.config.PropertiesConfig;
import com.stee.emasext.emaslus.dao.interfaces.primary.LusEquipConfigRepository;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.entities.primary.TunnelEquipStatus;
import com.stee.emasext.emaslus.enums.DimmingModeEnum;
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
public class RespDimmingStatusProcessor implements LusRespMessageProcessor {

    private final LusEquipConfigRepository lusEquipConfigRepository;
    private final TunnelEquipStatusService tunnelEquipStatusService;

    private final PropertiesConfig propertiesConfig;
    private final JmsTemplate queueTemplate;

    @Override
    public void process(String controllerId, BaseMessage baseMessage) {
        // receive the dimming mode of all equip under the Controller
        String attrCode = EmasLusAttrCodeEnum.DIMMING_MODE.getAttrCode();

        byte[] optionalParameter = baseMessage.getOptionalParameter();
        int dimmingMode = optionalParameter[12] & 0xff;
        DimmingModeEnum dimmingModeEnum = DimmingModeEnum.getDimmingModeEnumByMode((char) dimmingMode);

        List<LusEquipConfig> lusEquipList = lusEquipConfigRepository.getLusEquipConfigsByControllerId(controllerId);

        StatusCodeEnum statusCodeEnum = baseMessage.getStatusCodeEnum();
        Optional<CmdRespDto> cmdRespDtoOptional = ExecuteCommandUtils.checkExecutingMapThenCreateCmdRespDto(controllerId,
                statusCodeEnum, propertiesConfig.getLusResponseValidTime());
        if (cmdRespDtoOptional.isPresent()) {
            MessageTransmitUtils.sendJms(queueTemplate, propertiesConfig.getJmsLusCmhQueue(),
                    cmdRespDtoOptional.get(), MessageConstants.CMD_RESP_ID);
        } else {
            log.info("RespDimmingStatusProcessor: No Valid Executing command: {}", controllerId);
        }

        if (statusCodeEnum.equals(StatusCodeEnum.RESPONSE_OK)) {
            // update all equip under this Controller
            for (LusEquipConfig lus : lusEquipList) {
                // update the dimming mode
                TunnelEquipStatus status = TunnelEquipStatus.builder()
                        .equipId(lus.getEquipId())
                        .attrCode(attrCode)
                        .attrValue(dimmingModeEnum.getValue())
                        .build();
                tunnelEquipStatusService.saveStatus(status);
            }
        } else {
            log.error("RespDimmingStatusProcessor.process: The response didn't indicate SUCCESS. controllerId {}, status {}",
                    controllerId, statusCodeEnum.getDescription());
        }
    }
}
