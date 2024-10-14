package com.stee.emas.ctetun.pmcs.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.stee.emas.ctetun.pmcs.entity.PMCSEquipConfigEntity;

@Repository
public interface PMCSEquipConfigRepo extends JpaRepository<PMCSEquipConfigEntity, String> {
	
	@Query("select pmc from PMCSEquipConfigEntity pmc where pmc.felsCode = :felsCode and pmc.plcHost = :hostName" )
	public List<PMCSEquipConfigEntity> getAllEquipment(@Param("felsCode") String felsCode, @Param("hostName") String hostName);
	
	@Query("select pmc from PMCSEquipConfigEntity pmc where pmc.equipType = :equipType and pmc.equipId = :equipId and pmc.plcHost =:hostName")
	public PMCSEquipConfigEntity getAttributeAddress(@Param("equipType") String equipType, @Param("equipId") String equipId, @Param("hostName") String hostName);
	
	@Query(value = "select distinct pmc.maxReadRegister from PMCSEquipConfigEntity pmc where pmc.equipType =:equipType")
	public int getMaxReadRegisterByEquipType(@Param("equipType") String equipType);
	

}
