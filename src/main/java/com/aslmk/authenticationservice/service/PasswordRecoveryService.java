package com.aslmk.authenticationservice.service;

public interface PasswordRecoveryService {
    String reset(String email);
    String newPassword(String newPassword, String token);
}
