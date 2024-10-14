package com.stee.pasystem.services;

import com.stee.emas.common.constants.Constants;
import com.stee.emas.common.dto.TechnicalAlarmDto;
import com.stee.pasystem.dao.interfaces.TunnelEquipConfigDao;
import com.stee.pasystem.dao.interfaces.TunnelEquipStatusDao;
import com.stee.pasystem.entities.TunnelEquipConfig;
import com.stee.pasystem.entities.TunnelEquipStatus;
import com.stee.pasystem.entities.id.TunnelEquipStatusId;
import com.stee.pasystem.enums.AlarmCodeEnum;
import com.stee.pasystem.utils.CommonUtils;
import com.stee.pasystem.utils.PaConstant;
import com.stee.pasystem.vos.AlarmData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TunnelEquipStatusService {

    private final TunnelEquipConfigDao tunnelEquipConfigDao;
    private final TunnelEquipStatusDao tunnelEquipStatusDao;

    @Transactional
    public TechnicalAlarmDto saveStatusAndCreateTechnicalAlarm(TunnelEquipConfig tunnelEquip,
                                                               AlarmData alarmData) {
        AlarmCodeEnum alarmCodeEnum = null;
        try {
            alarmCodeEnum = AlarmCodeEnum.ofCode(alarmData.getAlarmCode());
        } catch (IllegalArgumentException e) {
            return null;
        }
        TunnelEquipStatusId statusId = new TunnelEquipStatusId(tunnelEquip.getEquipId(), alarmCodeEnum.getAttrCode());
        Optional<TunnelEquipStatus> equipAttrOptional = tunnelEquipStatusDao.findById(statusId);

        if (equipAttrOptional.isPresent()) {
            TunnelEquipStatus oldStatus = equipAttrOptional.get();
            if (Objects.equals(oldStatus.getAttrValue(), alarmData.getAlarmStatus())) {
                log.info("The status no changes, ignore it {}: {}", tunnelEquip.getEquipId(), alarmCodeEnum.getAttrCode());
                return null;
            } else { // else: status changed
                // update status and then change the current status
                oldStatus.setUpdatedDate(LocalDateTime.now());
                oldStatus.setUpdatedBy(PaConstant.SYSTEM);
                oldStatus.setAttrValue(alarmData.getAlarmStatus());
                tunnelEquipStatusDao.save(oldStatus);
                log.info("Equip {}, status [{}] changed to {}", tunnelEquip.getEquipId(),
                        alarmCodeEnum.getAttrCode(), alarmData.getAlarmStatus());
                TechnicalAlarmDto alarm = new TechnicalAlarmDto();
                if (alarmData.getAlarmStatus() == Constants.ALARM_RAISED) {
                    // raise an alarm
                    alarm = CommonUtils.createAlarmDtoRaise(tunnelEquip.getEquipId(), tunnelEquip.getEquipType(), alarmCodeEnum);
                } else {
                    // clear it if it required
                    alarm = CommonUtils.createAlarmDtoClear(tunnelEquip.getEquipId(), tunnelEquip.getEquipType(), alarmCodeEnum);
                }
                return alarm;
            }
        } else { // new Status need to be added
            log.info("The new Status, {}: {}, saving it to DB", tunnelEquip.getEquipId(), alarmCodeEnum.getAttrCode());
            TunnelEquipStatus equipStatus = new TunnelEquipStatus();
            equipStatus.setEquipId(tunnelEquip.getEquipId());
            equipStatus.setCreatedDate(LocalDateTime.now());
            equipStatus.setCreatedBy(PaConstant.SYSTEM);
            equipStatus.setUpdatedDate(LocalDateTime.now());
            equipStatus.setUpdatedBy(PaConstant.SYSTEM);
            equipStatus.setAttrCode(alarmCodeEnum.getAttrCode());
            equipStatus.setAttrValue(alarmData.getAlarmStatus());
            tunnelEquipStatusDao.save(equipStatus);
            return null;
        }
    }

    @Transactional
    public void saveStatus(String equipId, String attrCode, int attrValue) {
        TunnelEquipStatus equipStatus = new TunnelEquipStatus();
        equipStatus.setEquipId(equipId);
        equipStatus.setCreatedDate(LocalDateTime.now());
        equipStatus.setCreatedBy(PaConstant.SYSTEM);
        equipStatus.setUpdatedDate(LocalDateTime.now());
        equipStatus.setUpdatedBy(PaConstant.SYSTEM);
        equipStatus.setAttrCode(attrCode);
        equipStatus.setAttrValue(attrValue);
        tunnelEquipStatusDao.save(equipStatus);
    }

}
