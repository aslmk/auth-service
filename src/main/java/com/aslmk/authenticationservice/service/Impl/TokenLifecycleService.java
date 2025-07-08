package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.entity.TokenEntity;
import com.aslmk.authenticationservice.entity.TokenType;
import com.aslmk.authenticationservice.exception.*;
import com.aslmk.authenticationservice.repository.TokenRepository;
import com.aslmk.authenticationservice.service.TokenGenerationStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TokenLifecycleService {

    private final Map<TokenType, TokenGenerationStrategy> strategyMap;
    private final TokenRepository tokenRepository;

    public TokenLifecycleService(@Qualifier(value = "uuidTokenGenerator") TokenGenerationStrategy uuidGen,
                                 @Qualifier(value = "numericTokenGenerator") TokenGenerationStrategy numericGen,
                                 TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        this.strategyMap = Map.of(
                TokenType.TWO_FACTOR, numericGen,
                TokenType.VERIFICATION, uuidGen,
                TokenType.PASSWORD_RESET, uuidGen
        );

    }

    public TokenEntity createToken(String email, TokenType tokenType, Duration validFor) {
        TokenGenerationStrategy strategy = strategyMap.get(tokenType);

        if (strategy == null) {
            throw new RuntimeException("No generator found for token type " + tokenType);
        }

        String token = strategy.generateToken();

        LocalDateTime expiresAt = LocalDateTime.now().plus(validFor);

        tokenRepository.deleteByEmailAndTokenType(email, tokenType);

        TokenEntity newToken = TokenEntity.builder()
                .email(email)
                .tokenType(tokenType)
                .expiresAt(expiresAt)
                .token(token)
                .build();

        return tokenRepository.save(newToken);

    }

    public TokenEntity validateAndReturnTokenByValue(String token, TokenType tokenType) {
        TokenEntity tokenEntity = tokenRepository.findByTokenAndTokenType(token, tokenType)
                .orElseThrow(() -> new TokenNotFoundException(tokenType));

        isTokenExpired(tokenEntity);

        return tokenEntity;
    }

    public TokenEntity validateAndReturnTokenByEmail(String email, TokenType tokenType) {
        TokenEntity tokenEntity = tokenRepository.findByEmailAndTokenType(email, tokenType)
                .orElseThrow(() -> new TokenNotFoundException(tokenType));

        isTokenExpired(tokenEntity);
        return tokenEntity;
    }

    private void isTokenExpired(TokenEntity token) {
        boolean isTokenExpired = token.getExpiresAt().isBefore(LocalDateTime.now());

        if (isTokenExpired) {
            throw new TokenExpiredException(token.getTokenType());
        }
    }

    public void invalidateToken(TokenEntity token) {
        tokenRepository.delete(token);
    }
}
