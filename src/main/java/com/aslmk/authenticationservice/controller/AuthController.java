package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.annotation.ValidateRecaptcha;
import com.aslmk.authenticationservice.security.SecurityConstants;
import com.aslmk.authenticationservice.auth.core.AuthProcessor;
import com.aslmk.authenticationservice.auth.payload.AuthPayload;
import com.aslmk.authenticationservice.auth.payload.EmailPasswordAuthPayload;
import com.aslmk.authenticationservice.auth.result.AuthResult;
import com.aslmk.authenticationservice.auth.result.AuthResultType;
import com.aslmk.authenticationservice.auth.result.AuthStatusResponse;
import com.aslmk.authenticationservice.auth.result.AuthenticatedUser;
import com.aslmk.authenticationservice.dto.LoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthProcessor authProcessor;

    public AuthController(AuthProcessor authProcessor) {
        this.authProcessor = authProcessor;
    }

    @ValidateRecaptcha
    @PostMapping("/login")
    public ResponseEntity<AuthStatusResponse> authenticate(@Valid @RequestBody LoginRequestDto loginRequestDto,
                                                           HttpServletRequest request) {
        AuthPayload authPayload = EmailPasswordAuthPayload.builder()
                .email(loginRequestDto.getEmail())
                .password(loginRequestDto.getPassword())
                .code(loginRequestDto.getCode())
                .build();

        AuthResult result = authProcessor.process(authPayload);

        AuthStatusResponse response = new AuthStatusResponse(result.type().name());

        if (result.type().equals(AuthResultType.TWO_FACTOR_REQUIRED)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        if (result.type().equals(AuthResultType.AUTHENTICATED)) {
            saveContext(result.user(), request);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    private void saveContext(AuthenticatedUser authenticatedUser, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SecurityConstants.AUTHENTICATED_USER_SESSION_KEY, authenticatedUser);
    }
}
