package com.aslmk.authenticationservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleTokenResponse {
    @JsonAlias("access_token")
    private String accessToken;
    @JsonAlias("refresh_token")
    private String refreshToken;
    @JsonAlias("expires_in")
    private Integer expiresIn;
}
