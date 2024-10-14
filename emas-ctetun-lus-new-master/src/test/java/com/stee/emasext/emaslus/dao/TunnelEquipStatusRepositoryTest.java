package com.stee.emasext.emaslus.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stee.emasext.emaslus.dao.interfaces.primary.TunnelEquipAttrStatusRepository;
import com.stee.emasext.emaslus.entities.primary.TunnelEquipStatus;
import com.stee.emasext.emaslus.entities.primary.ids.TunnelEquipStatusId;
import com.stee.emasext.emaslus.enums.EmasLusAttrCodeEnum;
import com.stee.emasext.emaslus.schedules.UpdateEquipStatusSchedule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

/**
 * @author Wang Yu
 * Created at 2023/4/27
 */
@SpringBootTest(properties = {"stee.jms-enable=false"})
@Slf4j
public class TunnelEquipStatusRepositoryTest {
    @MockBean
    private UpdateEquipStatusSchedule updateEquipStatusSchedule;

    @Autowired
    private TunnelEquipAttrStatusRepository tunnelEquipAttrStatusRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    public void testGetAllByEquipId() {
        List<TunnelEquipStatus> status = tunnelEquipAttrStatusRepository.getAllByEquipId("TESTING_LUS01");
        log.info("{}", objectMapper.writeValueAsString(status));
    }

    @SneakyThrows
    @Test
    public void testGetById() {
        TunnelEquipStatus status = tunnelEquipAttrStatusRepository
                .findById(new TunnelEquipStatusId("TESTING_LUS02", EmasLusAttrCodeEnum.DISPLAY_MESSAGE.getAttrCode()))
                        .orElse(null);

        log.info("{}", objectMapper.writeValueAsString(status));
    }
}
