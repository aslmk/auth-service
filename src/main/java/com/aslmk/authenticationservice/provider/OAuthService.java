package com.aslmk.authenticationservice.provider;

import com.aslmk.authenticationservice.dto.LoginRequestDto;

public interface OAuthService {
    String buildAuthorizationUrl(String providerName);
    LoginRequestDto processOAuthCallback(String providerName, String code);
}
