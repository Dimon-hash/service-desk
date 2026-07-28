package com.example.service_desk.assignment;

import com.example.service_desk.specialist.Specialist;
import com.example.service_desk.specialist.SpecialistLevel;
import com.example.service_desk.specialist.SpecialistService;
import com.example.service_desk.ticket.Ticket;
import com.example.service_desk.ticket.TicketPriority;
import com.example.service_desk.ticket.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
        mockMvc.perform(patch("/api/tickets/{ticketId}/automatic-assignment", ticket.getId()))
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
        mockMvc.perform(patch("/api/tickets/{ticketId}/automatic-assignment", ticket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedSpecialistId").isEmpty())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }
}
