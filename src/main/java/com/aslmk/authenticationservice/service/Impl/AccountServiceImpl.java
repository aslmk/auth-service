package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.entity.AccountEntity;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.provider.OAuthUserInfo;
import com.aslmk.authenticationservice.repository.AccountRepository;
import com.aslmk.authenticationservice.service.AccountService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<AccountEntity> findByIdAndProvider(String id, String provider) {
        return accountRepository.findByIdAndProvider(id, provider);
    }

    @Override
    public void linkAccountToUser(AccountEntity account, UserEntity user, OAuthUserInfo userInfo) {
        account.setUser(user);
        accountRepository.save(account);
    }

    @Override
    public void createAccount(OAuthUserInfo userInfo, UserEntity user) {
        AccountEntity accountEntity = AccountEntity.builder()
                .id(userInfo.getId())
                .user(user)
                .accessToken(userInfo.getAccessToken())
                .expiresAt(LocalDateTime.now().plusSeconds(userInfo.getExpiresAt()))
                .refreshToken(userInfo.getRefreshToken())
                .createdAt(LocalDateTime.now())
                .provider(userInfo.getProvider())
                .build();
        accountRepository.save(accountEntity);
    }

    @Override
    public void updateAccountTokens(OAuthUserInfo userInfo, AccountEntity account) {
        account.setAccessToken(userInfo.getAccessToken());
        account.setRefreshToken(userInfo.getRefreshToken());
        account.setExpiresAt(LocalDateTime.now().plusSeconds(userInfo.getExpiresAt()));
        accountRepository.save(account);
    }
}
