package com.stee.emas.ctetun.pmcs.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.stee.emas.ctetun.pmcs.entity.PMCSAttributeStatusEntity;

@Repository
public interface PMCSAttributeStatusRepo extends JpaRepository<PMCSAttributeStatusEntity, String> {
	
	@Query("select status from PMCSAttributeStatusEntity as status where felsCode = :felsCode")
	public List<PMCSAttributeStatusEntity> findByFelsCode(@Param("felsCode") String felsCode);
	
	@Query("select status from PMCSAttributeStatusEntity as status where equipId =:equipId and attributeCode =:attributeCode")
	public  PMCSAttributeStatusEntity getAttributeValue(@Param("equipId") String equipId, @Param("attributeCode") String attributeCode);

}

