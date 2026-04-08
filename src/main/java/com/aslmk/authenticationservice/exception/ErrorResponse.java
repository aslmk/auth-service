package com.aslmk.authenticationservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String error, String message, long timestamp, List<ValidationError> details) {}
