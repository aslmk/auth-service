package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.entity.TokenEntity;
import com.aslmk.authenticationservice.entity.TokenType;
import com.aslmk.authenticationservice.exception.BadRequestException;
import com.aslmk.authenticationservice.exception.TokenExceptionMapper;
import com.aslmk.authenticationservice.exception.TokenExpiredException;
import com.aslmk.authenticationservice.exception.TokenNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TwoFactorAuthService {
    private final EmailService emailService;
    private final TokenLifecycleService tokenLifecycleService;


    public TwoFactorAuthService(EmailService emailService, TokenLifecycleService tokenLifecycleService) {
        this.emailService = emailService;
        this.tokenLifecycleService = tokenLifecycleService;
    }

    public void validateTwoFactorToken(String email, String code) {
        try {
            TokenEntity tokenEntity = tokenLifecycleService
                    .validateAndReturnTokenByEmail(email, TokenType.TWO_FACTOR);

            if (!tokenEntity.getToken().equals(code)) {
                throw new BadRequestException("Two factor token is not valid");
            }

            tokenLifecycleService.invalidateToken(tokenEntity);
        } catch (TokenNotFoundException e) {
            throw TokenExceptionMapper
                    .mapNotFound(TokenType.TWO_FACTOR, "Two factor token not found");
        } catch (TokenExpiredException e) {
            throw TokenExceptionMapper
                    .mapExpired(TokenType.TWO_FACTOR, "Two factor token expired");
        }
    }

    public String sendTwoFactorToken(String email) {
        TokenEntity twoFactorToken = tokenLifecycleService
                .createToken(email, TokenType.TWO_FACTOR, Duration.ofMinutes(15));
        emailService.sendTwoFactorAuthenticationTokenEmail(twoFactorToken.getEmail(), twoFactorToken.getToken());
        return "Two factor token was sent to your email";
    }
}
