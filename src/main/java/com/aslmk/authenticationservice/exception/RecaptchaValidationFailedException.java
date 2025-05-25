package com.aslmk.authenticationservice.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class RecaptchaValidationFailedException extends RuntimeException {
    private final List<String> errorCodes;
    public RecaptchaValidationFailedException(List<String> errorCodes) {
        super("Recaptcha validation failed");
        this.errorCodes = errorCodes;
    }
}
