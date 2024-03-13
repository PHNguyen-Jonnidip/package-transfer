package com.nguyenphuong.PackageTransfer.repositories;

import com.nguyenphuong.PackageTransfer.modals.entities.HttpResponseLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HttpResponseLogRepository extends JpaRepository<HttpResponseLogEntity, String>, JpaSpecificationExecutor<HttpResponseLogEntity> {
}
