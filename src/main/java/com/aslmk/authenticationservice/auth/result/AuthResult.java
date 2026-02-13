package com.aslmk.authenticationservice.auth.result;

public record AuthResult(
        AuthResultType type,
        AuthenticatedUser user
) {}
