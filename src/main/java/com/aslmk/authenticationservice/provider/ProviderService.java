package com.aslmk.authenticationservice.provider;

import com.aslmk.authenticationservice.dto.LoginRequestDto;
import com.aslmk.authenticationservice.entity.AccountEntity;
import com.aslmk.authenticationservice.entity.UserEntity;
import com.aslmk.authenticationservice.exception.ProviderNotFoundException;
import com.aslmk.authenticationservice.service.AccountService;
import com.aslmk.authenticationservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProviderService implements OAuthService {
    private final BaseProviderOptions providerOptions;
    private final AccountService accountService;
    private final UserService userService;
    private final Map<String, OAuthUserInfoProvider> userInfoProviders;

    public ProviderService(
            BaseProviderOptions providerOptions,
            AccountService accountService,
            UserService userService,
            List<OAuthUserInfoProvider> providers) {
        this.providerOptions = providerOptions;
        this.accountService = accountService;
        this.userService = userService;
        this.userInfoProviders = providers.stream()
                .collect(Collectors.toMap(OAuthUserInfoProvider::getProviderName, provider -> provider));
    }

    @Override
    public String buildAuthorizationUrl(String providerName) {
        ProviderProperties provider = getProvider(providerName);
        String scope = provider.getScopes().replace(",", " ");

        return UriComponentsBuilder.fromUriString(provider.getAuthUri())
                .queryParam("client_id", provider.getClientId())
                .queryParam("redirect_uri", provider.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build().toUriString();
    }

    @Override
    public LoginRequestDto processOAuthCallback(String providerName, String code) {
        OAuthUserInfo userInfo = fetchUserInfo(providerName, code);

        LoginRequestDto login = new LoginRequestDto();

        Optional<AccountEntity> account = accountService.findByIdAndProvider(userInfo.getId(), userInfo.getProvider());
        if (account.isPresent()) {
            AccountEntity existingAccount = account.get();
            if (existingAccount.getUser() == null) {
                UserEntity user = userService.createUserFromOAuth(userInfo);
                accountService.linkAccountToUser(existingAccount, user, userInfo);
                accountService.updateAccountTokens(userInfo, existingAccount);
            }
            login.setEmail(userInfo.getEmail());
            login.setUsername(userInfo.getName());
            login.setPassword("");
            return login;
        }
        UserEntity user = userService.createUserFromOAuth(userInfo);
        accountService.createAccount(userInfo, user);
        login.setEmail(user.getEmail());
        login.setUsername(user.getUsername());
        login.setPassword("");
        return login;
    }

    private ProviderProperties getProvider(String name) {
        ProviderProperties provider = providerOptions.getProviders().get(name);
        if (provider == null) {
            throw new ProviderNotFoundException("No such provider: " + name);
        }
        return provider;
    }

    private OAuthUserInfo fetchUserInfo(String providerName, String code) {
        ProviderProperties provider = getProvider(providerName);
        OAuthUserInfoProvider userInfoProvider = userInfoProviders.get(providerName);
        if (userInfoProvider == null) {
            throw new ProviderNotFoundException("No such provider: " + providerName);
        }

        return userInfoProvider.getUserInfoByCode(code, provider);
    }
}
