package com.example.service_desk.account;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DatabaseAuthenticationIntegrationTest {
    private final AccountService accountService;
    private final MockMvc mockMvc;

    @Autowired
    public DatabaseAuthenticationIntegrationTest(AccountService accountService, MockMvc mockMvc) {
        this.accountService = accountService;
        this.mockMvc = mockMvc;
    }

    @Test
    void registeredUserShouldAuthenticateWithCorrectPassword() throws Exception {
        String login = "dima2";
        String rawPassword = "strong-password-123";

        accountService.register(login, rawPassword);

        mockMvc.perform(formLogin()
                        .user(login)
                        .password(rawPassword))
                .andExpect(authenticated().withUsername(login));

    }

    @Test
    void registeredUserShouldNotAuthenticateWithWrongPassword() throws Exception {
        String login = "dima2";
        String rawPassword = "strong-password-123";

        accountService.register(login, rawPassword);
        mockMvc.perform(formLogin()
                        .user(login)
                        .password(rawPassword + "!"))
                .andExpect(unauthenticated());

    }
}
