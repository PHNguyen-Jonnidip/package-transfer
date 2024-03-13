package com.nguyenphuong.PackageTransfer.repositories;

import com.nguyenphuong.PackageTransfer.modals.entities.IncomingPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IncomingPackageRepository extends JpaRepository<IncomingPackageEntity, String>, JpaSpecificationExecutor<IncomingPackageEntity> {
}
