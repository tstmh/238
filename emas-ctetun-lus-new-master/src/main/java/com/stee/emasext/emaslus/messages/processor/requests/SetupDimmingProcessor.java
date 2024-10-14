package com.stee.emasext.emaslus.messages.processor.requests;

import com.stee.emas.common.tunnel.lus.LusDimmingRemoteVO;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.enums.CommandCodeEnum;
import com.stee.emasext.emaslus.exceptions.LusControlException;
import com.stee.emasext.emaslus.services.primary.LusEquipConfigService;
import com.stee.emasext.emaslus.utils.LusCommonUtils;
import com.stee.emasext.emaslus.utils.LusConstants;
import com.stee.emasext.emaslus.utils.MessageTransmitUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Wang Yu
 * Created at 2023/5/5
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SetupDimmingProcessor implements LusReqMessageProcessor<LusDimmingRemoteVO> {

    private final LusEquipConfigService lusEquipConfigService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    @Transactional
    public void process(LusDimmingRemoteVO message) {
        log.info("Processing Setup Dimming Request... {}", message);
        final String targetControllerId = LusCommonUtils.getTargetEquipController(message.getEquipId(), lusEquipConfigService);
        byte[] params = new byte[2];
        Integer dimmingLevel = message.getCmdValue();
        Character dimmingMode = message.getDimmingMode();
        params[0] = (byte) dimmingMode.charValue();
        params[1] = dimmingLevel.byteValue();
        MessageTransmitUtils.sendMessageToSocket(CommandCodeEnum.SETUP_DIMMING, params, targetControllerId);
        // need to get the update
        threadPoolTaskExecutor.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(2L);
                MessageTransmitUtils.sendMessageToSocket(CommandCodeEnum.REQ_PARAMETER, LusConstants.PARAMETER_ID_REQUEST, targetControllerId);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
