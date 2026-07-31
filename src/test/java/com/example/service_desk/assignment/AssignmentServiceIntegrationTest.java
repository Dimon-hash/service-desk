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
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@SpringBootTest
@Transactional
public class AssignmentServiceIntegrationTest {
    private final AssignmentService assignmentService;
    private final TicketService ticketService;
    private final SpecialistService specialistService;

    @Autowired
    public AssignmentServiceIntegrationTest(AssignmentService assignmentService, TicketService ticketService, SpecialistService specialistService) {
        this.assignmentService = assignmentService;
        this.ticketService = ticketService;
        this.specialistService = specialistService;
    }

    @Test
    void shouldAutomaticallyAssignLocalAvailableSpecialist() {
        Ticket ticket = ticketService.createTicket(100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH);

        Specialist specialist1 = specialistService.registerSpecialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        Specialist specialist2 = specialistService.registerSpecialist(
                "Антон",
                "Корпус 3",
                SpecialistLevel.SENIOR
        );
        specialistService.startShift(specialist1.getId());
        specialistService.startShift(specialist2.getId());

        Ticket assignedTicket =
                assignmentService.assignAutomatically(ticket.getId());


        assertEquals(TicketStatus.ASSIGNED, assignedTicket.getStatus());
        assertEquals(specialist1.getId(), assignedTicket.getAssignedSpecialistId());

        Specialist savedSpecialist =
                specialistService.getSpecialist(specialist1.getId());

        assertEquals(SpecialistStatus.BUSY, savedSpecialist.getStatus());
    }

    @Test
    void shouldKeepTicketCreatedWhenNoSpecialistIsAvailable(){
        Ticket ticket = ticketService.createTicket(100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH);

        Specialist specialist1 = specialistService.registerSpecialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        Ticket result =
                assignmentService.assignAutomatically(ticket.getId());

        assertEquals(TicketStatus.CREATED, result.getStatus());
        assertNull(result.getAssignedSpecialistId());
        assertEquals(SpecialistStatus.OFF_DUTY, specialist1.getStatus());
    }

    @Test
    void completingAssignedTicketShouldCompleteTicketAndReleaseSpecialist(){
        Ticket ticket = ticketService.createTicket(100L,
                "Не работает проектор",
                "Корпус 1",
                TicketPriority.HIGH);

        Specialist specialist1 = specialistService.registerSpecialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        specialistService.startShift(specialist1.getId());
        assignmentService.assignAutomatically(ticket.getId());
        ticketService.startWork(ticket.getId());
        Ticket completed = assignmentService.completeAssignedTicket(ticket.getId());
        assertEquals(TicketStatus.COMPLETED, completed.getStatus());
        assertEquals(specialist1.getId(), completed.getAssignedSpecialistId());
        Specialist savedSpecialist = specialistService.getSpecialist(specialist1.getId());

        assertEquals(SpecialistStatus.AVAILABLE, savedSpecialist.getStatus());

    }

}
