package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.dto.RegistrationRequestDto;
import com.aslmk.authenticationservice.entity.AccountEntity;
import com.aslmk.authenticationservice.entity.AuthMethod;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.entity.UserRoleEntity;
import com.aslmk.authenticationservice.exception.EmailAlreadyExistsException;
import com.aslmk.authenticationservice.exception.ServiceException;
import com.aslmk.authenticationservice.exception.UsernameAlreadyExistsException;
import com.aslmk.authenticationservice.repository.AccountEntityRepository;
import com.aslmk.authenticationservice.repository.UserRepository;
import com.aslmk.authenticationservice.repository.UserRoleRepository;
import com.aslmk.authenticationservice.service.UserService;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountEntityRepository accountEntityRepository;

    public UserServiceImpl(UserRoleRepository userRoleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, AccountEntityRepository accountEntityRepository) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountEntityRepository = accountEntityRepository;
    }

    @Transactional
    public UserEntity saveUser(RegistrationRequestDto registrationRequestDto,
                               String pictureUrl,
                               AuthMethod authMethod,
                               boolean isVerified)
            throws UsernameAlreadyExistsException, EmailAlreadyExistsException, ServiceException {
        try {
            UserRoleEntity userRoleEntity = userRoleRepository.findByRoleName("USER")
                    .orElseThrow(() -> new ServiceException("Default user role not found"));

            UserEntity userEntity = UserEntity.builder()
                    .username(registrationRequestDto.getUsername())
                    .password(
                            !registrationRequestDto.getPassword().isBlank()
                                    ? passwordEncoder.encode(registrationRequestDto.getPassword())
                                    : ""
                    )
                    .email(registrationRequestDto.getEmail())
                    .role(userRoleEntity)
                    .authMethod(authMethod)
                    .verified(isVerified)
                    .pictureUrl(pictureUrl)
                    .build();

            List<AccountEntity> userAccounts = accountEntityRepository.findAllByUserId(userEntity.getId())
                    .orElse(Collections.emptyList());
            userEntity.setAccounts(userAccounts);

            return userRepository.save(userEntity);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException cve) {
                String constraintName = cve.getConstraintName();

                if (constraintName == null) {
                    throw new ServiceException("Unknown database constraint violation: " + e);
                }

                switch (constraintName) {
                    case "users_username_key" ->
                            throw new UsernameAlreadyExistsException(
                                    String.format("User with username \"%s\" already exists", registrationRequestDto.getUsername())
                            );
                    case "users_email_key" ->
                            throw new EmailAlreadyExistsException(
                                    String.format("User with email \"%s\" already exists", registrationRequestDto.getEmail())
                            );
                    default -> throw new ServiceException("Database constraint violation: " + constraintName);
                }
            } else {
                throw new ServiceException("Unexpected error occurred while saving user: " + e.getMessage());
            }
        }

    }


    @Override
    public Optional<UserEntity> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateUserVerificationStatus(UserEntity user, boolean verified) {
        user.setVerified(verified);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = findUserByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with email \"%s\" not found", email))
        );
        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().toString())
                .build();

    }
}
