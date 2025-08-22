package com.aslmk.authenticationservice.service;

import com.aslmk.authenticationservice.dto.LoginRequestDto;
import com.aslmk.authenticationservice.dto.OAuthUserDto;
import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.dto.UserResponseDto;
import com.aslmk.authenticationservice.entity.AuthMethod;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.entity.UserRoleEntity;
import com.aslmk.authenticationservice.exception.BadRequestException;
import com.aslmk.authenticationservice.exception.UserNotFoundException;
import com.aslmk.authenticationservice.mapper.UserResponseDtoMapper;
import com.aslmk.authenticationservice.service.Impl.AuthServiceImpl;
import com.aslmk.authenticationservice.service.Impl.EmailConfirmationService;
import com.aslmk.authenticationservice.service.Impl.TwoFactorAuthService;
import com.aslmk.authenticationservice.util.SecurityContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserService userService;
    @Mock
    private EmailConfirmationService emailConfirmationService;
    @Mock
    private TwoFactorAuthService twoFactorAuthService;
    @Mock
    private SecurityContextUtil securityUtil;
    @Mock
    private UserResponseDtoMapper mapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private static final String USER_USERNAME = "test0";
    private static final String USER_PASSWORD = "test123";
    private static final String USER_EMAIL = "test@aslmk.com";


    @Test
    void registerUser_should_saveUserAndSendVerificationToken() {
        RegistrationRequestDto dto = new RegistrationRequestDto();
        dto.setUsername(USER_USERNAME);
        dto.setPassword(USER_PASSWORD);
        dto.setEmail(USER_EMAIL);

        UserEntity userEntity = createUserEntity(USER_USERNAME, USER_PASSWORD, USER_EMAIL);
        userEntity.setVerified(true);

        Mockito.when(userService.saveUser(dto,
                "",
                AuthMethod.CREDENTIALS,
                false)).thenReturn(userEntity);

        authService.registerUser(dto, request, response);

        Mockito.verify(userService, Mockito.times(1))
                .saveUser(dto, "", AuthMethod.CREDENTIALS, false);

        Mockito.verify(emailConfirmationService, Mockito.times(1))
                .sendVerificationToken(dto.getEmail());
    }

    @Test
    void authenticateUser_should_saveUserInSecurityContext() {
        LoginRequestDto dto = createLoginRequestDto(USER_USERNAME, USER_PASSWORD, USER_EMAIL);

        UserEntity userEntity = createUserEntity(USER_USERNAME, USER_PASSWORD, USER_EMAIL);
        userEntity.setVerified(true);

        Mockito.when(userService.findUserByEmail(dto.getEmail()))
                .thenReturn(Optional.of(userEntity));

        authService.authenticateUser(dto, request, response);

        Mockito.verify(securityUtil, Mockito.times(1))
                .authenticate(dto, request, response);
    }

    @Test
    void authenticateUser_should_throwUserNotFoundException_when_userWithGivenEmailDoesNotExist() {
        LoginRequestDto dto = createLoginRequestDto(USER_USERNAME, USER_PASSWORD, "doesNotExist@404.com");

        Assertions.assertThrows(UserNotFoundException.class,
                () -> authService.authenticateUser(dto, request, response));
    }

    @Test
    void authenticateUser_should_sendVerificationTokenAndThrowBadRequestException_when_userEmailIsNotVerified() {
        LoginRequestDto dto = createLoginRequestDto(USER_USERNAME, USER_PASSWORD, USER_EMAIL);

        UserEntity userEntity = createUserEntity(USER_USERNAME, USER_PASSWORD, USER_EMAIL);
        userEntity.setVerified(false);

        Mockito.when(userService.findUserByEmail(dto.getEmail())).thenReturn(Optional.of(userEntity));

        Assertions.assertThrows(BadRequestException.class,
                () -> authService.authenticateUser(dto, request, response));

        Mockito.verify(emailConfirmationService, Mockito.times(1))
                .sendVerificationToken(dto.getEmail());
    }

    @Test
    void authenticateUser_should_callValidateTwoFactorTokenMethod_when_twoFactorAuthIsEnabledAndCodeIsNotNull() {
        LoginRequestDto dto = createLoginRequestDto(USER_USERNAME, USER_PASSWORD, USER_EMAIL);
        dto.setCode("220825");

        UserEntity userEntity = createUserEntity(USER_USERNAME, USER_PASSWORD, USER_EMAIL);
        userEntity.setVerified(true);
        userEntity.setTwoFactorEnabled(true);

        Mockito.when(userService.findUserByEmail(dto.getEmail())).thenReturn(Optional.of(userEntity));

        authService.authenticateUser(dto, request, response);

        Mockito.verify(twoFactorAuthService, Mockito.times(1))
                .validateTwoFactorToken(dto.getEmail(), dto.getCode());
    }

    @Test
    void authenticateUser_should_sendTwoFactorToken_when_twoFactorAuthIsEnabledAndCodeIsNull() {
        LoginRequestDto dto = createLoginRequestDto(USER_USERNAME, USER_PASSWORD, USER_EMAIL);

        UserEntity userEntity = createUserEntity(USER_USERNAME, USER_PASSWORD, USER_EMAIL);
        userEntity.setVerified(true);
        userEntity.setTwoFactorEnabled(true);

        Mockito.when(userService.findUserByEmail(dto.getEmail())).thenReturn(Optional.of(userEntity));

        authService.authenticateUser(dto, request, response);

        Mockito.verify(twoFactorAuthService, Mockito.times(1))
                .sendTwoFactorToken(dto.getEmail());
    }

    @Test
    void authenticateOAuthUser_should_throwUserNotFoundException_when_userWithGivenEmailDoesNotExist() {
        OAuthUserDto dto = OAuthUserDto.builder().email(USER_EMAIL).build();

        Assertions.assertThrows(UserNotFoundException.class,
                () -> authService.authenticateOAuthUser(dto, request, response));
    }

    @Test
    void authenticateOAuthUser_should_saveUserInSecurityContextAndReturnUserResponseDto_when_oauthUserAuthenticationSucceeded() {
        OAuthUserDto dto = OAuthUserDto.builder().email(USER_EMAIL).build();

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRoleName("ROLE_USER");

        UserEntity userEntity = createUserEntity(USER_USERNAME, USER_PASSWORD, USER_EMAIL);
        userEntity.setVerified(true);
        userEntity.setRole(userRole);

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .username(USER_USERNAME)
                .email(USER_EMAIL)
                .verified(true)
                .twoFactorEnabled(false)
                .authMethod(AuthMethod.GOOGLE)
                .pictureUrl("")
                .role("ROLE_USER")
                .accounts(Collections.emptyList())
                .build();

        Mockito.when(mapper.mapToUserResponseDto(userEntity)).thenReturn(userResponseDto);
        Mockito.when(userService.findUserByEmail(dto.getEmail())).thenReturn(Optional.of(userEntity));

        UserResponseDto result = authService.authenticateOAuthUser(dto, request, response);

        Mockito.verify(securityUtil, Mockito.times(1))
                .authenticateOAuth(userEntity, request, response);

        Mockito.verify(mapper, Mockito.times(1))
                .mapToUserResponseDto(userEntity);

        Assertions.assertEquals(userResponseDto, result);
    }

    @Test
    void logout_should_invalidateSessionAndClearSecurityContext_when_sessionIsNotNull() {
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession(false)).thenReturn(session);

        authService.logout(request);

        Mockito.verify(session, Mockito.times(1)).invalidate();
        Mockito.verify(securityUtil, Mockito.times(1))
                .clear();
    }

    @Test
    void logout_should_clearSecurityContext_when_sessionIsNull() {
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession(false)).thenReturn(null);

        authService.logout(request);

        Mockito.verify(session, Mockito.never()).invalidate();
        Mockito.verify(securityUtil, Mockito.times(1))
                .clear();
    }

    private LoginRequestDto createLoginRequestDto(String username, String password, String email) {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setEmail(email);
        return dto;
    }
    private UserEntity createUserEntity(String username, String password, String email) {
        return UserEntity.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
    }
}