package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.exception.BadRequestException;
import com.aslmk.authenticationservice.provider.OAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class OAuthController {

    private final OAuthService providerService;

    public OAuthController(OAuthService providerService) {
        this.providerService = providerService;
    }

    @GetMapping("/oauth/connect/{provider}")
    public ResponseEntity<Map<String, String>> connect(@PathVariable String provider) {
        String authUrl = providerService.buildAuthorizationUrl(provider);
        return ResponseEntity.ok(Map.of("url", authUrl));
    }

    @GetMapping("/oauth/callback/{provider}")
    public ResponseEntity<?> callback(@PathVariable String provider, @RequestParam("code") String code) {
        if (code == null || code.isEmpty()) {
            throw new BadRequestException("Invalid code");
        }
        providerService.processOAuthCallback(provider, code);
        return ResponseEntity.noContent().build();
    }
}
