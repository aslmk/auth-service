package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.dto.LoginRequestDto;
import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.dto.UserResponseDto;
import com.aslmk.authenticationservice.entity.AuthMethod;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.exception.AuthenticationFailedException;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserResponseDtoMapper userResponseDtoMapper;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    public AuthServiceImpl(UserResponseDtoMapper userResponseDtoMapper, AuthenticationManager authenticationManager, UserService userService, SecurityContextRepository securityContextRepository) {
        this.userResponseDtoMapper = userResponseDtoMapper;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public UserResponseDto registerUser(RegistrationRequestDto registrationRequestDto,
                                        HttpServletRequest httpRequest,
                                        HttpServletResponse httpResponse) {

        UserEntity userEntity = userService.saveUser(registrationRequestDto,
                "",
                AuthMethod.CREDENTIALS,
                false);

        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken
                .unauthenticated(registrationRequestDto.getEmail(), registrationRequestDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        saveSecurityContext(authentication, httpRequest, httpResponse);

        UserResponseDto userResponseDto = userResponseDtoMapper.mapToUserResponseDto(userEntity);
        userResponseDto.setRole(userEntity.getRole().getRoleName());
        userResponseDto.setAccounts(userEntity.getAccounts());
        return userResponseDto;
    }

    @Override
    public UserResponseDto authenticateUser(LoginRequestDto loginRequestDto,
                                            HttpServletRequest httpRequest,
                                            HttpServletResponse httpResponse) {

        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            saveSecurityContext(authentication, httpRequest, httpResponse);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserEntity userEntity = userService.findUserByEmail(userDetails.getUsername()).orElseThrow(
                    () -> new UserNotFoundException("User " + userDetails.getUsername() + " not found")
            );
            UserResponseDto userResponseDto = userResponseDtoMapper.mapToUserResponseDto(userEntity);
            userResponseDto.setRole(userEntity.getRole().getRoleName());
            userResponseDto.setAccounts(userEntity.getAccounts());
            return userResponseDto;
        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Email or password is incorrect");
        }
    }

    private void saveSecurityContext(Authentication authentication, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);
    }
}
