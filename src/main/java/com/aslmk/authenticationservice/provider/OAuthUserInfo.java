package com.aslmk.authenticationservice.provider;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthUserInfo {
    private String id;
    private String email;
    private String picture;
    private String name;
    private String accessToken;
    private String refreshToken;
    private Integer expiresAt;
    private String provider;
}
