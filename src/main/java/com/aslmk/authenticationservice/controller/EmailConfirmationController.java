package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.service.Impl.EmailConfirmationService;
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
        name = "Email confirmation",
        description = "Endpoints for confirming user email addresses after registration"
)
@RestController
@RequestMapping("/auth")
public class EmailConfirmationController {

    private final EmailConfirmationService emailConfirmationService;

    public EmailConfirmationController(EmailConfirmationService emailConfirmationService) {
        this.emailConfirmationService = emailConfirmationService;
    }

    @Operation(
            summary = "Confirm user email address",
            description = "Confirms a user's email address using a verification token.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Email confirmation status returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Email confirmed successfully",
                                                    description = "Returned when email is successfully verified.",
                                                    value = """
                                                            {
                                                                "message": "Email confirmed successfully. You can now log in"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "Email already confirmed",
                                                    description = "Returned when user has already verified their email.",
                                                    value = """
                                                            {
                                                                "message": "Email already confirmed"
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Verification token or user not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Token not found",
                                                    description = "Returned when the verification token does not exist.",
                                                    value = """
                                                            {
                                                                "timestamp": "2025-10-29T11:18",
                                                                "status": 404,
                                                                "error": "Not Found",
                                                                "message": "Verification token not found",
                                                                "errors": []
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "User not found",
                                                    description = "Returned when the user associated with the token was not found.",
                                                    value = """
                                                            {
                                                                "timestamp": "2025-10-29T11:18",
                                                                "status": 404,
                                                                "error": "Not Found",
                                                                "message": "User not found for this token",
                                                                "errors": []
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Verification token expired",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = """
                                            {
                                                "timestamp": "2025-10-29T11:18",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "message": "Verification token has expired",
                                                "errors": []
                                            }
                                            """)
                            )
                    )
            }
    )
    @PostMapping("/email-confirmation")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token) {
        return ResponseEntity.ok().body(
                Map.of("message", emailConfirmationService.confirmEmail(token)));
    }
}
