package com.aslmk.authenticationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .filter(m -> m.getDefaultMessage() != null)
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        return buildErrorResponse(ErrorCode.FIELD_VALIDATION_FAILED.name(), "Validation failed", errors);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUsernameExists(UsernameAlreadyExistsException ex) {
    return buildErrorResponse(ErrorCode.USERNAME_ALREADY_EXISTS.name(), ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailExists(EmailAlreadyExistsException ex) {
        return buildErrorResponse(ErrorCode.EMAIL_ALREADY_EXISTS.name(), ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserNotFoundException ex) {
        return buildErrorResponse(ErrorCode.USER_NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UsernameNotFoundException ex) {
        return buildErrorResponse(ErrorCode.USERNAME_NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleBadCredentials(BadCredentialsException ex) {
        return buildErrorResponse(ErrorCode.BAD_CREDENTIALS.name(), ex.getMessage());
    }

    @ExceptionHandler(RecaptchaValidationFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRecaptchaValidationFailed(RecaptchaValidationFailedException ex) {
        List<ValidationError> errors = ex.getErrorCodes()
                .stream()
                .map(error -> new ValidationError("error", error))
                .toList();

        return buildErrorResponse(ErrorCode.RECAPTCHA_VALIDATION_FAILED.name(),
                "Recaptcha validation failed", errors);
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleVerificationTokenExpired(VerificationTokenExpiredException ex) {
        return buildErrorResponse(ErrorCode.VERIFICATION_TOKEN_EXPIRED.name(), ex.getMessage());
    }

    @ExceptionHandler(VerificationTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleVerificationTokenNotFound(VerificationTokenNotFoundException ex) {
        return buildErrorResponse(ErrorCode.VERIFICATION_TOKEN_NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(BadRequestException ex) {
        return buildErrorResponse(ErrorCode.BAD_REQUEST.name(), ex.getMessage());
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProviderNotFound(ProviderNotFoundException ex) {
        return buildErrorResponse(ErrorCode.PROVIDER_NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(PasswordResetTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePasswordResetTokenExpired(PasswordResetTokenExpiredException ex) {
        return buildErrorResponse(ErrorCode.PASSWORD_RESET_TOKEN_EXPIRED.name(), ex.getMessage());
    }

    @ExceptionHandler(PasswordResetTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePasswordResetTokenNotFound(PasswordResetTokenNotFoundException ex) {
        return buildErrorResponse(ErrorCode.PASSWORD_RESET_TOKEN_NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(OAuthException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOAuthException(OAuthException ex) {
        return buildErrorResponse(ErrorCode.INTERNAL_ERROR.name(), ex.getMessage());
    }

    @ExceptionHandler(TwoFactorTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTwoFactorTokenExpired(TwoFactorTokenExpiredException ex) {
        return buildErrorResponse(ErrorCode.TWO_FACTOR_TOKEN_EXPIRED.name(), ex.getMessage());
    }

    @ExceptionHandler(TwoFactorTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTwoFactorTokenNotFound(TwoFactorTokenNotFoundException ex) {
        return buildErrorResponse(ErrorCode.TWO_FACTOR_TOKEN_NOT_FOUND.name(), ex.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServiceException(ServiceException ex) {
        return buildErrorResponse(ErrorCode.INTERNAL_ERROR.name(), ex.getMessage());
    }

    private ErrorResponse buildErrorResponse(String message, String error) {
        return new ErrorResponse(error, message, Instant.now().toEpochMilli(), null);
    }

    private ErrorResponse buildErrorResponse(String error, String message, List<ValidationError> errors) {
        return new ErrorResponse(error, message, Instant.now().toEpochMilli(), errors);
    }
}
