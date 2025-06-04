package com.aslmk.authenticationservice.repository;

import com.aslmk.authenticationservice.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    Optional<List<AccountEntity>> findAllByUserId(Long userId);
    Optional<AccountEntity> findByIdAndProvider(String id, String providerName);
}
