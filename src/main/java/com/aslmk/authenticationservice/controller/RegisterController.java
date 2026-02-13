package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.annotation.ValidateRecaptcha;
import com.aslmk.authenticationservice.auth.result.AuthStatusResponse;
import com.aslmk.authenticationservice.auth.result.AuthResult;
import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class RegisterController {

    private final RegistrationService registrationService;

    public RegisterController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @ValidateRecaptcha
    @PostMapping("/register")
    public ResponseEntity<AuthStatusResponse> register(@Valid @RequestBody RegistrationRequestDto requestDto) {
        AuthResult result = registrationService.registerUser(requestDto);
        AuthStatusResponse response = new AuthStatusResponse(result.type().name());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
