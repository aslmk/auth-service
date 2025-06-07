package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.entity.TokenEntity;
import com.aslmk.authenticationservice.entity.TokenType;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.exception.*;
import com.aslmk.authenticationservice.repository.TokenRepository;
import com.aslmk.authenticationservice.service.PasswordRecoveryService;
import com.aslmk.authenticationservice.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    public PasswordRecoveryServiceImpl(UserService userService, TokenRepository tokenRepository, EmailService emailService) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Override
    public String reset(String email) {
        UserEntity user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        TokenEntity resetToken = generatePasswordResetToken(user.getEmail());
        emailService.sendPasswordResetEmail(resetToken.getEmail(), resetToken.getToken());

        return "Password reset token was successfully sent to your email";
    }

    @Override
    public String newPassword(String newPassword, String token) {
        TokenEntity passwordResetToken = tokenRepository.findByTokenAndTokenType(token, TokenType.PASSWORD_RESET)
                .orElseThrow(() -> new PasswordResetTokenNotFoundException("Password reset token not found"));

        boolean isTokenExpired = passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now());

        if (isTokenExpired) {
            throw new PasswordResetTokenExpiredException("Password reset token has expired");
        }

        UserEntity user = userService.findUserByEmail(passwordResetToken.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found for this token"));

        userService.updateUserPassword(user, newPassword);

        tokenRepository.delete(passwordResetToken);

        return "Password was successfully changed";
    }

    private TokenEntity generatePasswordResetToken(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        tokenRepository.deleteByEmailAndTokenType(email, TokenType.PASSWORD_RESET);
        TokenEntity newToken = TokenEntity.builder()
                .email(email)
                .tokenType(TokenType.PASSWORD_RESET)
                .expiresAt(expiresAt)
                .token(token)
                .build();

        return tokenRepository.save(newToken);
    }
}
