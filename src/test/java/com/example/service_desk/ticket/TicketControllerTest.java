package com.example.service_desk.ticket;

import com.example.service_desk.account.AccountService;
import com.example.service_desk.account.UserAccount;
import com.example.service_desk.audit.TicketEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class TicketControllerTest {

    private MockMvc mockMvc;
    private TicketService ticketService;
    private AccountService accountService;
    TicketEventRepository ticketEventRepository = mock(TicketEventRepository.class);

    @BeforeEach
    void setUp() {
        InMemoryTicketRepository ticketRepository = new InMemoryTicketRepository();
        ticketService = new TicketService(ticketRepository, ticketEventRepository);
        accountService = mock(AccountService.class);
        TicketController ticketController = new TicketController(ticketService, accountService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ticketController)
                .build();
    }

    @Test
    public void getTicketsShouldReturnEmptyJsonArray() throws Exception {

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

    }

    @Test
    public void getTicketShouldReturnTicketJson() throws Exception {

        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );

        mockMvc.perform(get("/api/tickets/{ticketId}", ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.description").value("Не работает проектор"))
                .andExpect(jsonPath("$.status").value("CREATED"));

    }

    @Test
    public void createTicketShouldReturn201AndSaveTicket() throws Exception {

        String requestJson = """
                {
                  "description": "Не работает проектор",
                  "location": "Аудитория 301",
                  "priority": "HIGH"
                }
                """;
        UserAccount account = mock(UserAccount.class);

        when(accountService.getByLogin("dima2"))
                .thenReturn(account);
        when(account.getId())
                .thenReturn(100L);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken("dima2", null);

        mockMvc.perform(post("/api/tickets")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.studentId").value(100))
                .andExpect(jsonPath("$.description").value("Не работает проектор"))
                .andExpect(jsonPath("$.location").value("Аудитория 301"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.createdAt").exists());


        assertEquals(1, ticketService.getAllTickets().size());


    }

    @Test
    public void createTicketWithBlankDescriptionShouldReturn400AndNotSaveTicket() throws Exception {

        String requestJson = """
                {
                  "studentId": 100,
                  "description": "  ",
                  "location": "Аудитория 301",
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        assertEquals(0, ticketService.getAllTickets().size());

    }

    @Test
    void getMissingTicketShouldReturn404() throws Exception {

        mockMvc.perform(get("/api/tickets/{ticketId}", 999))
                .andExpect(status().isNotFound());

    }

    @Test
    void startWorkShouldReturnInProgressTicket() throws Exception {

        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );

        ticketService.assignTicket(ticket.getId(), 50L);

        mockMvc.perform(patch("/api/tickets/{ticketId}/start",
                        ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.assignedSpecialistId").value(50));

        Ticket savedTicket = ticketService.getTicket(ticket.getId());
        assertEquals(50L, savedTicket.getAssignedSpecialistId());
        assertEquals(TicketStatus.IN_PROGRESS, savedTicket.getStatus());

    }

    @Test
    void startCreatedTicketShouldReturn409AndKeepStatusCreated() throws Exception {

        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );

        mockMvc.perform(patch("/api/tickets/{ticketId}/start",
                        ticket.getId()))
                .andExpect(status().isConflict());

        Ticket savedTicket = ticketService.getTicket(ticket.getId());
        assertNull(savedTicket.getAssignedSpecialistId());
        assertEquals(TicketStatus.CREATED, savedTicket.getStatus());


    }

}
