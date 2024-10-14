package com.stee.emasext.emaslus.tasks;

import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.services.primary.LusEquipConfigService;
import com.stee.emasext.emaslus.utils.GlobalVariable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Wang Yu
 * Created at 2023/7/19
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LusControllerInitializeTask implements ApplicationRunner {

    private final LusEquipConfigService lusEquipConfigService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("initializing ControllerId Ip address mapping ...");
        List<LusEquipConfig> lusEquipConfigs = lusEquipConfigService.listAllController();
        for (LusEquipConfig lus : lusEquipConfigs) {
            final InetSocketAddress inetSocketAddress = new InetSocketAddress(lus.getIpAddress(), lus.getPort());
            GlobalVariable.ControllerIdIpMap.put(lus.getEquipId(), inetSocketAddress.toString());
            GlobalVariable.IpAndControllerIdMap.put(inetSocketAddress.toString(), lus.getEquipId());
        }
        log.info("Task: initializing Ip address mapping DONE: size {}", GlobalVariable.ControllerIdIpMap.size());
        log.info("init map {}", GlobalVariable.ControllerIdIpMap);
    }
}
