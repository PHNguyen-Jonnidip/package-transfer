package com.nguyenphuong.PackageTransfer.repositories.admin;

import com.nguyenphuong.PackageTransfer.modals.entities.IncomingPackageEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "incoming", collectionResourceRel = "Incoming")
@Primary
public interface AdminIncomingRepository extends PagingAndSortingRepository<IncomingPackageEntity, String> {
}
