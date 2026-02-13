package com.aslmk.authenticationservice.auth.result;

import java.util.Set;

public record AuthenticatedUser(
        Long id,
        String email,
        Set<String> roles,
        boolean emailVerified,
        boolean twoFactorPassed
) {}
