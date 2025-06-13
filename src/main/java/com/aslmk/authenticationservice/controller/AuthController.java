package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.annotation.ValidateRecaptcha;
import com.aslmk.authenticationservice.dto.LoginRequestDto;
import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ValidateRecaptcha
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequestDto registrationRequestDto,
                                      HttpServletRequest httpRequest,
                                      HttpServletResponse httpResponse,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        String message = authService.registerUser(registrationRequestDto, httpRequest, httpResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", message));
    }

    @ValidateRecaptcha
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequestDto loginRequestDto,
                                                        HttpServletRequest httpRequest,
                                                        HttpServletResponse httpResponse,
                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        String message = authService.authenticateUser(loginRequestDto, httpRequest, httpResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", message));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
        authService.logout(httpRequest);
        return ResponseEntity.noContent().build();
    }
}
