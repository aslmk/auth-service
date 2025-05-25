package com.aslmk.authenticationservice.aspect;

import com.aslmk.authenticationservice.exception.RecaptchaValidationFailedException;
import com.aslmk.authenticationservice.dto.RecaptchaResponse;
import com.aslmk.authenticationservice.service.RecaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@ConditionalOnProperty(name = "google.recaptcha.enabled", havingValue = "true")
public class RecaptchaAspect {
    private final RecaptchaService recaptchaService;

    public RecaptchaAspect(RecaptchaService recaptchaService) {
        this.recaptchaService = recaptchaService;
    }

    @Before(value = "@annotation(ValidateRecaptcha)")
    public void validateRecaptcha() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String recaptcha = request.getHeader("recaptcha");
        RecaptchaResponse recaptchaResponse = recaptchaService.validateToken(recaptcha);
        if (!recaptchaResponse.isSuccess()) {
            throw new RecaptchaValidationFailedException(recaptchaResponse.getErrorCodes()
                    .stream()
                    .map(String::toString)
                    .toList());
        }
    }
}
