package com.stee.emasext.emaslus.schedules;

import com.stee.emasext.emaslus.config.PropertiesConfig;
import com.stee.emasext.emaslus.enums.CommandCodeEnum;
import com.stee.emasext.emaslus.services.secondary.EquipStatusService;
import com.stee.emasext.emaslus.utils.DateTimeUtils;
import com.stee.emasext.emaslus.utils.GlobalVariable;
import com.stee.emasext.emaslus.utils.LusConstants;
import com.stee.emasext.emaslus.utils.MessageTransmitUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Wang Yu
 * Created at 2022/12/21
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateEquipStatusSchedule {

    private final PropertiesConfig propertiesConfig;
    private final EquipStatusService equipStatusService;

    @Scheduled(fixedRateString = "${stee.lus-refresh-status-rate}", initialDelay = 1000)
    public void requestEquipStatus() {
        try {
            log.info("requestEquipStatus task starts");
            if (GlobalVariable.channelMap.isEmpty()) {
                log.info("Since no active channel, task ends");
                return;
            }
            MessageTransmitUtils.broadcastMessageToSocket(CommandCodeEnum.REQ_LUS_STATUS);
            /* MessageTransmitUtils.sendMessageToSocket(CommandCodeEnum.REQ_DIMMING_STATUS);
            MessageTransmitUtils.sendMessageToSocket(CommandCodeEnum.REQ_PARAMETER, new byte[0x02]);*/
            log.info("requestEquipStatus task ends");
        } catch (Exception e) {
            log.error("Error occurs on requestEquipStatus task", e);
        }
    }

    @Scheduled(fixedRateString = "${stee.lus-refresh-status-rate}", initialDelay = 6000)
    public void requestEquipStatusValue() {
        try {
            log.info("requestEquipStatusValue task starts");
            if (GlobalVariable.channelMap.isEmpty()) {
                log.info("Since no active channel, task ends");
                return;
            }
            MessageTransmitUtils.broadcastMessageToSocket(CommandCodeEnum.REQ_LUS_STATUS);
            log.info("requestEquipStatusValue task ends");
        } catch (Exception e) {
            log.error("Error occurs on requestEquipStatusValue task", e);
        }
    }

    @Scheduled(fixedRateString = "${stee.lus-refresh-status-rate}")
    public void changeLusStatus() {
        log.debug("Updating LUS INTF Status ...");
        LocalDateTime now = LocalDateTime.now();
        boolean result = equipStatusService.updateStatus(propertiesConfig.getLusInterfaceCode(),
                LusConstants.LUS_RUNNING_STATUS);
        if (result) {
            log.debug("Updated LUS INTF status: {} to {} at {}",
                    propertiesConfig.getLusInterfaceCode(),
                    LusConstants.LUS_RUNNING_STATUS,
                    DateTimeUtils.format(now, LusConstants.DATE_TIME_FORMATTER));
        } else {
            log.error("Fail to update LUS INTF status: {} at {}",
                    propertiesConfig.getLusInterfaceCode(),
                    DateTimeUtils.format(now, LusConstants.DATE_TIME_FORMATTER));
        }
    }
}
