package com.aslmk.authenticationservice.exception;

public class TwoFactorTokenExpiredException extends RuntimeException {
    public TwoFactorTokenExpiredException(String message) {
        super(message);
    }
}
