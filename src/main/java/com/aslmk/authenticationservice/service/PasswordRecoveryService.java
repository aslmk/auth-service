package com.aslmk.authenticationservice.service;

import com.aslmk.authenticationservice.account.action.AccountActionResult;

public interface PasswordRecoveryService {
    AccountActionResult reset(String email);
    AccountActionResult newPassword(String newPassword, String token);
}
