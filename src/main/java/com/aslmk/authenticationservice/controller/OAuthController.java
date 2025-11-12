package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.dto.OAuthUserDto;
import com.aslmk.authenticationservice.dto.UserResponseDto;
import com.aslmk.authenticationservice.exception.BadRequestException;
import com.aslmk.authenticationservice.provider.OAuthService;
import com.aslmk.authenticationservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(
        name = "OAuth2 authentication",
        description = "Endpoints for initiating and handling user authentication via external OAuth2 providers"
)
@RestController
@RequestMapping("/auth")
public class OAuthController {

    private final OAuthService providerService;
    private final AuthService authService;

    public OAuthController(OAuthService providerService, AuthService authService) {
        this.providerService = providerService;
        this.authService = authService;
    }

    @Operation(
            summary = "Get OAuth2 authorization URL for the given provider",
            description = "Generates and returns the OAuth2 authorization URL for the specified provider. Use this URL to start the OAuth2 authentication flow.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully generated the OAuth2 authorization URL.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = """
                                            {
                                                "url" : "https://accounts.google.com/o/oauth2/v2/auth?client_id=client_id&redirect_uri=redirect_uri&response_type=code&scope=openid profile email&access_type=offline&prompt=consent"
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Returned when the specified OAuth2 provider is not supported or not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = """
                                            {
                                            "timestamp": "2025-10-29T11:18",
                                            "status": 404,
                                            "error": "Not Found",
                                            "message": "No such provider: provider_name"
                                            }
                                            """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/oauth/connect/{provider}")
    public ResponseEntity<Map<String, String>> connect(@PathVariable String provider) {
        String authUrl = providerService.buildAuthorizationUrl(provider);
        return ResponseEntity.ok(Map.of("url", authUrl));
    }

    @Operation(
            summary = "Handle OAuth2 callback and authenticate user",
            description = "Handles the OAuth2 callback by exchanging the authorization code for user information and completing the authentication process.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully authenticated via OAuth2 provider.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Provider or user not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Provider not found",
                                                    description = "Returned when the specified OAuth2 provider does not exist.",
                                                    value = """
                                                            {
                                                            "timestamp": "2025-10-29T11:18",
                                                            "status": 404,
                                                            "error": "Not Found",
                                                            "message": "No such provider: provider_name"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "User not found",
                                                    description = "Returned when user not found.",
                                                    value = """
                                                            {
                                                              "timestamp": "2025-10-29T11:18",
                                                              "status": 404,
                                                              "error": "Not Found",
                                                              "message": "User example@example.com not found"                                                            }
                                                            """
                                            ),

                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or missing authorization code.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example=
                                            """
                                                {
                                                     "timestamp": "2025-10-29T11:18",
                                                     "status": 400,
                                                     "error": "Bad Request",
                                                     "message": "Invalid code"
                                                }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "User role not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example =
                                            """
                                                {
                                                     "timestamp": "2025-10-29T11:18",
                                                     "status": 500,
                                                     "error": "Internal Server Error",
                                                     "message": "Default user role not found"
                                                }
                                            """
                                    )
                            )
                    )
            }
    )
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
