package com.aslmk.authenticationservice.exception;

public record ValidationError(String field, String message) {}
