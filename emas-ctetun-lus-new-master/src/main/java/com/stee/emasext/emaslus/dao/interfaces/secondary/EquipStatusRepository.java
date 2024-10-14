package com.stee.emasext.emaslus.dao.interfaces.secondary;

import com.stee.emasext.emaslus.entities.secondary.EquipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipStatusRepository extends JpaRepository<EquipStatus, Long> {

    EquipStatus findEquipStatusByEquipId(String equipId);

}