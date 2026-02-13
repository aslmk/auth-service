package com.aslmk.authenticationservice.controller;

import com.aslmk.authenticationservice.account.action.AccountActionResult;
import com.aslmk.authenticationservice.account.action.AccountActionStatusResponse;
import com.aslmk.authenticationservice.service.Impl.EmailConfirmationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class EmailConfirmationController {

    private final EmailConfirmationService service;

    public EmailConfirmationController(EmailConfirmationService service) {
        this.service = service;
    }

    @PostMapping("/email-confirmation")
    public ResponseEntity<AccountActionStatusResponse> confirmEmail(@RequestParam("token") String token) {
        AccountActionResult result = service.confirmEmail(token);
        AccountActionStatusResponse response = new AccountActionStatusResponse(result.type().name());
        return ResponseEntity.ok(response);
    }
}
