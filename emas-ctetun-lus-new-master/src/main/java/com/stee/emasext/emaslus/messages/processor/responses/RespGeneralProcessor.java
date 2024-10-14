package com.stee.emasext.emaslus.messages.processor.responses;

import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.CmdRespDto;
import com.stee.emasext.emaslus.config.PropertiesConfig;
import com.stee.emasext.emaslus.enums.StatusCodeEnum;
import com.stee.emasext.emaslus.messages.BaseMessage;
import com.stee.emasext.emaslus.services.primary.LusEquipConfigService;
import com.stee.emasext.emaslus.utils.ExecuteCommandUtils;
import com.stee.emasext.emaslus.utils.GlobalVariable;
import com.stee.emasext.emaslus.utils.MessageTransmitUtils;
import com.stee.emasext.emaslus.vo.ExecutingCommandVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Queue;

/**
 * @author Wang Yu
 * Created at 2023/5/15
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class RespGeneralProcessor implements LusRespMessageProcessor {

    private final PropertiesConfig propertiesConfig;

    private final LusEquipConfigService lusEquipConfigService;
    private final JmsTemplate queueTemplate;

    @Override
    public void process(String controllerId, BaseMessage baseMessage) {
        // need to verify which equip corresponding.
        // Unknowing equip response corresponded due to limited functionality
        // DEFECT: due to limited functionality: can't detect which equip should send accordingly
        log.info("Processing General Response Message: {}", baseMessage);
        StatusCodeEnum statusCodeEnum = baseMessage.getStatusCodeEnum();

        // get current map value and check its validity
        Optional<CmdRespDto> cmdRespDtoOptional = ExecuteCommandUtils.checkExecutingMapThenCreateCmdRespDto(controllerId,
                statusCodeEnum, propertiesConfig.getLusResponseValidTime());
        if (cmdRespDtoOptional.isPresent()) {
            MessageTransmitUtils.sendJms(queueTemplate, propertiesConfig.getJmsLusCmhQueue(),
                    cmdRespDtoOptional.get(), MessageConstants.CMD_RESP_ID);
        } else {
            log.info("RespGeneralProcessor: No Valid Executing command: {}", controllerId);
        }

    }
}
