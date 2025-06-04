package com.aslmk.authenticationservice.provider;

public interface OAuthService {
    String buildAuthorizationUrl(String providerName);
    void processOAuthCallback(String providerName, String code);
}
