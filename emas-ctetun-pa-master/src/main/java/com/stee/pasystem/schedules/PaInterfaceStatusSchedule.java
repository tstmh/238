package com.stee.pasystem.schedules;

import com.stee.pasystem.services.TunnelEquipStatusService;
import com.stee.pasystem.utils.PaConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PaInterfaceStatusSchedule {
    private final TunnelEquipStatusService tunnelEquipStatusService;

    @Value("${stee.pa-interface-code}")
    private String paTag;

    @Scheduled(fixedRate = 60000)
    public void changePaStatus() {
        log.debug("Updating PA INTF Status ...");
        LocalDateTime now = LocalDateTime.now();
        tunnelEquipStatusService.saveStatus(paTag, PaConstant.OPE, PaConstant.RUNNING);
            log.debug("Updated PA INTF status: {} to {}",
                    paTag, PaConstant.RUNNING);

    }
}
