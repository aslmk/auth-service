package com.aslmk.authenticationservice.auth.core;

import com.aslmk.authenticationservice.auth.result.AuthResult;
import com.aslmk.authenticationservice.auth.payload.AuthPayload;

public interface AuthHandler<T extends AuthPayload> {
    boolean support(AuthPayload authPayload);
    AuthResult authenticate(T payload);
}
