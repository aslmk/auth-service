package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.dto.OAuthUserDto;
import com.aslmk.authenticationservice.exception.BadRequestException;
import com.aslmk.authenticationservice.provider.OAuthService;
import com.aslmk.authenticationservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class OAuthController {

    private final OAuthService providerService;
    private final AuthService authService;

    public OAuthController(OAuthService providerService, AuthService authService) {
        this.providerService = providerService;
        this.authService = authService;
    }

    @GetMapping("/oauth/connect/{provider}")
    public ResponseEntity<Map<String, String>> connect(@PathVariable String provider) {
        String authUrl = providerService.buildAuthorizationUrl(provider);
        return ResponseEntity.ok(Map.of("url", authUrl));
    }

    @GetMapping("/oauth/callback/{provider}")
    public ResponseEntity<?> callback(@PathVariable String provider,
                                      @RequestParam("code") String code,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        if (code == null || code.isEmpty()) {
            throw new BadRequestException("Invalid code");
        }
        OAuthUserDto oAuthUser = providerService.processOAuthCallback(provider, code);

        return ResponseEntity.ok(authService.authenticateOAuthUser(oAuthUser, request, response));
    }
}
