package com.example.service_desk.account;

import org.apache.coyote.AbstractProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccountControllerIntegrationTest {

    private final MockMvc mockMvc;
    private final UserAccountRepository userAccountRepository;
    private final AccountService accountService;

    @Autowired
    public AccountControllerIntegrationTest(MockMvc mockMvc,
                                            UserAccountRepository userAccountRepository,
                                            AccountService accountService) {
        this.mockMvc = mockMvc;
        this.userAccountRepository = userAccountRepository;
        this.accountService = accountService;
    }

    @Test
    void registrationShouldReturn201AndSafeAccountJson() throws Exception {
        String requestJson = """
                {
                  "login": "dima2",
                    "password": "strong-password-123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.login").value("dima2"))
                .andExpect(jsonPath("$.role").value(UserRole.STUDENT.name()))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void registrationWithInvalidLoginShouldReturn400AndNotSaveAccount() throws Exception {
        String requestJson = """
                {
                  "login": "bad login",
                    "password": "strong-password-123"
                }
                """;
        long size = userAccountRepository.count();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
        assertEquals(size, userAccountRepository.count());
    }

    @Test
    void duplicateLoginShouldReturn409AndNotCreateSecondAccount() throws Exception {
        String requestJson = """
                {
                  "login": "dima2",
                    "password": "strong-password-123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isCreated());
        long size = userAccountRepository.count();
        requestJson = """
                {
                  "login": "dima2",
                    "password": "strong-password-123!"
                }
                """;
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isConflict());
        assertEquals(size, userAccountRepository.count());


    }

    @Test
    void authenticatedUserShouldReturnOwnAccount() throws Exception {
        accountService.register("dima2", "strong-password-123");

        mockMvc.perform(get("/api/auth/me")
                        .with(user("dima2").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.login").value("dima2"))
                .andExpect(jsonPath("$.role").value(UserRole.STUDENT.name()))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void unauthenticatedUserShouldNotAccessOwnAccount() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void restLoginShouldCreateSessionAndAuthorizeFollowingRequest() throws Exception {
        accountService.register("dima2", "strong-password-123");
        String requestJson = """
                {
                  "login": "dima2",
                    "password": "strong-password-123"
                }
                """;
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        assertNotNull(session);
        mockMvc.perform(get("/api/auth/me")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("dima2"))
                .andExpect(jsonPath("$.role").value(UserRole.STUDENT.name()));

    }

    @Test
    void restLoginWithWrongPasswordShouldReturn401() throws Exception {
        accountService.register("dima2", "strong-password-123");
        String requestJson = """
                {
                  "login": "dima2",
                    "password": "strong-password-123!"
                }
                """;
       mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated());

    }
}
