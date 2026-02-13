package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.annotation.ValidateRecaptcha;
import com.aslmk.authenticationservice.account.action.AccountActionResult;
import com.aslmk.authenticationservice.account.action.AccountActionStatusResponse;
import com.aslmk.authenticationservice.service.PasswordRecoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class PasswordRecoveryController {

    private final PasswordRecoveryService service;

    public PasswordRecoveryController(PasswordRecoveryService service) {
        this.service = service;
    }

    @ValidateRecaptcha
    @PostMapping("/password-recovery/new")
    public ResponseEntity<AccountActionStatusResponse> newPassword(
            @RequestParam("newPassword") String newPassword,
            @RequestParam("token") String token) {
        AccountActionResult result = service.newPassword(newPassword, token);
        AccountActionStatusResponse response = new AccountActionStatusResponse(result.type().name());
        return ResponseEntity.ok(response);
    }

    @ValidateRecaptcha
    @PostMapping("/password-recovery/reset")
    public ResponseEntity<AccountActionStatusResponse> resetPassword(@RequestParam("email") String email) {
        AccountActionResult result =service.reset(email);
        AccountActionStatusResponse response = new AccountActionStatusResponse(result.type().name());
        return ResponseEntity.ok(response);
    }
}
