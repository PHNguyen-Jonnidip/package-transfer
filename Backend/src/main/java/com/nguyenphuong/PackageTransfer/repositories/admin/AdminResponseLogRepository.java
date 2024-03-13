package com.nguyenphuong.PackageTransfer.repositories.admin;

import com.nguyenphuong.PackageTransfer.modals.entities.HttpResponseLogEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "response-logs", collectionResourceRel = "Response Logs")
@Primary
public interface AdminResponseLogRepository extends PagingAndSortingRepository<HttpResponseLogEntity, String> {
}
