package com.aslmk.authenticationservice.service;

import com.aslmk.authenticationservice.auth.result.AuthResult;
import com.aslmk.authenticationservice.dto.RegistrationRequestDto;

public interface RegistrationService {
    AuthResult registerUser(RegistrationRequestDto request);
}
