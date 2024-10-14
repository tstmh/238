package com.stee.emasext.emaslus.services.secondary;

import com.stee.emasext.emaslus.dao.interfaces.secondary.EquipStatusRepository;
import com.stee.emasext.emaslus.entities.secondary.EquipStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Wang Yu
 * Created at 2023/4/25
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class EquipStatusService {

    private final EquipStatusRepository equipStatusRepository;

    public boolean updateStatus(String equipId, int status) {
        EquipStatus equipStatus = equipStatusRepository
                .findEquipStatusByEquipId(equipId);
        if (equipStatus == null) {
            log.warn("no equip found: {}", equipId);
            return false;
        }
        equipStatus.setStatus(status);
        equipStatus.setDateTime(LocalDateTime.now());
        equipStatusRepository.save(equipStatus);
        return true;
    }

}
