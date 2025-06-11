package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.entity.TokenEntity;
import com.aslmk.authenticationservice.entity.TokenType;
import com.aslmk.authenticationservice.exception.BadRequestException;
import com.aslmk.authenticationservice.exception.TwoFactorTokenExpiredException;
import com.aslmk.authenticationservice.exception.TwoFactorTokenNotFoundException;
import com.aslmk.authenticationservice.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class TwoFactorAuthService {
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    public TwoFactorAuthService(TokenRepository tokenRepository, EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    public void validateTwoFactorToken(String email, String code) {
        TokenEntity twoFactorToken = tokenRepository.findByEmailAndTokenType(email, TokenType.TWO_FACTOR)
                .orElseThrow(() -> new TwoFactorTokenNotFoundException("Two factor token not found"));

        boolean isTokenExpired = twoFactorToken.getExpiresAt().isBefore(LocalDateTime.now());

        if (isTokenExpired) {
            throw new TwoFactorTokenExpiredException("Two factor token has expired");
        }

        if (!twoFactorToken.getToken().equals(code)) {
            throw new BadRequestException("Two factor token is not valid");
        }

        tokenRepository.delete(twoFactorToken);

    }

    public String sendTwoFactorToken(String email) {
        TokenEntity twoFactorToken = generateTwoFactorToken(email);
        emailService.sendTwoFactorAuthenticationTokenEmail(twoFactorToken.getEmail(), twoFactorToken.getToken());
        return "Two factor token was sent to your email";
    }

    private TokenEntity generateTwoFactorToken(String email) {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        String token = String.valueOf(code);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        tokenRepository.deleteByEmailAndTokenType(email, TokenType.TWO_FACTOR);

        TokenEntity newToken = TokenEntity.builder()
                .email(email)
                .tokenType(TokenType.TWO_FACTOR)
                .expiresAt(expiresAt)
                .token(token)
                .build();

        return tokenRepository.save(newToken);
    }
}
