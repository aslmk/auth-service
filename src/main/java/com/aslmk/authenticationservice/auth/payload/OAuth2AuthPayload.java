package com.aslmk.authenticationservice.auth.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OAuth2AuthPayload implements AuthPayload {
    private String email;
}
