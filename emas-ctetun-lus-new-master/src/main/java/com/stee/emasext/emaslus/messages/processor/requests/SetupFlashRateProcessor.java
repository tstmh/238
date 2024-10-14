package com.stee.emasext.emaslus.messages.processor.requests;

import com.stee.emas.common.tunnel.lus.LusFlashRateRemoteVO;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.enums.CommandCodeEnum;
import com.stee.emasext.emaslus.exceptions.LusControlException;
import com.stee.emasext.emaslus.services.primary.LusEquipConfigService;
import com.stee.emasext.emaslus.utils.LusCommonUtils;
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
 * Created at 2023/5/9
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SetupFlashRateProcessor implements LusReqMessageProcessor<LusFlashRateRemoteVO> {

    private final LusEquipConfigService lusEquipConfigService;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    @Transactional
    public void process(LusFlashRateRemoteVO message) {
        log.info("Processing Setup FlashRate Request... {}", message);
        final String targetControllerId = LusCommonUtils.getTargetEquipController(message.getEquipId(), lusEquipConfigService);
        byte[] params = new byte[6];
        params[0] = 0x01;
        params[1] = (byte) message.getFlashOn();
        params[2] = (byte) message.getFlashOff();
        // TODO handle rest value
        params[3] = 0x02;
        params[4] = 0x58;
        params[5] = 0x06;
        MessageTransmitUtils.sendMessageToSocket(CommandCodeEnum.REQ_PARAMETER, params, targetControllerId);
        // need to get the update
        threadPoolTaskExecutor.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(2L);
                MessageTransmitUtils.sendMessageToSocket(CommandCodeEnum.REQ_DIMMING_STATUS, targetControllerId);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
