package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.entity.TokenEntity;
import com.aslmk.authenticationservice.entity.TokenType;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.exception.UserNotFoundException;
import com.aslmk.authenticationservice.exception.VerificationTokenExpiredException;
import com.aslmk.authenticationservice.exception.VerificationTokenNotFoundException;
import com.aslmk.authenticationservice.repository.TokenRepository;
import com.aslmk.authenticationservice.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailConfirmationService {
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserService userService;

    public EmailConfirmationService(TokenRepository tokenRepository, EmailService emailService, UserService userService) {
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    public String confirmEmail(String token) {
        TokenEntity verificationToken = tokenRepository.findByTokenAndTokenType(token, TokenType.VERIFICATION)
                .orElseThrow(() -> new VerificationTokenNotFoundException("Verification token not found"));

        boolean isTokenExpired = verificationToken.getExpiresAt().isBefore(LocalDateTime.now());

        if (isTokenExpired) {
            throw new VerificationTokenExpiredException("Verification token has expired");
        }

        UserEntity user = userService.findUserByEmail(verificationToken.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found for this token"));

        if (user.isVerified()) {
            return "Email already confirmed";
        }
        userService.updateUserVerificationStatus(user, true);

        tokenRepository.delete(verificationToken);

        return "Email confirmed successfully. You can now log in";
    }

    public void sendVerificationToken(String email) {
        TokenEntity verificationToken = generateVerificationToken(email);
        emailService.sendConfirmationEmail(email, verificationToken.getToken());
    }

    private TokenEntity generateVerificationToken(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        tokenRepository.deleteByEmailAndTokenType(email, TokenType.VERIFICATION);
        TokenEntity newToken = TokenEntity.builder()
                .email(email)
                .tokenType(TokenType.VERIFICATION)
                .expiresAt(expiresAt)
                .token(token)
                .build();

        return tokenRepository.save(newToken);
    }

}
