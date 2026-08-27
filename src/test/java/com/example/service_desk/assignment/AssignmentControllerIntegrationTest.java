package com.example.service_desk.assignment;

import com.example.service_desk.specialist.Specialist;
import com.example.service_desk.specialist.SpecialistLevel;
import com.example.service_desk.specialist.SpecialistService;
import com.example.service_desk.specialist.SpecialistStatus;
import com.example.service_desk.ticket.Ticket;
import com.example.service_desk.ticket.TicketPriority;
import com.example.service_desk.ticket.TicketService;
import com.example.service_desk.ticket.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.example.service_desk.specialist.SpecialistStatus.AVAILABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AssignmentControllerIntegrationTest {

    private MockMvc mockMvc;
    private SpecialistService specialistService;
    private TicketService ticketService;

    @Autowired
    public AssignmentControllerIntegrationTest(MockMvc mockMvc, SpecialistService specialistService, TicketService ticketService) {
        this.mockMvc = mockMvc;
        this.specialistService = specialistService;
        this.ticketService = ticketService;
    }

    @Test
    void automaticAssignmentShouldReturnAssignedTicket() throws Exception {
        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH
        );
        Specialist specialist1 = specialistService.registerSpecialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        specialistService.startShift(specialist1.getId());
        mockMvc.perform(patch("/api/tickets/{ticketId}/automatic-assignment", ticket.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedSpecialistId").value(specialist1.getId()))
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andExpect(jsonPath("$.id").value(ticket.getId()));
    }

    @Test
    void automaticAssignmentWithoutAvailableSpecialistShouldReturnCreatedTicket() throws Exception {
        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH
        );
        mockMvc.perform(patch("/api/tickets/{ticketId}/automatic-assignment", ticket.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedSpecialistId").isEmpty())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }


    @Test
    void completingTicketShouldReleaseAssignedSpecialist() throws Exception {

        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH
        );


        Specialist specialist1 = specialistService.registerSpecialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        specialistService.startShift(specialist1.getId());
        mockMvc.perform(patch("/api/tickets/{ticketId}/automatic-assignment", ticket.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"));

        ticketService.startWork(ticket.getId());

        mockMvc.perform(patch("/api/tickets/{ticketId}/complete", ticket.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
        Specialist specialist2 = specialistService.getSpecialist(specialist1.getId());
        assertEquals(AVAILABLE, specialist2.getStatus());
    }

    @Test
    void createdTicketShouldReturn409AndKeepTicketCreated() throws Exception {
        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH
        );
        mockMvc.perform(patch("/api/tickets/{ticketId}/complete", ticket.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isConflict());
        Ticket ticket1 = ticketService.getTicket(ticket.getId());
        assertEquals(TicketStatus.CREATED, ticket1.getStatus());

    }

    @Test
    void blockingTicketShouldReleaseAssignedSpecialist() throws Exception {

        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH
        );

        Specialist specialist1 = specialistService.registerSpecialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.SENIOR
        );
        specialistService.startShift(specialist1.getId());
        mockMvc.perform(patch("/api/tickets/{ticketId}/automatic-assignment", ticket.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"));
        ticketService.startWork(ticket.getId());
        String requestJson = """
                {
                   "reason": "Нет отвертки"
                }
                """;
        mockMvc.perform(patch("/api/tickets/{ticketId}/block", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"))
                .andExpect(jsonPath("$.failureReason").value("Нет отвертки"));
        Specialist specialist = specialistService.getSpecialist(specialist1.getId());
        assertEquals(AVAILABLE, specialist.getStatus());
    }

    @Test
    void blockingTicketWithBlankReasonShouldReturn400AndKeepState() throws Exception {

        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH
        );

        Specialist specialist1 = specialistService.registerSpecialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.SENIOR
        );
        specialistService.startShift(specialist1.getId());
        mockMvc.perform(patch("/api/tickets/{ticketId}/automatic-assignment", ticket.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"));
        ticketService.startWork(ticket.getId());
        String requestJson = """
                {
                   "reason": "  "
                }
                """;
        mockMvc.perform(patch("/api/tickets/{ticketId}/block", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
        Specialist specialist = specialistService.getSpecialist(specialist1.getId());
        Ticket ticket2 = ticketService.getTicket(ticket.getId());
        assertEquals(SpecialistStatus.BUSY, specialist.getStatus());
        assertEquals(TicketStatus.IN_PROGRESS, ticket2.getStatus());
    }

    @Test
    void blockingCreatedTicketShouldReturn409AndKeepState() throws Exception {
        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH
        );

        Specialist specialist1 = specialistService.registerSpecialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.SENIOR
        );
        specialistService.startShift(specialist1.getId());
        String requestJson = """
                {
                   "reason": "Нет отвертки"
                }
                """;
        mockMvc.perform(patch("/api/tickets/{ticketId}/block", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isConflict());
        Ticket ticket2 = ticketService.getTicket(ticket.getId());
        assertEquals(TicketStatus.CREATED, ticket2.getStatus());
    }

    @Test
    void manualAssignmentShouldMarkSpecialistBusy() throws Exception {

        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH
        );

        Specialist specialist1 = specialistService.registerSpecialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.SENIOR
        );
        specialistService.startShift(specialist1.getId());
        long id = specialist1.getId();
        String requestJson = """
                {
                  "specialistId": %d
                }
                """.formatted(specialist1.getId());
        mockMvc.perform(patch("/api/tickets/{ticketId}/assignment", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andExpect(jsonPath("$.assignedSpecialistId").value(id));
        Ticket ticket1 = ticketService.getTicket(ticket.getId());
        assertEquals(TicketStatus.ASSIGNED, ticket1.getStatus());
        assertEquals(id, ticket1.getAssignedSpecialistId());
        Specialist savedSpecialist = specialistService.getSpecialist(specialist1.getId());
        assertEquals(SpecialistStatus.BUSY, savedSpecialist.getStatus());
    }
    @Test
    void manualAssignmentWithZeroSpecialistIdShouldReturn400() throws Exception {

        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH
        );
        String requestJson = """
                {
                  "specialistId": 0
                }
                """;
        mockMvc.perform(patch("/api/tickets/{ticketId}/assignment", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
        Ticket ticket1 = ticketService.getTicket(ticket.getId());
        assertEquals(TicketStatus.CREATED, ticket1.getStatus());
        assertNull(ticket1.getAssignedSpecialistId());

    }

    @Test
    void assignedTicketShouldRejectManualReassignment() throws Exception {
        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH
        );

        Specialist firstSpecialist = specialistService.registerSpecialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.SENIOR
        );

        Specialist secondSpecialist = specialistService.registerSpecialist(
                "Антон",
                "Корпус 1",
                SpecialistLevel.SENIOR
        );

        specialistService.startShift(firstSpecialist.getId());
        specialistService.startShift(secondSpecialist.getId());

        String firstAssignmentJson = """
            {
              "specialistId": %d
            }
            """.formatted(firstSpecialist.getId());

        mockMvc.perform(patch(
                        "/api/tickets/{ticketId}/assignment",
                        ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstAssignmentJson)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andExpect(jsonPath("$.assignedSpecialistId")
                        .value(firstSpecialist.getId()));

        String secondAssignmentJson = """
            {
              "specialistId": %d
            }
            """.formatted(secondSpecialist.getId());

        mockMvc.perform(patch(
                        "/api/tickets/{ticketId}/assignment",
                        ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondAssignmentJson)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isConflict());

        Ticket savedTicket = ticketService.getTicket(ticket.getId());

        Specialist savedFirstSpecialist = specialistService.getSpecialist(firstSpecialist.getId());

        Specialist savedSecondSpecialist = specialistService.getSpecialist(secondSpecialist.getId());

        assertEquals(
                firstSpecialist.getId(),
                savedTicket.getAssignedSpecialistId()
        );
        assertEquals(TicketStatus.ASSIGNED, savedTicket.getStatus());
        assertEquals(
                SpecialistStatus.BUSY,
                savedFirstSpecialist.getStatus()
        );
        assertEquals(
                SpecialistStatus.AVAILABLE,
                savedSecondSpecialist.getStatus()
        );
    }

    @Test
    void blockedTicketShouldBeManuallyReassignedWithoutReleasingSpecialistTwice() throws Exception {

        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH
        );

        Specialist specialist1 = specialistService.registerSpecialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.SENIOR
        );
        Specialist specialist2 = specialistService.registerSpecialist(
                "Антон",
                "Корпус 1",
                SpecialistLevel.SENIOR
        );
        specialistService.startShift(specialist1.getId());
        specialistService.startShift(specialist2.getId());
        long id1 = specialist1.getId();
        String requestJson = """
                {
                  "specialistId": %d
                }
                """.formatted(id1);
        mockMvc.perform(patch("/api/tickets/{ticketId}/assignment", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk());
        ticketService.startWork(ticket.getId());
        requestJson = """
                {
                   "reason": "Нет отвертки"
                }
                """;
        mockMvc.perform(patch("/api/tickets/{ticketId}/block", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk());
        long id2 = specialist2.getId();
        requestJson = """
                {
                  "specialistId": %d
                }
                """.formatted(id2);
        mockMvc.perform(patch("/api/tickets/{ticketId}/assignment", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk());
        Ticket ticket1 = ticketService.getTicket(ticket.getId());
        Specialist savedSpecialist1 = specialistService.getSpecialist(specialist1.getId());
        Specialist savedSpecialist2 = specialistService.getSpecialist(specialist2.getId());
        assertEquals(savedSpecialist2.getId(), ticket1.getAssignedSpecialistId());
        assertEquals(SpecialistStatus.BUSY, savedSpecialist2.getStatus());
        assertEquals(AVAILABLE, savedSpecialist1.getStatus());
        assertEquals("Нет отвертки", ticket1.getFailureReason());

    }
}
