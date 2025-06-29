package com.aslmk.authenticationservice.dto;

import com.aslmk.authenticationservice.entity.AccountEntity;
import com.aslmk.authenticationservice.entity.AuthMethod;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private String username;
    private String email;
    private String role;
    private String pictureUrl;
    private boolean verified;
    private boolean twoFactorEnabled;
    private AuthMethod authMethod;
    private LocalDateTime createdAt;
    private List<AccountEntity> accounts;
}
