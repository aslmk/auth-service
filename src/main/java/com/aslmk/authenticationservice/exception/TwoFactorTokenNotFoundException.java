package com.aslmk.authenticationservice.exception;

public class TwoFactorTokenNotFoundException extends RuntimeException {
    public TwoFactorTokenNotFoundException(String message) {
        super(message);
    }
}
