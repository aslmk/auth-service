package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.service.TokenGenerationStrategy;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class NumericTokenGenerator implements TokenGenerationStrategy {
    @Override
    public String generateToken() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
