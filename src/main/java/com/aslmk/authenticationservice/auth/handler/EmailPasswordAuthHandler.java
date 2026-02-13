package com.aslmk.authenticationservice.auth.handler;

import com.aslmk.authenticationservice.auth.result.AuthenticatedUser;
import com.aslmk.authenticationservice.auth.core.AuthHandler;
import com.aslmk.authenticationservice.auth.result.AuthResult;
import com.aslmk.authenticationservice.auth.result.AuthResultType;
import com.aslmk.authenticationservice.auth.payload.AuthPayload;
import com.aslmk.authenticationservice.auth.payload.EmailPasswordAuthPayload;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.exception.BadCredentialsException;
import com.aslmk.authenticationservice.exception.BadRequestException;
import com.aslmk.authenticationservice.exception.UserNotFoundException;
import com.aslmk.authenticationservice.service.Impl.EmailConfirmationService;
import com.aslmk.authenticationservice.service.Impl.TwoFactorAuthService;
import com.aslmk.authenticationservice.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EmailPasswordAuthHandler implements AuthHandler<EmailPasswordAuthPayload> {

    private final UserService userService;
    private final EmailConfirmationService emailConfirmationService;
    private final TwoFactorAuthService twoFactorAuthService;
    private final PasswordEncoder passwordEncoder;

    public EmailPasswordAuthHandler(UserService userService,
                                    EmailConfirmationService emailConfirmationService,
                                    TwoFactorAuthService twoFactorAuthService,
                                    PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailConfirmationService = emailConfirmationService;
        this.twoFactorAuthService = twoFactorAuthService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean support(AuthPayload authPayload) {
        return authPayload instanceof EmailPasswordAuthPayload;
    }

    @Override
    public AuthResult authenticate(EmailPasswordAuthPayload payload) {
        UserEntity user = userService.findUserByEmail(payload.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User not found: email='%s'", payload.getEmail()))
                );

        if (!user.isVerified()) {
            emailConfirmationService.sendVerificationToken(user.getEmail());
            throw new BadRequestException("Email not verified. Check your inbox for a verification token");
        }

        if (user.isTwoFactorEnabled()) {
            if (payload.getCode() == null) {
                twoFactorAuthService.sendTwoFactorToken(user.getEmail());
                return new AuthResult(AuthResultType.TWO_FACTOR_REQUIRED, null);
            }
            twoFactorAuthService.validateTwoFactorToken(user.getEmail(), payload.getCode());
        }

        if (!passwordEncoder.matches(payload.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Email or password incorrect");
        }

        AuthenticatedUser authUser = new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                Set.of(user.getRole().getRoleName()),
                user.isVerified(),
                user.isTwoFactorEnabled());

        return new AuthResult(AuthResultType.AUTHENTICATED, authUser);
    }
}
