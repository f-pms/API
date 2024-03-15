package com.hbc.pms.integration.db.repository;

import com.hbc.pms.integration.db.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ReportRepository
    extends CrudRepository<ReportEntity, Long>, JpaSpecificationExecutor<ReportEntity> {}
