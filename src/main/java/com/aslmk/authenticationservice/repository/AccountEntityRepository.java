package com.aslmk.authenticationservice.repository;

import com.aslmk.authenticationservice.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountEntityRepository extends JpaRepository<AccountEntity, Long> {
    Optional<List<AccountEntity>> findAllByUserId(Long userId);
}
