package com.stee.emas.ctetun.wmss.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.stee.emas.ctetun.wmss.entity.WmssEquipConfigEntity;

@Repository
public interface WmssEquipConfigRepo extends JpaRepository<WmssEquipConfigEntity, String> {
	
	@Query("select wms from WmssEquipConfigEntity wms where wms.felsCode = :felsCode" )
	public List<WmssEquipConfigEntity> getAllWmssEquipment(@Param("felsCode") String felsCode);
	

}
