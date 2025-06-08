package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.dto.LoginRequestDto;
import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.dto.UserResponseDto;
import com.aslmk.authenticationservice.entity.AuthMethod;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.exception.AuthenticationFailedException;
import com.aslmk.authenticationservice.exception.BadRequestException;
import com.aslmk.authenticationservice.exception.UserNotFoundException;
import com.aslmk.authenticationservice.mapper.UserResponseDtoMapper;
import com.aslmk.authenticationservice.service.AuthService;
import com.aslmk.authenticationservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserResponseDtoMapper userResponseDtoMapper;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final EmailConfirmationService emailConfirmationService;

    public AuthServiceImpl(UserResponseDtoMapper userResponseDtoMapper, AuthenticationManager authenticationManager, UserService userService, SecurityContextRepository securityContextRepository, EmailConfirmationService emailConfirmationService) {
        this.userResponseDtoMapper = userResponseDtoMapper;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.securityContextRepository = securityContextRepository;
        this.emailConfirmationService = emailConfirmationService;
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
    public UserResponseDto authenticateUser(LoginRequestDto loginRequestDto,
                                            HttpServletRequest httpRequest,
                                            HttpServletResponse httpResponse) {

        UserEntity userEntity = userService.findUserByEmail(loginRequestDto.getEmail()).orElseThrow(
                () -> new UserNotFoundException("User " + loginRequestDto.getEmail() + " not found")
        );

        if (!userEntity.isVerified()) {
            emailConfirmationService.sendVerificationToken(userEntity.getEmail());
            throw new BadRequestException("Email not verified. Check your inbox for a verification token");
        }

        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            saveSecurityContext(authentication, httpRequest, httpResponse);

            return buildUserResponse(userEntity);
        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Email or password is incorrect");
        }
    }

    public UserResponseDto authenticateOAuthUser(LoginRequestDto login,
                                                 HttpServletRequest httpRequest,
                                                 HttpServletResponse httpResponse) {
        UserEntity userEntity = userService.findUserByEmail(login.getEmail()).orElseThrow(
                () -> new UserNotFoundException("User " + login.getEmail() + " not found")
        );

        User userDetails = new User(userEntity.getEmail(),
                "",
                List.of(new SimpleGrantedAuthority(userEntity.getRole().getRoleName())));

        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken
                .authenticated(userDetails, "", userDetails.getAuthorities());
        saveSecurityContext(authentication, httpRequest, httpResponse);

        return buildUserResponse(userEntity);
    }
    private UserResponseDto buildUserResponse(UserEntity userEntity) {
        UserResponseDto dto = userResponseDtoMapper.mapToUserResponseDto(userEntity);
        dto.setRole(userEntity.getRole().getRoleName());
        dto.setAccounts(userEntity.getAccounts());
        return dto;
    }
    private void saveSecurityContext(Authentication authentication, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);
    }
}
