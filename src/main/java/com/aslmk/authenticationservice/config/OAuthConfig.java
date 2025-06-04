package com.aslmk.authenticationservice.config;

import com.aslmk.authenticationservice.provider.BaseProviderOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BaseProviderOptions.class)
public class OAuthConfig {
}
