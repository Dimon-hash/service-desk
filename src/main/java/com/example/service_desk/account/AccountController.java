package com.example.service_desk.account;

import com.example.service_desk.account.dto.AccountResponse;
import com.example.service_desk.account.dto.RegisterAccountRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(
            @Valid @RequestBody RegisterAccountRequest registerAccountRequest
    ) {
        UserAccount userAccount = accountService.register(
                registerAccountRequest.login(),
                registerAccountRequest.password()
        );
        return AccountResponse.from(userAccount);
    }
}
