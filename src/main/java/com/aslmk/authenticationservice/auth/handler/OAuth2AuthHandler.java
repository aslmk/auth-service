package com.aslmk.authenticationservice.auth.handler;

import com.aslmk.authenticationservice.auth.result.AuthenticatedUser;
import com.aslmk.authenticationservice.auth.core.AuthHandler;
import com.aslmk.authenticationservice.auth.result.AuthResult;
import com.aslmk.authenticationservice.auth.result.AuthResultType;
import com.aslmk.authenticationservice.auth.payload.AuthPayload;
import com.aslmk.authenticationservice.auth.payload.OAuth2AuthPayload;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.exception.UserNotFoundException;
import com.aslmk.authenticationservice.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OAuth2AuthHandler implements AuthHandler<OAuth2AuthPayload> {

    private final UserService userService;

    public OAuth2AuthHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean support(AuthPayload authPayload) {
        return authPayload instanceof OAuth2AuthPayload;
    }

    @Override
    public AuthResult authenticate(OAuth2AuthPayload payload) {
        UserEntity user = userService.findUserByEmail(payload.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User not found: email='%s'", payload.getEmail()))
                );

        AuthenticatedUser authUser = new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                Set.of(user.getRole().getRoleName()),
                user.isVerified(),
                user.isTwoFactorEnabled());

        return new AuthResult(AuthResultType.AUTHENTICATED, authUser);
    }
}
