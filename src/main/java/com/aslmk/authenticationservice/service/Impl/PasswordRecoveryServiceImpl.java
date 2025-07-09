package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.entity.TokenEntity;
import com.aslmk.authenticationservice.entity.TokenType;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.exception.TokenExceptionMapper;
import com.aslmk.authenticationservice.exception.TokenExpiredException;
import com.aslmk.authenticationservice.exception.TokenNotFoundException;
import com.aslmk.authenticationservice.exception.UserNotFoundException;
import com.aslmk.authenticationservice.service.PasswordRecoveryService;
import com.aslmk.authenticationservice.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private final UserService userService;
    private final EmailService emailService;
    private final TokenLifecycleService tokenLifecycleService;

    public PasswordRecoveryServiceImpl(UserService userService, EmailService emailService, TokenLifecycleService tokenLifecycleService) {
        this.userService = userService;
        this.emailService = emailService;
        this.tokenLifecycleService = tokenLifecycleService;
    }

    @Override
    public String reset(String email) {
        UserEntity user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        TokenEntity resetToken = tokenLifecycleService
                .createToken(user.getEmail(), TokenType.PASSWORD_RESET, Duration.ofHours(1));

        emailService.sendPasswordResetEmail(resetToken.getEmail(), resetToken.getToken());

        return "Password reset token was successfully sent to your email";
    }

    @Override
    public String newPassword(String newPassword, String token) {
        try {
            TokenEntity tokenEntity = tokenLifecycleService
                    .validateAndReturnTokenByValue(token, TokenType.PASSWORD_RESET);

            UserEntity user = userService.findUserByEmail(tokenEntity.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found for this token"));

            userService.updateUserPassword(user, newPassword);

            tokenLifecycleService.invalidateToken(tokenEntity);
        } catch (TokenNotFoundException e) {
            throw TokenExceptionMapper
                    .mapNotFound(TokenType.PASSWORD_RESET, "Password reset token not found");
        } catch (TokenExpiredException e) {
            throw TokenExceptionMapper
                    .mapExpired(TokenType.PASSWORD_RESET, "Password reset token has expired");
        }

        return "Password was successfully changed";
    }
}
