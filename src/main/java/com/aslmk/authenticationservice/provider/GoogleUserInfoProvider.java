package com.aslmk.authenticationservice.provider;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GoogleUserInfoProvider implements OAuthUserInfoProvider {
    private final RestTemplate restTemplate;

    public GoogleUserInfoProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getProviderName() {
        return "google";
    }

    @Override
    public OAuthUserInfo getUserInfoByCode(String code, ProviderProperties provider) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", provider.getClientId());
        params.add("client_secret", provider.getClientSecret());
        params.add("redirect_uri", provider.getRedirectUri());
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(provider.getTokenUri(), request, Map.class);

        String accessToken = (String) response.getBody().get("access_token");

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<?> userRequest = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                provider.getUserInfoUri(), HttpMethod.GET, userRequest, Map.class
        );
        return parseUserInfo(userInfoResponse.getBody(), response.getBody());
    }

    private OAuthUserInfo parseUserInfo(Map userInfo, Map response) {
        return OAuthUserInfo.builder()
                .id((String) userInfo.get("sub"))
                .name((String) userInfo.get("name"))
                .email((String) userInfo.get("email"))
                .picture((String) userInfo.get("picture"))
                .accessToken((String) response.get("access_token"))
                .refreshToken((String) response.get("refresh_token"))
                .expiresAt((Integer) response.get("expires_in"))
                .provider(getProviderName())
                .build();
    }
}
