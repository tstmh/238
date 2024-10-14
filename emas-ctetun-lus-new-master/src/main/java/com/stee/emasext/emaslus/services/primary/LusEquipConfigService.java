package com.stee.emasext.emaslus.services.primary;

import com.stee.emasext.emaslus.dao.interfaces.primary.LusEquipConfigRepository;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Wang Yu
 * Created at 2023/5/4
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LusEquipConfigService {

    private final LusEquipConfigRepository lusEquipConfigRepository;

    public List<LusEquipConfig> listEquipUnderController(String controllerId) {
        return lusEquipConfigRepository.getLusEquipConfigsByControllerId(controllerId);
    }

    public int countEquipsByControllerId(String controllerId) {
        return lusEquipConfigRepository.countByControllerId(controllerId);
    }

    public List<LusEquipConfig> listAllController() {
        return lusEquipConfigRepository.listAllController();
    }

    public Optional<LusEquipConfig> getEquipById(String equipId) {
        return lusEquipConfigRepository.findById(equipId);
    }

}
