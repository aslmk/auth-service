package com.aslmk.authenticationservice.service;

import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.entity.AuthMethod;
import com.aslmk.authenticationservice.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    UserEntity saveUser(RegistrationRequestDto registrationRequestDto,
                        String pictureUrl,
                        AuthMethod authMethod,
                        boolean isVerified);
    Optional<UserEntity> findUserByEmail(String email);
}
