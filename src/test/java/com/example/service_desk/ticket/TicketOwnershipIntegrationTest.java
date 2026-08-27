package com.example.service_desk.ticket;

import com.example.service_desk.account.AccountService;
import com.example.service_desk.account.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TicketOwnershipIntegrationTest {

    private final AccountService accountService;
    private final MockMvc mockMvc;

    @Autowired
    TicketOwnershipIntegrationTest(
            AccountService accountService,
            MockMvc mockMvc
    ) {
        this.accountService = accountService;
        this.mockMvc = mockMvc;
    }

    @Test
    void authenticatedStudentShouldCreateTicketForOwnAccount() throws Exception {
        UserAccount account = accountService.register(
                "dima2",
                "strong-password-123");

        String requestJson = """
                {
                  "description": "Не работает компьютер",
                  "location": "Аудитория 301",
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/tickets")
                        .with(user("dima2").roles("STUDENT"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(account.getId()));
    }

    @Test
    void adminShouldNotCreateTicket() throws Exception {
        String requestJson = """
                {
                  "description": "Не работает компьютер",
                  "location": "Аудитория 301",
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/tickets")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden());
    }
}
