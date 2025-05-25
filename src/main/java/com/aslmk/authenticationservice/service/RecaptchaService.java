package com.aslmk.authenticationservice.service;

import com.aslmk.authenticationservice.dto.RecaptchaResponse;

public interface RecaptchaService {
    RecaptchaResponse validateToken(String token);
}
