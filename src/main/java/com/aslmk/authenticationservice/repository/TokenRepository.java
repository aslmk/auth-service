package com.aslmk.authenticationservice.repository;

import com.aslmk.authenticationservice.entity.TokenEntity;
import com.aslmk.authenticationservice.entity.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    void deleteByEmailAndTokenType(String email, TokenType tokenType);
    Optional<TokenEntity> findByTokenAndTokenType(String token, TokenType tokenType);
}
