package com.aslmk.authenticationservice.provider;

import com.aslmk.authenticationservice.dto.OAuthUserDto;

public interface OAuthService {
    String buildAuthorizationUrl(String providerName);
    OAuthUserDto processOAuthCallback(String providerName, String code);
}
