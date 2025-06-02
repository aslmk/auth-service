package com.aslmk.authenticationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()
                )).toList();
        return new ResponseEntity<>(
                getErrorResponse("Validation failed", errors, HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleUsernameExists(UsernameAlreadyExistsException ex) {
        return new ResponseEntity<>(getErrorResponse(ex.getMessage(), List.of(), HttpStatus.CONFLICT), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleEmailExists(EmailAlreadyExistsException ex) {
        return new ResponseEntity<>(getErrorResponse(ex.getMessage(), List.of(), HttpStatus.CONFLICT), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(getErrorResponse(ex.getMessage(), List.of(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Map<String, Object>> handleAuthenticationFailed(AuthenticationFailedException ex) {
        return new ResponseEntity<>(getErrorResponse(ex.getMessage(), List.of(), HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RecaptchaValidationFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleRecaptchaValidationFailed(RecaptchaValidationFailedException ex) {
        List<Map<String, String>> errors = ex.getErrorCodes()
                .stream()
                .map(error -> Map.of("error", error))
                .toList();
        return new ResponseEntity<>(getErrorResponse(ex.getMessage(), errors, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleVerificationTokenExpired(VerificationTokenExpiredException ex) {
        return new ResponseEntity<>(getErrorResponse(ex.getMessage(), List.of(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VerificationTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleVerificationTokenNotFound(VerificationTokenNotFoundException ex) {
        return new ResponseEntity<>(getErrorResponse(ex.getMessage(), List.of(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return new ResponseEntity<>(getErrorResponse(ex.getMessage(), List.of(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> getErrorResponse(String message, List<Map<String, String>> errors, HttpStatus status) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}
