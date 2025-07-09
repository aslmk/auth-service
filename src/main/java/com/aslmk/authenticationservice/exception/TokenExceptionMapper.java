package com.aslmk.authenticationservice.exception;

import com.aslmk.authenticationservice.entity.TokenType;

public class TokenExceptionMapper {
    public static RuntimeException mapNotFound(TokenType tokenType, String message) {
        switch (tokenType) {
            case VERIFICATION -> {
                return new VerificationTokenNotFoundException(message);
            }
            case TWO_FACTOR -> {
                return new TwoFactorTokenNotFoundException(message);
            }
            case PASSWORD_RESET -> {
                return new PasswordResetTokenNotFoundException(message);
            }
            default -> {
                return new RuntimeException("Unhandled token type: " + tokenType);
            }
        }
    }
    public static RuntimeException mapExpired(TokenType tokenType, String message) {
        switch (tokenType) {
            case VERIFICATION -> {
                return new VerificationTokenExpiredException(message);
            }
            case TWO_FACTOR -> {
                return new TwoFactorTokenExpiredException(message);
            }
            case PASSWORD_RESET -> {
                return new PasswordResetTokenExpiredException(message);
            }
            default -> {
                return new RuntimeException("Unhandled token type: " + tokenType);
            }
        }
    }
}
