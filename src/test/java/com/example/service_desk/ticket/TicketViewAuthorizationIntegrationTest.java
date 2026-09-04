package com.example.service_desk.ticket;

import com.example.service_desk.account.AccountService;
import com.example.service_desk.account.UserAccount;
import com.example.service_desk.account.UserAccountRepository;
import com.example.service_desk.account.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TicketViewAuthorizationIntegrationTest {

    private static final String PASSWORD = "strong-password-123";

    private final MockMvc mockMvc;
    private final TicketService ticketService;
    private final AccountService accountService;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    TicketViewAuthorizationIntegrationTest(
            MockMvc mockMvc,
            TicketService ticketService,
            AccountService accountService,
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.mockMvc = mockMvc;
        this.ticketService = ticketService;
        this.accountService = accountService;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    void unauthenticatedUserShouldNotViewTicket() throws Exception {

        UserAccount owner = createAccount("owner", UserRole.STUDENT);
        Ticket ticket = createTicketFor(owner);

        mockMvc.perform(get("/api/tickets/{ticketId}", ticket.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void studentShouldViewOwnTicket() throws Exception {
        UserAccount owner = createAccount("owner", UserRole.STUDENT);
        Ticket ticket = createTicketFor(owner);

        mockMvc.perform(get("/api/tickets/{ticketId}", ticket.getId())
                        .with(user(owner.getLogin()).roles(UserRole.STUDENT.name())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.studentId").value(owner.getId()));
    }

    @Test
    void studentShouldNotViewAnotherStudentsTicket() throws Exception {
        UserAccount owner = createAccount("owner", UserRole.STUDENT);
        UserAccount anotherStudent = createAccount("another-student", UserRole.STUDENT);
        Ticket ticket = createTicketFor(owner);

        mockMvc.perform(get("/api/tickets/{ticketId}", ticket.getId())
                        .with(user(anotherStudent.getLogin()).roles(UserRole.STUDENT.name())))
                .andExpect(status().isForbidden());
    }

    @Test
    void specialistShouldViewAnotherStudentsTicket() throws Exception {
        UserAccount owner = createAccount("owner", UserRole.STUDENT);
        UserAccount specialist = createAccount("specialist", UserRole.SPECIALIST);
        Ticket ticket = createTicketFor(owner);

        mockMvc.perform(get("/api/tickets/{ticketId}", ticket.getId())
                        .with(user(specialist.getLogin()).roles(UserRole.SPECIALIST.name())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticket.getId()));
    }

    @Test
    void adminShouldViewAnyTicket() throws Exception {
        UserAccount owner = createAccount("owner", UserRole.STUDENT);
        UserAccount admin = createAccount("admin", UserRole.ADMIN);
        Ticket ticket = createTicketFor(owner);

        mockMvc.perform(get("/api/tickets/{ticketId}", ticket.getId())
                        .with(user(admin.getLogin()).roles(UserRole.ADMIN.name())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticket.getId()));
    }

    @Test
    void authenticatedUserShouldReceive404ForMissingTicket() throws Exception {
        UserAccount student = createAccount("missing-ticket-viewer", UserRole.STUDENT);

        mockMvc.perform(get("/api/tickets/{ticketId}", Long.MAX_VALUE)
                        .with(user(student.getLogin()).roles(UserRole.STUDENT.name())))
                .andExpect(status().isNotFound());
    }

    private UserAccount createAccount(String login, UserRole role) {
        if (role == UserRole.STUDENT) {
            return accountService.register(login, PASSWORD);
        }

        UserAccount account = new UserAccount(
                login,
                passwordEncoder.encode(PASSWORD),
                role
        );
        return userAccountRepository.save(account);
    }

    private Ticket createTicketFor(UserAccount owner) {
        return ticketService.createTicket(
                owner.getId(),
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
    }
}
