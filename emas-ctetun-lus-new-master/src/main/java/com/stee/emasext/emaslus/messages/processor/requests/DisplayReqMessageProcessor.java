package com.stee.emasext.emaslus.messages.processor.requests;

import com.stee.emas.common.tunnel.TunnelRemoteDto;
import com.stee.emasext.emaslus.dao.interfaces.primary.LusEquipConfigRepository;
import com.stee.emasext.emaslus.dao.interfaces.primary.TunnelEquipAttrStatusRepository;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.entities.primary.TunnelEquipStatus;
import com.stee.emasext.emaslus.entities.primary.ids.TunnelEquipStatusId;
import com.stee.emasext.emaslus.enums.CommandCodeEnum;
import com.stee.emasext.emaslus.enums.DisplayMessageEnum;
import com.stee.emasext.emaslus.enums.EmasLusAttrCodeEnum;
import com.stee.emasext.emaslus.utils.MessageTransmitUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Wang Yu
 * Created at 2023/5/3
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DisplayReqMessageProcessor implements LusReqMessageProcessor<TunnelRemoteDto> {

    private final LusEquipConfigRepository lusEquipConfigRepository;
    private final TunnelEquipAttrStatusRepository tunnelEquipAttrStatusRepository;

    @Override
    @Transactional
    public void process(TunnelRemoteDto message) {
        // 1. Get the controller of the target equip
        // 2. Get all the equipment of the controller, in ordered
        // 3. Get all current equipment displays
        // 4. only change the target equipment display
        // 5. assemble the message, send to LUS controller

        log.info("Processing Display Message: {}", message);
        Optional<LusEquipConfig> targetOptional = lusEquipConfigRepository.findById(message.getEquipId());
        if (targetOptional.isPresent()) {
            LusEquipConfig targetEquip = targetOptional.get();
            String targetControllerId = targetEquip.getControllerId();
            if (targetControllerId == null) {
                log.info("DisplayMessageProcessor: target equip {} doesn't assign any controller", targetEquip.getEquipId());
                return;
            }
            List<LusEquipConfig> allEquips = lusEquipConfigRepository
                    .getLusEquipConfigsByControllerId(targetControllerId);
            int size = allEquips.size();
            if (size > 8) {
                throw new IllegalArgumentException("The size cannot excess 8");
            }
            byte[] parameters = new byte[8];
            Arrays.fill(parameters, (byte) 0xff);

            int targetPosition = targetEquip.getAssignPosition();

            TunnelEquipStatus defaultStatus = TunnelEquipStatus.builder()
                    .attrCode(EmasLusAttrCodeEnum.DISPLAY_MESSAGE.name())
                    .attrValue(DisplayMessageEnum.ALL_OFF.getCode())
                    .build();

            for (int i = 0; i < size; ++i) {
                LusEquipConfig lusEquipConfig = allEquips.get(i);
                TunnelEquipStatus currentStatus = tunnelEquipAttrStatusRepository.findById(new TunnelEquipStatusId(lusEquipConfig.getEquipId(),
                                EmasLusAttrCodeEnum.DISPLAY_MESSAGE.getAttrCode()))
                        .orElseGet(() -> {
                            defaultStatus.setEquipId(lusEquipConfig.getEquipId());
                            return defaultStatus;
                        });

                log.info("Equip {}, display message {}", lusEquipConfig.getEquipId(), currentStatus.getAttrValue());
                // set for only target equip
                if (i == targetPosition - 1) {
                    parameters[i] = message.getCmdValue().byteValue();
                } else {
                    parameters[i] = currentStatus.getAttrValue().byteValue();
                }
            }

            // send to LUS
            MessageTransmitUtils.sendMessageToSocket(CommandCodeEnum.DISPLAY_MESSAGE, parameters, targetControllerId);
            try {
                TimeUnit.SECONDS.sleep(2L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            MessageTransmitUtils.sendMessageToSocket(CommandCodeEnum.REQ_LUS_STATUS, null, targetControllerId);

        }

    }
}
