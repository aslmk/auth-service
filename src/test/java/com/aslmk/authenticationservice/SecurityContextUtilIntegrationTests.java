package com.aslmk.authenticationservice;

import com.aslmk.authenticationservice.dto.LoginRequestDto;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.entity.UserRoleEntity;
import com.aslmk.authenticationservice.exception.AuthenticationFailedException;
import com.aslmk.authenticationservice.repository.UserRepository;
import com.aslmk.authenticationservice.repository.UserRoleRepository;
import com.aslmk.authenticationservice.util.SecurityContextUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SecurityContextUtilIntegrationTests {
    @Autowired
    private SecurityContextUtil securityContextUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    private MockHttpServletRequest httpRequest;
    private MockHttpServletResponse httpResponse;
    private LoginRequestDto login;

    private static final String USER_USERNAME = "test0";
    private static final String USER_PASSWORD = "test123";
    private static final String USER_EMAIL = "test@test-email.com";

    @BeforeEach
    void setUp() {
        createUser(USER_USERNAME, USER_PASSWORD, USER_EMAIL);
        login = createLoginRequest(USER_USERNAME, USER_PASSWORD, USER_EMAIL);
        httpRequest = new MockHttpServletRequest();
        httpResponse = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticate_should_saveAuthenticationInContext_when_validCredentials() {
        securityContextUtil.authenticate(login, httpRequest, httpResponse);

        SecurityContext context = SecurityContextHolder.getContext();
        Assertions.assertNotNull(context);

        Authentication authentication = context.getAuthentication();
        Assertions.assertNotNull(authentication);
        Assertions.assertTrue(authentication.isAuthenticated());
        Assertions.assertEquals(authentication.getName(), USER_EMAIL);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Assertions.assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void authenticate_should_replacePreviousAuthentication_when_newUserLogsIn() {
        UserEntity user2 = createUser("test1", USER_PASSWORD, "test1@test1-email.com");
        LoginRequestDto login2 = createLoginRequest(user2.getUsername(), USER_PASSWORD, user2.getEmail());

        securityContextUtil.authenticate(login, httpRequest, httpResponse);

        SecurityContext context = SecurityContextHolder.getContext();
        Assertions.assertNotNull(context);

        Authentication authentication = context.getAuthentication();
        Assertions.assertNotNull(authentication);
        Assertions.assertTrue(authentication.isAuthenticated());
        Assertions.assertEquals(authentication.getName(), USER_EMAIL);

        securityContextUtil.authenticate(login2, httpRequest, httpResponse);

        context = SecurityContextHolder.getContext();
        Assertions.assertNotNull(context);

        authentication = context.getAuthentication();
        Assertions.assertNotNull(authentication);
        Assertions.assertTrue(authentication.isAuthenticated());
        Assertions.assertEquals(authentication.getName(), user2.getEmail());
    }

    @Test
    void authenticate_should_throwAuthenticationFailedException_when_passwordIsInvalid() {
        login.setPassword("wrongPassword");
        Assertions.assertThrows(AuthenticationFailedException.class,
                () -> securityContextUtil.authenticate(login, httpRequest, httpResponse));
    }

    @Test
    void authenticate_should_throwAuthenticationFailedException_when_emailIsInvalid() {
        login.setEmail("wrongEmail");
        Assertions.assertThrows(AuthenticationFailedException.class,
                () -> securityContextUtil.authenticate(login, httpRequest, httpResponse));
    }

    @Test
    void clear_should_clearAuthenticationInContext() {
        securityContextUtil.authenticate(login, httpRequest, httpResponse);

        SecurityContext context = SecurityContextHolder.getContext();
        Assertions.assertNotNull(context);

        Authentication authentication = context.getAuthentication();
        Assertions.assertNotNull(authentication);

        securityContextUtil.clear();

        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void authenticateOAuth_should_saveAuthenticationInContext_when_validCredentials() {
        UserEntity oauthUser = createUser("oauthUser", "", "oauth-test@test-email.com");

        securityContextUtil.authenticateOAuth(oauthUser, httpRequest, httpResponse);

        SecurityContext context = SecurityContextHolder.getContext();
        Assertions.assertNotNull(context);

        Authentication authentication = context.getAuthentication();
        Assertions.assertNotNull(authentication);
        Assertions.assertTrue(authentication.isAuthenticated());
        Assertions.assertEquals(authentication.getName(), oauthUser.getEmail());

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Assertions.assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void authenticateOAuth_should_replaceAuthentication_when_newOAuthUserLogsIn() {
        UserEntity oauthUser = createUser("oauthUser", "", "oauth-test@test-email.com");
        UserEntity oauthUser2 = createUser("oauthUser1", "", "oauth-test1@test-email.com");

        securityContextUtil.authenticateOAuth(oauthUser, httpRequest, httpResponse);

        SecurityContext context = SecurityContextHolder.getContext();
        Assertions.assertNotNull(context);

        Authentication authentication = context.getAuthentication();
        Assertions.assertNotNull(authentication);
        Assertions.assertTrue(authentication.isAuthenticated());
        Assertions.assertEquals(authentication.getName(), oauthUser.getEmail());


        securityContextUtil.authenticateOAuth(oauthUser2, httpRequest, httpResponse);

        context = SecurityContextHolder.getContext();
        Assertions.assertNotNull(context);

        authentication = context.getAuthentication();
        Assertions.assertNotNull(authentication);
        Assertions.assertTrue(authentication.isAuthenticated());
        Assertions.assertEquals(authentication.getName(), oauthUser2.getEmail());
    }

    private LoginRequestDto createLoginRequest(String username, String password, String email) {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }
    private UserEntity createUser(String username, String password, String email) {
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRoleName("USER");

        UserRoleEntity role = userRoleRepository.findByRoleName("USER")
                .orElseGet(() -> userRoleRepository.save(userRole));

        return userRepository.save(UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .role(role)
                .build());
    }
}
