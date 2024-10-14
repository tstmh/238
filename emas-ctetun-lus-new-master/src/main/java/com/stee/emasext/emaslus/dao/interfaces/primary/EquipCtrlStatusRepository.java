package com.stee.emasext.emaslus.dao.interfaces.primary;

import com.stee.emasext.emaslus.entities.primary.EquipCtrlStatus;
import com.stee.emasext.emaslus.entities.primary.ids.EquipCtrlStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipCtrlStatusRepository extends JpaRepository<EquipCtrlStatus, EquipCtrlStatusId> {

}