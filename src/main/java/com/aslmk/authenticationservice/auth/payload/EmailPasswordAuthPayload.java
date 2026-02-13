package com.aslmk.authenticationservice.auth.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailPasswordAuthPayload implements AuthPayload {
    private String email;
    private String password;
    private String code;
}
