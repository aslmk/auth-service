package com.aslmk.authenticationservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .filter(m -> m.getDefaultMessage() != null)
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        log.warn("Validation error: {}", ex.getMessage());

        return buildErrorResponse(ErrorCode.FIELD_VALIDATION_FAILED.name(), "Validation failed", errors);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUsernameExists(UsernameAlreadyExistsException ex) {
        log.warn("Username already exists: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.USERNAME_ALREADY_EXISTS.name(), ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailExists(EmailAlreadyExistsException ex) {
        log.warn("Email already exists: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.EMAIL_ALREADY_EXISTS.name(), ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.USER_NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UsernameNotFoundException ex) {
        log.warn("Username not found: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.USERNAME_NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleBadCredentials(BadCredentialsException ex) {
        log.warn("Bad credentials: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.BAD_CREDENTIALS.name(), ex.getMessage());
    }

    @ExceptionHandler(RecaptchaValidationFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRecaptchaValidationFailed(RecaptchaValidationFailedException ex) {
        List<ValidationError> errors = ex.getErrorCodes()
                .stream()
                .map(error -> new ValidationError("error", error))
                .toList();

        log.warn("Recaptcha validation failed: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.RECAPTCHA_VALIDATION_FAILED.name(),
                "Recaptcha validation failed", errors);
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleVerificationTokenExpired(VerificationTokenExpiredException ex) {
        log.warn("Verification token expired: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.VERIFICATION_TOKEN_EXPIRED.name(), ex.getMessage());
    }

    @ExceptionHandler(VerificationTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleVerificationTokenNotFound(VerificationTokenNotFoundException ex) {
        log.warn("Verification token not found: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.VERIFICATION_TOKEN_NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(BadRequestException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.BAD_REQUEST.name(), ex.getMessage());
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProviderNotFound(ProviderNotFoundException ex) {
        log.warn("Provider not found: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.PROVIDER_NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(PasswordResetTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePasswordResetTokenExpired(PasswordResetTokenExpiredException ex) {
        log.warn("Password reset token expired: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.PASSWORD_RESET_TOKEN_EXPIRED.name(), ex.getMessage());
    }

    @ExceptionHandler(PasswordResetTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePasswordResetTokenNotFound(PasswordResetTokenNotFoundException ex) {
        log.warn("Password reset token not found: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.PASSWORD_RESET_TOKEN_NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(TwoFactorTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTwoFactorTokenExpired(TwoFactorTokenExpiredException ex) {
        log.warn("Two-factor token expired: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.TWO_FACTOR_TOKEN_EXPIRED.name(), ex.getMessage());
    }

    @ExceptionHandler(TwoFactorTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTwoFactorTokenNotFound(TwoFactorTokenNotFoundException ex) {
        log.warn("Two-factor token not found: {}", ex.getMessage());
        return buildErrorResponse(ErrorCode.TWO_FACTOR_TOKEN_NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAll(Exception ex) {
        log.error("Unexpected error", ex);
        return buildErrorResponse(ErrorCode.INTERNAL_ERROR.name(), "Internal Server Error");
    }

    @ExceptionHandler(ParameterMissingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(ParameterMissingException ex) {
        log.warn(ex.getMessage());
        return buildErrorResponse(ErrorCode.PARAMETER_MISSING.name(), ex.getMessage());
    }

    private ErrorResponse buildErrorResponse(String message, String error) {
        return new ErrorResponse(error, message, Instant.now().toEpochMilli(), null);
    }

    private ErrorResponse buildErrorResponse(String error, String message, List<ValidationError> errors) {
        return new ErrorResponse(error, message, Instant.now().toEpochMilli(), errors);
    }
}
