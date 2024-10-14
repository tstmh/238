package com.stee.emas.ctetun.pmcs.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stee.emas.ctetun.pmcs.entity.PMCSInterfaceStatusEntity;

@Repository
public interface PMCSInterfaceStatusRepo extends JpaRepository<PMCSInterfaceStatusEntity, Integer> {
	
	@Query("select interfaceStatus from PMCSInterfaceStatusEntity as interfaceStatus where equipId =:equipId")
	public PMCSInterfaceStatusEntity getInterfaceStatus(@Param("equipId") String equipId);
	
}
