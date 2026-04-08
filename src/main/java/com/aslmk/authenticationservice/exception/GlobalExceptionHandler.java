package com.aslmk.authenticationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .filter(m -> m.getDefaultMessage() != null)
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()
                )).toList();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return buildErrorResponse("Validation failed",
                status.value(), status.getReasonPhrase(), errors);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUsernameExists(UsernameAlreadyExistsException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailExists(EmailAlreadyExistsException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UsernameNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleBadCredentials(BadCredentialsException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(RecaptchaValidationFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRecaptchaValidationFailed(RecaptchaValidationFailedException ex) {
        List<Map<String, String>> errors = ex.getErrorCodes()
                .stream()
                .map(error -> Map.of("error", error))
                .toList();

        HttpStatus status = HttpStatus.BAD_REQUEST;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase(), errors);
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleVerificationTokenExpired(VerificationTokenExpiredException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(VerificationTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleVerificationTokenNotFound(VerificationTokenNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(BadRequestException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProviderNotFound(ProviderNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(PasswordResetTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePasswordResetTokenExpired(PasswordResetTokenExpiredException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(PasswordResetTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePasswordResetTokenNotFound(PasswordResetTokenNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(OAuthException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOAuthException(OAuthException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(TwoFactorTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTwoFactorTokenExpired(TwoFactorTokenExpiredException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(TwoFactorTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTwoFactorTokenNotFound(TwoFactorTokenNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServiceException(ServiceException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return buildErrorResponse(ex.getMessage(), status.value(), status.getReasonPhrase());
    }

    private ErrorResponse buildErrorResponse(String message, int status, String error) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .build();
    }

    private ErrorResponse buildErrorResponse(String message, int status, String error, List<Map<String, String>> errors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .details(errors)
                .build();
    }
}
