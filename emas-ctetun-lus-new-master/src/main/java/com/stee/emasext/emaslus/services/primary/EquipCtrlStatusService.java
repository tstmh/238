package com.stee.emasext.emaslus.services.primary;

import com.stee.emasext.emaslus.dao.interfaces.primary.EquipCtrlStatusRepository;
import com.stee.emasext.emaslus.entities.primary.EquipCtrlStatus;
import com.stee.emasext.emaslus.utils.LusConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author Wang Yu
 * Created at 2023/6/9
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EquipCtrlStatusService {
    private final EquipCtrlStatusRepository equipCtrlStatusRepository;

    @Transactional
    public void save(EquipCtrlStatus equipCtrlStatus) {
        equipCtrlStatus.setCreatedBy(LusConstants.SYSTEM);
        equipCtrlStatus.setCreatedDate(LocalDateTime.now());
        equipCtrlStatus.setUpdatedBy(LusConstants.SYSTEM);
        equipCtrlStatus.setUpdatedDate(LocalDateTime.now());
        equipCtrlStatusRepository.save(equipCtrlStatus);
    }
}
