package com.stee.emasext.emaslus.services.primary;

import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.EquipStatusDto;
import com.stee.emas.common.dto.EquipStatusDtoList;
import com.stee.emas.common.dto.TechAlarmDtoList;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.emasext.emaslus.config.PropertiesConfig;
import com.stee.emasext.emaslus.dao.interfaces.primary.LusEquipConfigRepository;
import com.stee.emasext.emaslus.dao.interfaces.primary.TunnelEquipAttrStatusRepository;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.entities.primary.TunnelEquipStatus;
import com.stee.emasext.emaslus.entities.primary.ids.TunnelEquipStatusId;
import com.stee.emasext.emaslus.enums.AlarmCodeEnum;
import com.stee.emasext.emaslus.utils.DateTimeUtils;
import com.stee.emasext.emaslus.utils.LusCommonUtils;
import com.stee.emasext.emaslus.utils.LusConstants;
import com.stee.emasext.emaslus.utils.MessageTransmitUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Wang Yu
 * Created at 2023/4/26
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TunnelEquipStatusService {

    private final TunnelEquipAttrStatusRepository tunnelEquipAttrStatusRepository;
    private final LusEquipConfigRepository lusEquipConfigRepository;

    private final Optional<JmsTemplate> queueTemplate;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final PropertiesConfig propertiesConfig;

    private static final Set<String> TECHNICAL_ALARM_SET;

    static {
        TECHNICAL_ALARM_SET = Arrays.stream(AlarmCodeEnum.values())
                .map(AlarmCodeEnum::getAttrCode).collect(Collectors.toSet());
    }

    @Transactional
    public void saveStatus(TunnelEquipStatus equipStatus) {
        TunnelEquipStatusId statusId = new TunnelEquipStatusId(equipStatus.getEquipId(), equipStatus.getAttrCode());
        Optional<TunnelEquipStatus> equipAttrOptional = tunnelEquipAttrStatusRepository.findById(statusId);
        Optional<LusEquipConfig> lusOptional = lusEquipConfigRepository.findById(equipStatus.getEquipId());
        if (!lusOptional.isPresent()) {
            log.warn("Cannot found equip {}", equipStatus.getEquipId());
            return;
        }
        if (equipAttrOptional.isPresent()) {
            TunnelEquipStatus oldStatus = equipAttrOptional.get();
            if (Objects.equals(oldStatus.getAttrValue(), equipStatus.getAttrValue())) {
                log.info("The status no changes, ignore it {}: {}", equipStatus.getEquipId(), equipStatus.getAttrCode());
            } else {
                // update status and then change the current status
                oldStatus.setUpdatedDate(LocalDateTime.now());
                oldStatus.setUpdatedBy(LusConstants.SYSTEM);
                oldStatus.setAttrValue(equipStatus.getAttrValue());
                tunnelEquipAttrStatusRepository.save(oldStatus);

                log.info("Equip {}, status [{}] changed to {}", equipStatus.getEquipId(),
                        equipStatus.getAttrCode(), equipStatus.getAttrValue());
                // send Jms
                queueTemplate.ifPresent(template ->
                        threadPoolTaskExecutor.submit(() -> {
                            if (TECHNICAL_ALARM_SET.contains(oldStatus.getAttrCode())) {
                                // technical alarm
                                List<TechnicalAlarmDto> dtoList = new ArrayList<>();
                                TechAlarmDtoList listDto = new TechAlarmDtoList(dtoList);
                                LusCommonUtils.addToAlarmListConditionally(oldStatus.getAttrValue(),
                                        dtoList, lusOptional.get(),
                                        AlarmCodeEnum.getAlarmCodeEnum(oldStatus.getAttrCode()));
                                if (!dtoList.isEmpty()) {
                                    MessageTransmitUtils.sendJms(template, propertiesConfig.getJmsLusCmhQueue(),
                                            listDto, MessageConstants.TECH_ALARM_ID);
                                }
                            } else {
                                // equip status
                                List<EquipStatusDto> dtoList = new ArrayList<>();
                                EquipStatusDtoList equipStatusDtoList = new EquipStatusDtoList(dtoList);
                                EquipStatusDto dto = createJmsDtoFromStatus(oldStatus, lusOptional.get());
                                dtoList.add(dto);
                                MessageTransmitUtils.sendJms(template, propertiesConfig.getJmsLusCmhQueue(),
                                        equipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
                            }
                        }));


            } // else: status changed
        } else {
            log.info("The new Status, {}: {}, saving it to DB", equipStatus.getEquipId(), equipStatus.getAttrCode());
            equipStatus.setCreatedDate(LocalDateTime.now());
            equipStatus.setCreatedBy(LusConstants.SYSTEM);
            equipStatus.setUpdatedDate(LocalDateTime.now());
            equipStatus.setUpdatedBy(LusConstants.SYSTEM);
            tunnelEquipAttrStatusRepository.save(equipStatus);
            queueTemplate.ifPresent(template -> {
                List<EquipStatusDto> dtoList = new ArrayList<>();
                EquipStatusDtoList equipStatusDtoList = new EquipStatusDtoList(dtoList);
                EquipStatusDto dto = createJmsDtoFromStatus(equipStatus, lusOptional.get());
                dtoList.add(dto);
                MessageTransmitUtils.sendJms(template, propertiesConfig.getJmsLusCmhQueue(),
                        equipStatusDtoList, MessageConstants.EQUIP_STATUS_ID);
            });

        }
    }

    private EquipStatusDto createJmsDtoFromStatus(TunnelEquipStatus tunnelEquipStatus, LusEquipConfig lusEquipConfig) {
        EquipStatusDto dto = new EquipStatusDto();
        dto.setEquipId(tunnelEquipStatus.getEquipId());
        dto.setStatusCode(tunnelEquipStatus.getAttrCode());
        dto.setStatus(tunnelEquipStatus.getAttrValue());
        dto.setEquipType(lusEquipConfig.getEquipType());
        dto.setDateTime(DateTimeUtils.convertLocalDateTimeToDate(tunnelEquipStatus.getUpdatedDate()));
        dto.setSystemId(lusEquipConfig.getSystemId());
        return dto;
    }


}
