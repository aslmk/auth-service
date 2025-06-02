package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.service.Impl.EmailConfirmationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class EmailConfirmationController {

    private final EmailConfirmationService emailConfirmationService;

    public EmailConfirmationController(EmailConfirmationService emailConfirmationService) {
        this.emailConfirmationService = emailConfirmationService;
    }

    @PostMapping("/email-confirmation")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token) {
        return ResponseEntity.ok().body(
                Map.of("message", emailConfirmationService.confirmEmail(token)));
    }
}
