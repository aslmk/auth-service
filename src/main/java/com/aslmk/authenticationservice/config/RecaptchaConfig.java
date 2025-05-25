package com.aslmk.authenticationservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google.recaptcha")
@Getter
@Setter
public class RecaptchaConfig {
    private String secretKey;
    private String verifyUrl;
    private boolean enabled;
}
