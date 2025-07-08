package com.aslmk.authenticationservice.exception;

import com.aslmk.authenticationservice.entity.TokenType;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(TokenType tokenType) {
        super(String.valueOf(tokenType));
    }
}
