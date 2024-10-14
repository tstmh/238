package com.stee.emasext.emaslus.jms.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.tunnel.TunnelRemoteDto;
import com.stee.emas.common.tunnel.lus.LusDimmingRemoteVO;
import com.stee.emas.common.tunnel.lus.LusFlashRateRemoteVO;
import com.stee.emasext.emaslus.config.PropertiesConfig;
import com.stee.emasext.emaslus.config.WeblogicContextConfig;
import com.stee.emasext.emaslus.dao.interfaces.primary.EquipCtrlStatusRepository;
import com.stee.emasext.emaslus.dao.interfaces.primary.HistCtrlRecordRepository;
import com.stee.emasext.emaslus.dao.interfaces.primary.LusEquipConfigRepository;
import com.stee.emasext.emaslus.entities.primary.EquipCtrlStatus;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.enums.EmasControlCommandEnum;
import com.stee.emasext.emaslus.jms.LusJmsMessage;
import com.stee.emasext.emaslus.messages.processor.requests.DisplayReqMessageProcessor;
import com.stee.emasext.emaslus.messages.processor.requests.SetupDimmingProcessor;
import com.stee.emasext.emaslus.messages.processor.requests.SetupFlashRateProcessor;
import com.stee.emasext.emaslus.services.primary.EquipCtrlStatusService;
import com.stee.emasext.emaslus.utils.GlobalVariable;
import com.stee.emasext.emaslus.utils.LusCommonUtils;
import com.stee.emasext.emaslus.utils.LusConstants;
import com.stee.emasext.emaslus.vo.ExecutingCommandVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

@Component
@Slf4j(topic = "jmsLogger")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ConditionalOnBean(WeblogicContextConfig.class)
public class LusJmsListener {
    private final LusEquipConfigRepository lusEquipConfigRepository;
    private final EquipCtrlStatusService equipCtrlStatusService;

    private final PropertiesConfig propertiesConfig;
    private final ObjectMapper objectMapper;
    private final DisplayReqMessageProcessor displayReqMessageProcessor;
    private final SetupDimmingProcessor setupDimmingProcessor;
    private final SetupFlashRateProcessor setupFlashRateProcessor;
    private final HistCtrlRecordRepository histCtrlRecordRepository;

    @JmsListener(destination = "${stee.jms-cmh-lus-queue}", containerFactory = "queueListenerFactory")
    public void receiveMessage(LusJmsMessage<Serializable> message) throws IOException {
        log.info("Received message from queue [{}]: {}", propertiesConfig.getJmsCmhLusQueue(), message);
        String jmsCorrelationID = message.getCorrelationId();
        String json = objectMapper.writeValueAsString(message.getData());
        log.info("Received LusJmsMessage from queue [{}]: {}; correlationID: {}",
                propertiesConfig.getJmsCmhLusQueue(), json, jmsCorrelationID);
        if (StringUtils.isEmpty(jmsCorrelationID)) {
            log.warn("Received null/empty CorrelationID, drop it");
            return;
        }
        // save it into command history table
        TunnelRemoteDto commandVO = LusCommonUtils.getDataFromLusJmsMessage(message, TunnelRemoteDto.class);
        EquipCtrlStatus equipCtrlStatus = new EquipCtrlStatus();
        equipCtrlStatus.setEquipId(commandVO.getEquipId());
        equipCtrlStatus.setCtrlCode(commandVO.getAttributeId());
        equipCtrlStatus.setCtrlValue(commandVO.getCmdValue());
        equipCtrlStatusService.save(equipCtrlStatus);
        // process
        switch (jmsCorrelationID) {
            case MessageConstants.TUNNEL_CTRL_ID:
                String attributeId = commandVO.getAttributeId();
                putIntoExecutingCommandMap(commandVO);
                if (EmasControlCommandEnum.DISPLAY_MESSAGE.getEmasAttr().equals(attributeId)) {
                    log.info("tackling with Display message... {}", commandVO);
                    displayReqMessageProcessor.doProcess(histCtrlRecordRepository, commandVO);
                }
                break;
            case MessageConstants.LUS_SETUP_DIMMING:
                LusDimmingRemoteVO setupDimmingVO = LusCommonUtils.getDataFromLusJmsMessage(message, LusDimmingRemoteVO.class);
                log.info("tackling with Setup Dimming message... {}", setupDimmingVO);
                putIntoExecutingCommandMap(setupDimmingVO);
                setupDimmingProcessor.doProcess(histCtrlRecordRepository, setupDimmingVO);
                break;
            case MessageConstants.LUS_SETUP_FLASH_RATE:
                LusFlashRateRemoteVO setupFlashRateVO = LusCommonUtils.getDataFromLusJmsMessage(message, LusFlashRateRemoteVO.class);
                log.info("tackling with Setup FlashRate message... {}", setupFlashRateVO);
                putIntoExecutingCommandMap(setupFlashRateVO);
                setupFlashRateProcessor.doProcess(histCtrlRecordRepository, setupFlashRateVO);
                break;
            default:
                log.warn("No handler for correlationID: {}", jmsCorrelationID);
                break;
        }

    }

    @PostConstruct
    public void onceCreated() {
        log.info("Starting to listen queue [{}]",
                propertiesConfig.getJmsCmhLusQueue());
    }

    private void putIntoExecutingCommandMap(TunnelRemoteDto commandVO) {
        Optional<LusEquipConfig> equipOptional = lusEquipConfigRepository.findById(commandVO.getEquipId());
        LusEquipConfig equip = equipOptional.orElseThrow(() ->
                new IllegalArgumentException("Equip ID not found: " + commandVO.getEquipId()));
        String controllerId = equip.getControllerId();
        Queue<ExecutingCommandVO> queue = GlobalVariable.executingCommandVOQueue
                .computeIfAbsent(controllerId, (k) -> new LinkedList<>());

        ExecutingCommandVO executingCommandVO = ExecutingCommandVO.builder()
                .execId(commandVO.getExecId())
                .cmdId(commandVO.getCmdId())
                .equipId(equip.getEquipId())
                .equipType(equip.getEquipType())
                .sender(LusConstants.LUS_FELS_CODE)
                .startTime(LocalDateTime.now())
                .build();

        queue.offer(executingCommandVO);
    }

}
