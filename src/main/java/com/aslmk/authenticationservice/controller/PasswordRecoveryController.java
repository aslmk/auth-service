package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.annotation.ValidateRecaptcha;
import com.aslmk.authenticationservice.service.PasswordRecoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(
        name = "Password recovery",
        description = "Endpoints for password reset and recovery process"
)
@RestController
@RequestMapping("/auth")
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    public PasswordRecoveryController(PasswordRecoveryService passwordRecoveryService) {
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @Operation(
            summary = "Set new password using recovery token",
            description = "Set a new password using a valid password recovery token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password successfully changed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example =
                                            """
                                                        {
                                                             "message": "Password was successfully changed"
                                                        }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Token or user not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Token not found",
                                                    description = "Returned when the password reset token does not exist.",
                                                    value = """
                                                            {
                                                            "timestamp": "2025-10-29T11:18",
                                                            "status": 404,
                                                            "error": "Not Found",
                                                            "message": "Password reset token not found",
                                                            "errors": []
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "User not found",
                                                    description = "Returned when user not found for the given token.",
                                                    value = """
                                                            {
                                                              "timestamp": "2025-10-29T11:18",
                                                              "status": 404,
                                                              "error": "Not Found",
                                                              "message": "User not found for this token",
                                                              "errors": []
                                                            }
                                                            """
                                            ),

                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Password reset token expired",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example =
                                            """
                                                        {
                                                             "timestamp": "2025-10-29T11:18",
                                                             "status": 400,
                                                             "error": "Bad Request",
                                                             "message": "Password reset token has expired",
                                                             "errors": []
                                                        }
                                                    """
                                    )
                            )
                    )
            }
    )
    @ValidateRecaptcha
    @PostMapping("/password-recovery/new")
    public ResponseEntity<?> newPassword(@RequestParam("newPassword") String newPassword,
                                         @RequestParam("token") String token) {
        String message = passwordRecoveryService.newPassword(newPassword, token);
        return ResponseEntity.ok(Map.of("message", message));
    }


    @Operation(
            summary = "Send password reset token to user email",
            description = "Sends a password reset token to the user's registered email address.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password reset token successfully sent",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example =
                                            """
                                                        {
                                                             "message": "Password reset token was successfully sent to your email"
                                                        }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User with the given email not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = """
                                            {
                                              "timestamp": "2025-10-29T11:18",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "User with email example@example.com not found",
                                              "errors": []
                                            }
                                            """
                                    )
                            )
                    )
            }
    )
    @ValidateRecaptcha
    @PostMapping("/password-recovery/reset")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email) {
        String message = passwordRecoveryService.reset(email);
        return ResponseEntity.ok(Map.of("message", message));
    }
}
