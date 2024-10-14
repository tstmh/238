package com.stee.emas.ctetun.wmss.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.stee.emas.ctetun.wmss.entity.WmssAlarmCodeMappingEntity;

@Repository
public interface WmssAlarmCodeMappingRepo extends JpaRepository<WmssAlarmCodeMappingEntity,String> {
}
