package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.security.UserContext;
import com.aslmk.authenticationservice.dto.UserProfileDto;
import com.aslmk.authenticationservice.auth.result.AuthenticatedUser;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.exception.UserNotFoundException;
import com.aslmk.authenticationservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserContext userContext;

    public UserController(UserService userService, UserContext userContext) {
        this.userService = userService;
        this.userContext = userContext;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> profile() {
        AuthenticatedUser authUser = userContext.getUser();

        UserEntity user = userService.findUserByEmail(authUser.email())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User not found for '%s'", authUser.email()))
                );

        UserProfileDto userProfile = mapToUserProfileDto(user);

        return ResponseEntity.ok(userProfile);
    }

    private UserProfileDto mapToUserProfileDto(UserEntity user) {
        return UserProfileDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().getRoleName())
                .authMethod(user.getAuthMethod())
                .pictureUrl(user.getPictureUrl())
                .createdAt(user.getCreatedAt())
                .twoFactorEnabled(user.isTwoFactorEnabled())
                .verified(user.isVerified())
                .accounts(user.getAccounts())
                .build();
    }
}
