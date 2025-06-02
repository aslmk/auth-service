package com.aslmk.authenticationservice.service;

import com.aslmk.authenticationservice.dto.LoginRequestDto;
import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.dto.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    String registerUser(RegistrationRequestDto registrationRequestDto, HttpServletRequest request, HttpServletResponse response);
    UserResponseDto authenticateUser(LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse httpServletResponse);
}
