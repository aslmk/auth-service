package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.auth.result.AuthResult;
import com.aslmk.authenticationservice.auth.result.AuthResultType;
import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.entity.AuthMethod;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.service.RegistrationService;
import com.aslmk.authenticationservice.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;
    private final EmailConfirmationService emailConfirmationService;

    public RegistrationServiceImpl(UserService userService, EmailConfirmationService emailConfirmationService) {
        this.userService = userService;
        this.emailConfirmationService = emailConfirmationService;
    }

    public AuthResult registerUser(RegistrationRequestDto request) {
        UserEntity userEntity = userService.saveUser(request,
                "",
                AuthMethod.CREDENTIALS,
                false);

        emailConfirmationService.sendVerificationToken(userEntity.getEmail());

        return new AuthResult(AuthResultType.EMAIL_VERIFICATION_REQUIRED, null);
    }
}
