package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.entity.TokenEntity;
import com.aslmk.authenticationservice.entity.TokenType;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.exception.TokenExceptionMapper;
import com.aslmk.authenticationservice.exception.TokenExpiredException;
import com.aslmk.authenticationservice.exception.TokenNotFoundException;
import com.aslmk.authenticationservice.exception.UserNotFoundException;
import com.aslmk.authenticationservice.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class EmailConfirmationService {
    private final EmailService emailService;
    private final UserService userService;
    private final TokenLifecycleService tokenLifecycleService;

    public EmailConfirmationService(EmailService emailService, UserService userService, TokenLifecycleService tokenLifecycleService) {
        this.emailService = emailService;
        this.userService = userService;
        this.tokenLifecycleService = tokenLifecycleService;
    }

    public String confirmEmail(String token) {
        try {
            TokenEntity tokenEntity = tokenLifecycleService
                    .validateAndReturnTokenByValue(token, TokenType.VERIFICATION);

            UserEntity user = userService.findUserByEmail(tokenEntity.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found for this token"));

            if (user.isVerified()) {
                return "Email already confirmed";
            }
            userService.updateUserVerificationStatus(user, true);

            tokenLifecycleService.invalidateToken(tokenEntity);
        } catch (TokenNotFoundException e) {
            throw TokenExceptionMapper
                    .mapNotFound(TokenType.VERIFICATION, "Verification token not found");
        } catch (TokenExpiredException e) {
            throw TokenExceptionMapper
                    .mapExpired(TokenType.VERIFICATION, "Verification token has expired");
        }

        return "Email confirmed successfully. You can now log in";
    }

    public void sendVerificationToken(String email) {
        TokenEntity verificationToken = tokenLifecycleService
                .createToken(email, TokenType.VERIFICATION, Duration.ofHours(1));

        emailService.sendConfirmationEmail(email, verificationToken.getToken());
    }
}
