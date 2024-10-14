package com.stee.emas.ctetun.wmss.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.stee.emas.ctetun.wmss.entity.WmssAttributeStatusEntity;

@Repository
public interface WmssAttributeStatusRepo extends JpaRepository<WmssAttributeStatusEntity, String> {
	
	@Query("select status from WmssAttributeStatusEntity as status where equipId =:equipId and attributeCode =:attributeCode")
	public  WmssAttributeStatusEntity getAttributeValue(@Param("equipId") String equipId, @Param("attributeCode") String attributeCode);

}

