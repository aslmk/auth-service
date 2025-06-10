package com.aslmk.authenticationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserInfoResponse {
    private String sub;
    private String email;
    private String name;
    private String picture;
}
