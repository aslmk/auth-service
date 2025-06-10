package com.aslmk.authenticationservice.provider;

import com.aslmk.authenticationservice.dto.GoogleTokenResponse;
import com.aslmk.authenticationservice.dto.GoogleUserInfoResponse;
import com.aslmk.authenticationservice.exception.OAuthException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
        GoogleTokenResponse tokenResponse = exchangeAccessToken(code, provider);

        String accessToken = tokenResponse.getAccessToken();

        GoogleUserInfoResponse userInfo = fetchUserInfo(accessToken, provider);

        return parseUserInfo(userInfo, tokenResponse);
    }

    private OAuthUserInfo parseUserInfo(GoogleUserInfoResponse userInfo, GoogleTokenResponse response) {
        return OAuthUserInfo.builder()
                .id(userInfo.getSub())
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .picture(userInfo.getPicture())
                .accessToken(response.getAccessToken())
                .refreshToken(response.getRefreshToken())
                .expiresAt(response.getExpiresIn())
                .provider(getProviderName())
                .build();
    }
    private GoogleTokenResponse exchangeAccessToken(String code, ProviderProperties provider) throws OAuthException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", provider.getClientId());
        params.add("client_secret", provider.getClientSecret());
        params.add("redirect_uri", provider.getRedirectUri());
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(provider.getTokenUri(), request, GoogleTokenResponse.class);

        return validateResponseBody(response, "Failed to retrieve access token from Google");
    }
    private GoogleUserInfoResponse fetchUserInfo(String accessToken, ProviderProperties provider) throws OAuthException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserInfoResponse> response = restTemplate.exchange(
                provider.getUserInfoUri(), HttpMethod.GET, request, GoogleUserInfoResponse.class
        );

        return validateResponseBody(response, "Failed to exchange access token to user info from Google");
    }
    private <T> T validateResponseBody(ResponseEntity<T> response, String errorMessage) {
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new OAuthException(errorMessage);
        }
        return response.getBody();
    }
}
