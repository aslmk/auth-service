package com.aslmk.authenticationservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RecaptchaResponse {
    private boolean success;
    @JsonAlias("challenge_ts")
    private LocalDateTime challengeTs;
    private String hostName;
    @JsonAlias("error-codes")
    private List<String> errorCodes;
}
