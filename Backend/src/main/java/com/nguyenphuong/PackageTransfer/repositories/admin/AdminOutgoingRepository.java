package com.nguyenphuong.PackageTransfer.repositories.admin;

import com.nguyenphuong.PackageTransfer.modals.entities.OutgoingPackageEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "outgoings", collectionResourceRel = "Outgoings")
@Primary
public interface AdminOutgoingRepository extends PagingAndSortingRepository<OutgoingPackageEntity, String> {
}
