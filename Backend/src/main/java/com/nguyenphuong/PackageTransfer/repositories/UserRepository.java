package com.nguyenphuong.PackageTransfer.repositories;

import com.nguyenphuong.PackageTransfer.modals.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {
  UserEntity findUserEntityByEmailIgnoreCase(String email);

  UserEntity findUserEntityByUsernameIgnoreCase(String username);
}
