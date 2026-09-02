package com.example.service_desk.account;

import com.example.service_desk.account.dto.AccountResponse;
import com.example.service_desk.account.dto.LoginAccountRequest;
import com.example.service_desk.account.dto.RegisterAccountRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AccountController {
    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public AccountController(AccountService accountService,
                             AuthenticationManager authenticationManager,
                             SecurityContextRepository securityContextRepository) {
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
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

    @GetMapping("/me")
    public AccountResponse me(Authentication authentication) {
        String username = authentication.getName();
        UserAccount userAccount = accountService.getByLogin(username);
        return AccountResponse.from(userAccount);
    }

    @PostMapping("/login")
    public AccountResponse login(@Valid @RequestBody LoginAccountRequest request,
                                 HttpServletRequest httpRequest,
                                 HttpServletResponse httpResponse) {

        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                request.login(),
                request.password()
        );

        Authentication authenticate = authenticationManager.authenticate(token);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticate);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );


        String name = authenticate.getName();
        UserAccount userAccount = accountService.getByLogin(name);
        return AccountResponse.from(userAccount);
    }
}
