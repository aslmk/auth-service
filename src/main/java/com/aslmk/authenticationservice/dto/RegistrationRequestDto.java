package com.aslmk.authenticationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequestDto {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "The size should be in the range from 3 to 20")
    private String username;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must contain at least 6 symbols")
    private String password;
    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;
}
