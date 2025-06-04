package com.aslmk.authenticationservice.provider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authUri;
    private String tokenUri;
    private String userInfoUri;
    private String scopes;
}
