package com.stee.emas.ctetun.pmcs.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.stee.emas.ctetun.pmcs.entity.PMCSAlarmCodeMappingEntity;

@Repository
public interface PMCSAlarmCodeMappingRepo extends JpaRepository<PMCSAlarmCodeMappingEntity,String> {
	
	@Query("select pm from PMCSAlarmCodeMappingEntity pm where pm.felsCode = :felsCode")
	public List<PMCSAlarmCodeMappingEntity> findByFelsCode(@Param("felsCode") String felsCode);
	
}
