package com.aslmk.authenticationservice.repository;

import com.aslmk.authenticationservice.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    Optional<UserRoleEntity> findByRoleName(String role);
}
