package com.nguyenphuong.PackageTransfer.repositories;

import com.nguyenphuong.PackageTransfer.modals.entities.WebhookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WebhookRepository extends JpaRepository<WebhookEntity, String>, JpaSpecificationExecutor<WebhookEntity> {
}
