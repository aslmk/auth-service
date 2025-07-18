package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.dto.LoginRequestDto;
import com.aslmk.authenticationservice.dto.OAuthUserDto;
import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.dto.UserResponseDto;
import com.aslmk.authenticationservice.entity.AuthMethod;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.exception.BadRequestException;
import com.aslmk.authenticationservice.exception.UserNotFoundException;
import com.aslmk.authenticationservice.mapper.UserResponseDtoMapper;
import com.aslmk.authenticationservice.service.AuthService;
import com.aslmk.authenticationservice.service.UserService;
import com.aslmk.authenticationservice.util.SecurityContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserResponseDtoMapper userResponseDtoMapper;
    private final UserService userService;
    private final SecurityContextUtil securityContextUtil;
    private final EmailConfirmationService emailConfirmationService;
    private final TwoFactorAuthService twoFactorAuthService;

    public AuthServiceImpl(UserResponseDtoMapper userResponseDtoMapper,
                           UserService userService,
                           SecurityContextUtil securityContextUtil,
                           EmailConfirmationService emailConfirmationService,
                           TwoFactorAuthService twoFactorAuthService) {
        this.userResponseDtoMapper = userResponseDtoMapper;
        this.userService = userService;
        this.securityContextUtil = securityContextUtil;
        this.emailConfirmationService = emailConfirmationService;
        this.twoFactorAuthService = twoFactorAuthService;
    }

    @Override
    public String registerUser(RegistrationRequestDto registrationRequestDto,
                                        HttpServletRequest httpRequest,
                                        HttpServletResponse httpResponse) {

        UserEntity userEntity = userService.saveUser(registrationRequestDto,
                "",
                AuthMethod.CREDENTIALS,
                false);

        emailConfirmationService.sendVerificationToken(userEntity.getEmail());

        return "Registration successful. Check your inbox to verify your email";
    }

    @Override
    public String authenticateUser(LoginRequestDto loginRequestDto,
                                            HttpServletRequest httpRequest,
                                            HttpServletResponse httpResponse) {

        UserEntity userEntity = userService.findUserByEmail(loginRequestDto.getEmail()).orElseThrow(
                () -> new UserNotFoundException("User " + loginRequestDto.getEmail() + " not found")
        );

        if (!userEntity.isVerified()) {
            emailConfirmationService.sendVerificationToken(userEntity.getEmail());
            throw new BadRequestException("Email not verified. Check your inbox for a verification token");
        }

        if (userEntity.isTwoFactorEnabled()) {
            if (loginRequestDto.getCode() != null) {
                twoFactorAuthService.validateTwoFactorToken(userEntity.getEmail(), loginRequestDto.getCode());
            } else {
                return twoFactorAuthService.sendTwoFactorToken(userEntity.getEmail());
            }
        }

        securityContextUtil.authenticate(loginRequestDto, httpRequest, httpResponse);
        return "Login successful";
    }

    public UserResponseDto authenticateOAuthUser(OAuthUserDto login,
                                                 HttpServletRequest httpRequest,
                                                 HttpServletResponse httpResponse) {
        UserEntity userEntity = userService.findUserByEmail(login.getEmail()).orElseThrow(
                () -> new UserNotFoundException("User " + login.getEmail() + " not found")
        );

        securityContextUtil.authenticateOAuth(userEntity, httpRequest, httpResponse);

        return buildUserResponse(userEntity);
    }

    @Override
    public void logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        securityContextUtil.clear();
    }

    private UserResponseDto buildUserResponse(UserEntity userEntity) {
        UserResponseDto dto = userResponseDtoMapper.mapToUserResponseDto(userEntity);
        dto.setRole(userEntity.getRole().getRoleName());
        dto.setAccounts(userEntity.getAccounts());
        return dto;
    }
}
