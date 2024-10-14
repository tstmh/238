package com.stee.emasext.emaslus.dao;

import com.stee.emasext.emaslus.dao.interfaces.primary.LusEquipConfigRepository;
import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import com.stee.emasext.emaslus.schedules.UpdateEquipStatusSchedule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

/**
 * @author Wang Yu
 * Created at 2023/4/26
 */
@SpringBootTest(properties = {"stee.jms-enable=false"})
@Slf4j
public class LusEquipConfigRepositoryTest {

    @Autowired
    private LusEquipConfigRepository lusEquipConfigRepository;

    @MockBean
    private UpdateEquipStatusSchedule updateEquipStatusSchedule;

    @Test
    public void testListAllController() {
        List<LusEquipConfig> lusEquipConfigs = lusEquipConfigRepository.listAllController();
        log.info("lus equipments size: {}", lusEquipConfigs.size());
    }
}
