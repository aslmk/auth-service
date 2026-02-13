package com.aslmk.authenticationservice.auth.core;

import com.aslmk.authenticationservice.auth.result.AuthResult;
import com.aslmk.authenticationservice.auth.payload.AuthPayload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthProcessor {

    private final List<AuthHandler<?>> authHandlers;

    public AuthProcessor(List<AuthHandler<?>> authHandlers) {
        this.authHandlers = authHandlers;
    }

    public AuthResult process(AuthPayload authPayload) {
        for (AuthHandler<?> authHandler : authHandlers) {
            if (authHandler.support(authPayload)) {
                return authenticate(authHandler, authPayload);
            }
        }

        throw new IllegalArgumentException("Unsupported auth payload: " + authPayload.getClass());
    }

    @SuppressWarnings("unchecked")
    private <T extends AuthPayload> AuthResult authenticate(AuthHandler<T> authHandler,
                                                            AuthPayload authPayload) {
        return authHandler.authenticate((T) authPayload);
    }
}
