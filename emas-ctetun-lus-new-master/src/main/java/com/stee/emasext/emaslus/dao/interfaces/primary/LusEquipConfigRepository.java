package com.stee.emasext.emaslus.dao.interfaces.primary;

import com.stee.emasext.emaslus.entities.primary.LusEquipConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LusEquipConfigRepository extends JpaRepository<LusEquipConfig, String> {

    @Query("from LusEquipConfig lus where lus.controllerId = :controllerId and lus.enable = 'Y' " +
            "order by lus.assignPosition asc")
    List<LusEquipConfig> getLusEquipConfigsByControllerId(@Param("controllerId") String controllerId);

    @Query("select count(*) from LusEquipConfig lus where lus.controllerId = :controllerId and lus.enable = 'Y'")
    int countByControllerId(@Param("controllerId") String controllerId);

    @Query("from LusEquipConfig lus where lus.equipType = 'ltc' and lus.controllerId is null and lus.enable = 'Y'")
    List<LusEquipConfig> listAllController();

}