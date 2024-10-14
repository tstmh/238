package com.stee.pasystem.dao.interfaces;

import com.stee.pasystem.entities.TunnelEquipConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TunnelEquipConfigDao extends JpaRepository<TunnelEquipConfig, String> {
}
