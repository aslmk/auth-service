package com.aslmk.authenticationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .filter(m -> m.getDefaultMessage() != null)
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()
                )).toList();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                buildErrorResponse(
                        "Validation failed",
                        status.value(),
                        status.getReasonPhrase(),
                        errors
                ),
                status);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleUsernameExists(UsernameAlreadyExistsException ex) {
        HttpStatus status = HttpStatus.CONFLICT;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        HttpStatus status = HttpStatus.CONFLICT;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailed(AuthenticationFailedException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);
    }

    @ExceptionHandler(RecaptchaValidationFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleRecaptchaValidationFailed(RecaptchaValidationFailedException ex) {
        List<Map<String, String>> errors = ex.getErrorCodes()
                .stream()
                .map(error -> Map.of("error", error))
                .toList();

        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase(),
                        errors
                ),
                status);
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleVerificationTokenExpired(VerificationTokenExpiredException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);
    }

    @ExceptionHandler(VerificationTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleVerificationTokenNotFound(VerificationTokenNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleProviderNotFound(ProviderNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);
    }

    @ExceptionHandler(PasswordResetTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handlePasswordResetTokenExpired(PasswordResetTokenExpiredException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);    }

    @ExceptionHandler(PasswordResetTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handlePasswordResetTokenNotFound(PasswordResetTokenNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);
    }

    @ExceptionHandler(OAuthException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleOAuthException(OAuthException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);
    }

    @ExceptionHandler(TwoFactorTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleTwoFactorTokenExpired(TwoFactorTokenExpiredException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);
    }

    @ExceptionHandler(TwoFactorTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleTwoFactorTokenNotFound(TwoFactorTokenNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(
                buildErrorResponse(
                        ex.getMessage(),
                        status.value(),
                        status.getReasonPhrase()
                ),
                status);
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
