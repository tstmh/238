package com.stee.emas.ctetun.pmcs.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stee.emas.ctetun.pmcs.entity.PMCSEquipAttributeEntity;

@Repository
public interface PMCSEquipAttributeRepo extends JpaRepository<PMCSEquipAttributeEntity,Integer>{
	
	@Query("select pmc from PMCSEquipAttributeEntity pmc where pmc.equipType =:equipType and pmc.registerPosition =:registerPosition")
	public List<PMCSEquipAttributeEntity> getAllEquipmentAttribute(@Param("equipType") String equipType, @Param("registerPosition") Integer registerPosition);
	
	@Query(value = "select distinct equip_type from equip_attr_config where fels_code =?1", nativeQuery = true)
	public List<String> getEquipTypeList(String felsCode);
	
}
