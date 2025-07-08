package com.aslmk.authenticationservice.exception;

import com.aslmk.authenticationservice.entity.TokenType;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(TokenType tokenType) {
        super(String.valueOf(tokenType));
    }
}
