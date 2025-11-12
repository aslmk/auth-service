package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.annotation.ValidateRecaptcha;
import com.aslmk.authenticationservice.dto.LoginRequestDto;
import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(
        name = "Authentication",
        description = "Endpoints for user authentication and registration"
)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register new user",
            description = "Creates new account and sends confirmation email",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User registered successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = """
                                            {
                                                "message" : "Registration successful. Check your inbox to verify your email" 
                                            }
                                            """
                                    )
                            )

                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Returned when the request body contains validation errors",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = """
                                            {
                                              "timestamp": "2025-10-29T11:18",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Validation failed",
                                              "details": [
                                                {
                                                  "field": "password",
                                                  "message": "Password is required"
                                                },
                                                {
                                                  "field": "username",
                                                  "message": "Username is required"
                                                },
                                                {
                                                  "field": "email",
                                                  "message": "Invalid email format"
                                                },
                                                {
                                                  "field": "confirmPassword",
                                                  "message": "Passwords do not match"
                                                },
                                                {
                                                  "field": "username",
                                                  "message": "The size should be in the range from 3 to 20"
                                                },
                                                {
                                                  "field": "password",
                                                  "message": "Password must contain at least 6 symbols"
                                                }
                                              ]
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict — username or email already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Username already exists",
                                                    description = "Returned when username is already exists",
                                                    value = """
                                                            {
                                                              "timestamp": "2025-10-29T11:18",
                                                              "status": 409,
                                                              "error": "Conflict",
                                                              "message": "User with username \\"example_username\\" already exists"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "Email already exists",
                                                    description = "Returned when email is already exists",
                                                    value = """
                                                            {
                                                              "timestamp": "2025-10-29T11:18",
                                                              "status": 409,
                                                              "error": "Conflict",
                                                              "message": "User with email \\"example@example.com\\" already exists"
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )

            }
    )
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

    @Operation(
            summary = "Login existing user",
            description = """
                    Authenticates user by username/password.
                    Sends verification token if user's email is not verified.
                    Sends two-factor token if two-factor auth is enabled.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User authenticates successfully or requires two-factor verification",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Successful login",
                                                    summary = "User logged in successfully",
                                                    description = "Returned when user credentials are valid and no 2FA is required",
                                                    value = """
                                                            {
                                                              "message": "Login successful"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "Two-factor required",
                                                    summary = "2FA token sent to user's email",
                                                    description = "Returned when user enabled 2FA and must verify token",
                                                    value = """
                                                            {
                                                              "message": "Two factor token was sent to your email"
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found or 2FA token is missing",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "User not found",
                                                    description = "Returned when user not found",
                                                    value = """
                                                            {
                                                              "timestamp": "2025-10-29T11:18",
                                                              "status": 404,
                                                              "error": "Not Found",
                                                              "message": "User example@example.com not found"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "Two factor token not found",
                                                    description = "Returned when 2FA is enabled and the token is not found",
                                                    value = """
                                                            {
                                                              "timestamp": "2025-10-29T11:18",
                                                              "status": 404,
                                                              "error": "Not Found",
                                                              "message": "Two factor token not found"
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Unable to authenticate user",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = """
                                            {
                                              "timestamp": "2025-10-29T11:18",
                                              "status": 403,
                                              "error": "Bad request",
                                              "message": "Email or password is incorrect"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request — see examples for possible causes",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Two factor token expired",
                                                    description = "Returned when 2FA is enabled and the token is expired",
                                                    value = """
                                                            {
                                                              "timestamp": "2025-10-29T11:18",
                                                              "status": 400,
                                                              "error": "Bad request",
                                                              "message": "Two factor token expired"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "Two factor token is not valid",
                                                    description = "Returned when 2FA is enabled and the token is not valid",
                                                    value = """
                                                            {
                                                              "timestamp": "2025-10-29T11:18",
                                                              "status": 400,
                                                              "error": "Bad request",
                                                              "message": "Two factor token is not valid"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "Email is not verified",
                                                    description = "Returned when user's email is not verified",
                                                    value = """
                                                            {
                                                              "timestamp": "2025-10-29T11:18",
                                                              "status": 400,
                                                              "error": "Bad request",
                                                              "message": "Email not verified. Check your inbox for a verification token"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "Validation error",
                                                    description = "Returned when the request body contains validation errors",
                                                    value = """
                                                            {
                                                              "timestamp": "2025-10-29T11:18",
                                                              "status": 400,
                                                              "error": "Bad Request",
                                                              "message": "Validation failed",
                                                              "details": [
                                                                {
                                                                  "field": "password",
                                                                  "message": "Password is required"
                                                                },
                                                                {
                                                                  "field": "email",
                                                                  "message": "Invalid email format"
                                                                },
                                                                {
                                                                  "field": "username",
                                                                  "message": "Username is required"
                                                                }
                                                              ]
                                                            }
                                                            """
                                            )
                                    })
                    )
            }
    )
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

    @Operation(
            summary = "Logout user",
            description = "Logs out current user",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User logged out successfully")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
        authService.logout(httpRequest);
        return ResponseEntity.noContent().build();
    }
}
