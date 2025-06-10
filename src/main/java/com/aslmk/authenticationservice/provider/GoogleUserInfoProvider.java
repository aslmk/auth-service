package com.aslmk.authenticationservice.provider;

import com.aslmk.authenticationservice.exception.OAuthException;
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
    public OAuthUserInfo getUserInfoByCode(String code, ProviderProperties provider) throws OAuthException {
        Map tokenResponse = exchangeAccessToken(code, provider);

        String accessToken = (String) tokenResponse.get("access_token");

        Map userInfo = fetchUserInfo(accessToken, provider);

        return parseUserInfo(userInfo, tokenResponse);
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
    private Map exchangeAccessToken(String code, ProviderProperties provider) throws OAuthException {
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

        return validateResponseBody(response, "Failed to retrieve access token from Google");
    }
    private Map fetchUserInfo(String accessToken, ProviderProperties provider) throws OAuthException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> userRequest = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                provider.getUserInfoUri(), HttpMethod.GET, userRequest, Map.class
        );

        return validateResponseBody(response, "Failed to exchange access token to user info from Google");
    }
    private Map validateResponseBody(ResponseEntity<Map> response, String errorMessage) {
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new OAuthException(errorMessage);
        }
        return response.getBody();
    }
}
