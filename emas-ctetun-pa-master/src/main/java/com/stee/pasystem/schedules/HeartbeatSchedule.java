package com.stee.pasystem.schedules;

import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.pasystem.dao.interfaces.TunnelEquipConfigDao;
import com.stee.pasystem.entities.TunnelEquipConfig;
import com.stee.pasystem.enums.AlarmCodeEnum;
import com.stee.pasystem.exceptions.ApiProcessException;
import com.stee.pasystem.services.HeartbeatService;
import com.stee.pasystem.services.TunnelEquipStatusService;
import com.stee.pasystem.utils.CommonUtils;
import com.stee.pasystem.vos.AlarmData;
import com.stee.pasystem.vos.HeartbeatRespVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HeartbeatSchedule {

    private final HeartbeatService heartbeatService;
    private final TunnelEquipStatusService tunnelEquipStatusService;
    private final TunnelEquipConfigDao tunnelEquipConfigDao;
    private final JmsTemplate queueTemplate;
    @Value("${stee.pa-cmh}")
    private String queueName;

    @Retryable(include = { ApiProcessException.class, Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    @Scheduled(fixedRateString = "${stee.heartbeat-frequency}")
    public void heartbeat() {
        log.info("Heartbeat schedule starts");
        try {
            HeartbeatRespVO heartbeatRespVO = heartbeatService.process();
            health(Constants.ALARM_CLEARED);
        } catch (ApiProcessException e) {
            log.error("Error occurs on heartbeat schedule: ", e);
            throw e;
        }
        log.info("Heartbeat schedule ends");
    }

    @Recover
    public void recover(Exception e) {
        log.error("error occurs in Heartbeat after 3 retries", e);
        // send disconnection alarm to CMH
        health(Constants.ALARM_RAISED);
    }

    private void health(int status) {
        List<TechnicalAlarmDto> alarmList = new ArrayList<>();
        TechAlarmDtoList techAlarmDtoList = new TechAlarmDtoList(alarmList);
        String equipId = "paw_01";
        Optional<TunnelEquipConfig> equipConfigOptional = tunnelEquipConfigDao.findById(equipId);
        if (!equipConfigOptional.isPresent()) {
            log.warn("Cannot find equip {} in DB", equipId);
            return;
        }
        TunnelEquipConfig tunnelEquip = equipConfigOptional.get();
        AlarmData alarmData = new AlarmData();
        alarmData.setAlarmStatus(status);
        alarmData.setDeviceId(equipId);
        alarmData.setAlarmCode(AlarmCodeEnum.SYSTEM_SHUTDOWN_DETECTED.getCode());
        TechnicalAlarmDto alarm = tunnelEquipStatusService.saveStatusAndCreateTechnicalAlarm(tunnelEquip, alarmData);
        if (alarm != null) {
            alarmList.add(alarm);
        }
        if (!alarmList.isEmpty()) {
            CommonUtils.sendJms(queueTemplate, queueName,
                    techAlarmDtoList, MessageConstants.TECH_ALARM_ID);
        }
    }
}
