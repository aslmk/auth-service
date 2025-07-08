package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.service.TokenGenerationStrategy;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidTokenGenerator implements TokenGenerationStrategy {
    @Override
    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
