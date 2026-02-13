package com.aslmk.authenticationservice.security;

import com.aslmk.authenticationservice.auth.result.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserContextFilter extends OncePerRequestFilter {

    private final UserContext userContext;

    public UserContextFilter(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            Object sessionUser = session.getAttribute(SecurityConstants.AUTHENTICATED_USER_SESSION_KEY);
            if (sessionUser instanceof AuthenticatedUser authUser) {
                userContext.setUser(authUser);
            }
        }

        filterChain.doFilter(request, response);
    }
}
