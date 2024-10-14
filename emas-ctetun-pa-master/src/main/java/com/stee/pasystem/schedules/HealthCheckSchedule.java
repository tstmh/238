package com.stee.pasystem.schedules;

import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.pasystem.dao.interfaces.TunnelEquipConfigDao;
import com.stee.pasystem.dao.interfaces.TunnelEquipStatusDao;
import com.stee.pasystem.entities.TunnelEquipConfig;
import com.stee.pasystem.entities.TunnelEquipStatus;
import com.stee.pasystem.enums.AlarmCodeEnum;
import com.stee.pasystem.enums.AlarmStatusEnum;
import com.stee.pasystem.exceptions.ApiProcessException;
import com.stee.pasystem.services.HealthStatusService;
import com.stee.pasystem.services.TunnelEquipStatusService;
import com.stee.pasystem.utils.CommonUtils;
import com.stee.pasystem.vos.AlarmData;
import com.stee.pasystem.vos.HealthStatusRespVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HealthCheckSchedule {

    private final HealthStatusService healthStatusService;
    private final JmsTemplate queueTemplate;

    private final TunnelEquipConfigDao tunnelEquipConfigDao;
    private final TunnelEquipStatusService tunnelEquipStatusService;

    @Value("${stee.pa-cmh}")
    private String queueName;

    @Retryable(include = {ApiProcessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    @Scheduled(fixedRateString = "${stee.health-check-frequency}")
    public void healthCheckSchedule() {
        log.info("HealthCheck schedule starts");
        HealthStatusRespVO healthStatusRespVO = healthStatusService.process();
        List<AlarmData> alarmDataList = healthStatusRespVO.getResponseData();
        List<TechnicalAlarmDto> alarmList = new ArrayList<>();
        TechAlarmDtoList techAlarmDtoList = new TechAlarmDtoList(alarmList);
        if (alarmDataList != null) {
            for (AlarmData alarmData : alarmDataList) {
                // get alarm data from PA, then send it to CMH
                String equipId = alarmData.getDeviceId();
                Optional<TunnelEquipConfig> equipConfigOptional = tunnelEquipConfigDao.findById(equipId);
                if (!equipConfigOptional.isPresent()) {
                    log.warn("Cannot find equip {} in DB", equipId);
                    continue;
                }
                TunnelEquipConfig tunnelEquip = equipConfigOptional.get();
                TechnicalAlarmDto alarm = tunnelEquipStatusService.saveStatusAndCreateTechnicalAlarm(tunnelEquip, alarmData);
                if (alarm != null) {
                    alarmList.add(alarm);
                }
            }
            if (!alarmDataList.isEmpty()) {
                CommonUtils.sendJms(queueTemplate, queueName, techAlarmDtoList, MessageConstants.TECH_ALARM_ID);
            }
        }

        log.info("HealthCheck schedule ends");
    }

    @Recover
    public void recover(ApiProcessException e) {
        log.error("error occurs in Health Check after 3 retries", e);

    }
}
 