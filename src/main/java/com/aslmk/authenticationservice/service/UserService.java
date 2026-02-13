package com.aslmk.authenticationservice.service;

import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.entity.AuthMethod;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.provider.OAuthUserInfo;

import java.util.Optional;

public interface UserService {
    UserEntity saveUser(RegistrationRequestDto registrationRequestDto,
                        String pictureUrl,
                        AuthMethod authMethod,
                        boolean isVerified);
    Optional<UserEntity> findUserByEmail(String email);
    void updateUserVerificationStatus(UserEntity user, boolean verified);
    UserEntity createIfNotExistsUserFromOAuth(OAuthUserInfo userInfo);
    void updateUserPassword(UserEntity user, String password);
}
