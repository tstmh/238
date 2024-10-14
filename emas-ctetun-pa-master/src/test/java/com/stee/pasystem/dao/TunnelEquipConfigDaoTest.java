package com.stee.pasystem.dao;

import com.stee.pasystem.dao.interfaces.TunnelEquipConfigDao;
import com.stee.pasystem.entities.TunnelEquipConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class TunnelEquipConfigDaoTest {

    @Autowired
    private TunnelEquipConfigDao tunnelEquipConfigDao;

    @Test
    public void testSelect() {
        Optional<TunnelEquipConfig> equip = tunnelEquipConfigDao.findById("PAS001");
        System.out.println(equip.isPresent() ? equip.get() : "null");
    }
}
