package com.example.service_desk.account;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void successfulLoginShouldAuthorizeFollowingRequestThroughSession() throws Exception {
        String login = "dima2";
        String rawPassword = "strong-password-123";

        accountService.register(login, rawPassword);
        MvcResult loginResult = mockMvc.perform(formLogin()
                        .user(login)
                        .password(rawPassword))
                .andExpect(authenticated().withUsername(login))
                .andReturn();

        MockHttpSession session =
                (MockHttpSession) loginResult.getRequest().getSession(false);

        assertNotNull(session);
        mockMvc.perform(get("/api/auth/me")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.role").value(UserRole.STUDENT.name()));
    }

    @Test
    void logoutShouldInvalidateSession() throws Exception {
        String login = "dima2";
        String rawPassword = "strong-password-123";

        accountService.register(login, rawPassword);

        MvcResult loginResult = mockMvc.perform(formLogin()
                        .user(login)
                        .password(rawPassword))
                .andExpect(authenticated().withUsername(login))
                .andReturn();

        MockHttpSession session =
                (MockHttpSession) loginResult.getRequest().getSession(false);

        assertNotNull(session);

        mockMvc.perform(post("/api/auth/logout")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(unauthenticated());

        assertTrue(session.isInvalid());
    }
}
