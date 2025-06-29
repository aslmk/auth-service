package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.dto.UserProfileDto;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> profile(Authentication authentication) {
        String username = authentication.getName();
        UserEntity user = userService.findUserByEmail(username).orElseThrow();

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
