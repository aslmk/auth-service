package com.aslmk.authenticationservice.provider;

public interface OAuthUserInfoProvider {
    String getProviderName();
    OAuthUserInfo getUserInfoByCode(String code, ProviderProperties properties);
}
