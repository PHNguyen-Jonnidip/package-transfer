package com.nguyenphuong.PackageTransfer.repositories.admin;

import com.nguyenphuong.PackageTransfer.modals.entities.WebhookEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "webhooks", collectionResourceRel = "Webhooks")
@Primary
public interface AdminWebhookRepository extends PagingAndSortingRepository<WebhookEntity, String> {
}
