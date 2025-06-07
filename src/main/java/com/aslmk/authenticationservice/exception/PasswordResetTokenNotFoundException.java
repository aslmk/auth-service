package com.aslmk.authenticationservice.exception;

public class PasswordResetTokenNotFoundException extends RuntimeException {
    public PasswordResetTokenNotFoundException(String message) {
        super(message);
    }
}
