package com.aslmk.authenticationservice.service;

import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    UserEntity saveUser(RegistrationRequestDto registrationRequestDto);
    Optional<UserEntity> findUserByEmail(String email);
}
