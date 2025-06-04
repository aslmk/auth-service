package com.aslmk.authenticationservice.provider;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "oauth")
@Getter
@Setter
public class BaseProviderOptions {
    private Map<String, ProviderProperties> providers = new HashMap<>();

    public Map<String, ProviderProperties> getProviders() {
        return Collections.unmodifiableMap(providers);
    }
}
