package com.aslmk.authenticationservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponseDto {
    private long id;
    private String username;
    private String email;
    private String role;
}
