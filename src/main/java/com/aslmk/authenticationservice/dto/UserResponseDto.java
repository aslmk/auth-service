package com.aslmk.authenticationservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private long id;
    private String username;
    private String email;
    private String role;
}
