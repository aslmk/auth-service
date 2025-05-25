package com.aslmk.authenticationservice.service.Impl;

import com.aslmk.authenticationservice.config.RecaptchaConfig;
import com.aslmk.authenticationservice.dto.RecaptchaResponse;
import com.aslmk.authenticationservice.service.RecaptchaService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaServiceImpl implements RecaptchaService {

    private final RecaptchaConfig recaptchaConfig;
    private final RestTemplate restTemplate;

    public RecaptchaServiceImpl(RecaptchaConfig recaptchaConfig, RestTemplate restTemplate) {
        this.recaptchaConfig = recaptchaConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    public RecaptchaResponse validateToken(String recaptchaToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", recaptchaConfig.getSecretKey());
        map.add("response", recaptchaToken);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<RecaptchaResponse> response = restTemplate.exchange(recaptchaConfig.getVerifyUrl(),
                HttpMethod.POST,
                entity,
                RecaptchaResponse.class);
        return response.getBody();
    }
}
