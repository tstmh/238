package com.stee.emas.ctetun.pmcs.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stee.emas.ctetun.pmcs.entity.PMCSHostConfigEntity;

@Repository
public interface PMCSHostConfigRepo extends JpaRepository<PMCSHostConfigEntity,String> {
	
	@Query("select pm from PMCSHostConfigEntity pm where pm.felsCode = :felsCode")
	public List<PMCSHostConfigEntity> findByFelsCode(@Param("felsCode") String felsCode);
	
	@Query("select pm from PMCSHostConfigEntity pm where pm.plcHost = :host")
	public  PMCSHostConfigEntity findByHostId(@Param("host") String host);

}
