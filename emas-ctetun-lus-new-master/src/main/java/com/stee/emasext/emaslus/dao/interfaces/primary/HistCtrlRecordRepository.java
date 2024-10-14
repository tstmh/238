package com.stee.emasext.emaslus.dao.interfaces.primary;

import com.stee.emasext.emaslus.entities.primary.HistCtrlRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HistCtrlRecordRepository extends JpaRepository<HistCtrlRecord, String>, JpaSpecificationExecutor<HistCtrlRecord> {

}