package com.nguyenphuong.PackageTransfer.repositories.admin;

import com.nguyenphuong.PackageTransfer.modals.entities.UserEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "users", collectionResourceRel = "Users")
@Primary
public interface AdminUserRepository extends PagingAndSortingRepository<UserEntity, String> {
}
