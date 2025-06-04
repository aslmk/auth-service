package com.aslmk.authenticationservice.service;

import com.aslmk.authenticationservice.entity.AccountEntity;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.provider.OAuthUserInfo;

import java.util.Optional;

public interface AccountService {
    Optional<AccountEntity> findByIdAndProvider(String id, String provider);
    void linkAccountToUser(AccountEntity account, UserEntity user, OAuthUserInfo userInfo);
    void createAccount(OAuthUserInfo userInfo, UserEntity user);
    void updateAccountTokens(OAuthUserInfo userInfo, AccountEntity account);
}
