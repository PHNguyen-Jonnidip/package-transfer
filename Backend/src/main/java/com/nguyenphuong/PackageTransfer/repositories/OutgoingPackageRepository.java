package com.nguyenphuong.PackageTransfer.repositories;

import com.nguyenphuong.PackageTransfer.modals.entities.OutgoingPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OutgoingPackageRepository extends JpaRepository<OutgoingPackageEntity, String>, JpaSpecificationExecutor<OutgoingPackageEntity> {
}
