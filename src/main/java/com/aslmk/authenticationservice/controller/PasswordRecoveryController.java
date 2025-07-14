package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.annotation.ValidateRecaptcha;
import com.aslmk.authenticationservice.service.PasswordRecoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    public PasswordRecoveryController(PasswordRecoveryService passwordRecoveryService) {
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @ValidateRecaptcha
    @PostMapping("password-recovery/new")
    public ResponseEntity<?> newPassword(@RequestParam("newPassword") String newPassword,
            @RequestParam("token") String token) {
        String message = passwordRecoveryService.newPassword(newPassword, token);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @ValidateRecaptcha
    @PostMapping("password-recovery/reset")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email) {
        String message = passwordRecoveryService.reset(email);
        return ResponseEntity.ok(Map.of("message", message));
    }
}
