package com.stee.pasystem.dao.interfaces;

import com.stee.pasystem.entities.TunnelEquipStatus;
import com.stee.pasystem.entities.id.TunnelEquipStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TunnelEquipStatusDao extends JpaRepository<TunnelEquipStatus, TunnelEquipStatusId> {
}
