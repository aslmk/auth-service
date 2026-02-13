package com.aslmk.authenticationservice.security;

import com.aslmk.authenticationservice.auth.result.AuthenticatedUser;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@Getter
@RequestScope
public class UserContext {

    private AuthenticatedUser user;

    void setUser(AuthenticatedUser user) {
        this.user = user;
    }
}
