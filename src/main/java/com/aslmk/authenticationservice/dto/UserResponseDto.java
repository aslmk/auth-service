package com.aslmk.authenticationservice.dto;

import com.aslmk.authenticationservice.entity.AccountEntity;
import com.aslmk.authenticationservice.entity.AuthMethod;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private long id;
    private String username;
    private String email;
    private String role;
    private String pictureUrl;
    private boolean verified;
    private boolean twoFactorEnabled;
    private AuthMethod authMethod;
    private List<AccountEntity> accounts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
