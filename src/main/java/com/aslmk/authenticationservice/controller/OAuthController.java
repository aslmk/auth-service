package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.auth.core.AuthProcessor;
import com.aslmk.authenticationservice.auth.result.AuthStatusResponse;
import com.aslmk.authenticationservice.dto.OAuthProviderConnectResponse;
import com.aslmk.authenticationservice.auth.result.AuthResult;
import com.aslmk.authenticationservice.auth.payload.AuthPayload;
import com.aslmk.authenticationservice.auth.payload.OAuth2AuthPayload;
import com.aslmk.authenticationservice.dto.OAuthUserDto;
import com.aslmk.authenticationservice.exception.BadRequestException;
import com.aslmk.authenticationservice.provider.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class OAuthController {

    private final ProviderService providerService;
    private final AuthProcessor authProcessor;

    public OAuthController(ProviderService providerService, AuthProcessor authProcessor) {
        this.providerService = providerService;
        this.authProcessor = authProcessor;
    }

    @GetMapping("/oauth/connect/{provider}")
    public ResponseEntity<OAuthProviderConnectResponse> connect(@PathVariable String provider) {
        String authUrl = providerService.buildAuthorizationUrl(provider);
        OAuthProviderConnectResponse response = new OAuthProviderConnectResponse(authUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/oauth/callback/{provider}")
    public ResponseEntity<AuthStatusResponse> callback(@PathVariable String provider,
                                      @RequestParam("code") String code) {
        OAuthUserDto oAuthUser = providerService.processOAuthCallback(provider, code);

        AuthPayload authPayload = OAuth2AuthPayload.builder()
                .email(oAuthUser.getEmail())
                .build();

        AuthResult result = authProcessor.process(authPayload);

        AuthStatusResponse response = new AuthStatusResponse(result.type().name());

        return ResponseEntity.ok(response);
    }

}
