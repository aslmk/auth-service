package com.aslmk.authenticationservice.exception;

public class VerificationTokenNotFoundException extends RuntimeException {
    public VerificationTokenNotFoundException(String message) {
        super(message);
    }
}
