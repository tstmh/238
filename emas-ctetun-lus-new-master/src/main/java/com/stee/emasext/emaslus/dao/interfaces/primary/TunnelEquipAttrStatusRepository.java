package com.stee.emasext.emaslus.dao.interfaces.primary;

import com.stee.emasext.emaslus.entities.primary.TunnelEquipStatus;
import com.stee.emasext.emaslus.entities.primary.ids.TunnelEquipStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TunnelEquipAttrStatusRepository extends JpaRepository<TunnelEquipStatus, TunnelEquipStatusId> {

    List<TunnelEquipStatus> getAllByEquipId(String equipId);

}