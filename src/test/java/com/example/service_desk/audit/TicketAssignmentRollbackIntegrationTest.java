package com.example.service_desk.audit;

import com.example.service_desk.assignment.AssignmentService;
import com.example.service_desk.specialist.*;
import com.example.service_desk.ticket.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TicketAssignmentRollbackIntegrationTest {

    private final TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final SpecialistService specialistService;
    private final AssignmentService assignmentService;
    private final SpecialistRepository specialistRepository;
    private final SpringDataTicketRepository springDataTicketRepository;
    private final SpringDataSpecialistRepository springDataSpecialistRepository;

    private Long createdTicketId;
    private Long createdSpecialistId;


    @MockitoBean
    private TicketEventRepository ticketEventRepository;

    @Autowired
    public TicketAssignmentRollbackIntegrationTest(TicketRepository ticketRepository,
                                                   TicketService ticketService,
                                                   SpecialistService specialistService,
                                                   AssignmentService assignmentService,
                                                   SpecialistRepository specialistRepository, SpringDataTicketRepository springDataTicketRepository, SpringDataSpecialistRepository springDataSpecialistRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketService = ticketService;
        this.specialistService = specialistService;
        this.assignmentService = assignmentService;
        this.specialistRepository = specialistRepository;
        this.springDataTicketRepository = springDataTicketRepository;
        this.springDataSpecialistRepository = springDataSpecialistRepository;
    }

    @Test
    void assignmentShouldRollbackWhenAuditSavingFails() {

        Ticket ticket = ticketService.createTicket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        Specialist specialist = specialistService.registerSpecialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );
        createdTicketId = ticket.getId();
        createdSpecialistId = specialist.getId();

        specialistService.startShift(specialist.getId());
        clearInvocations(ticketEventRepository);
        when(ticketEventRepository.save(any(TicketEvent.class)))
                .thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class,
                () -> assignmentService.assignManually(ticket.getId(), specialist.getId()));
        ArgumentCaptor<TicketEvent> eventCaptor = ArgumentCaptor.forClass(TicketEvent.class);

        verify(ticketEventRepository, times(1)).save(eventCaptor.capture());

        TicketEvent capturedEvent = eventCaptor.getValue();

        assertEquals(ticket.getId(), capturedEvent.getTicketId());
        assertEquals(TicketEventType.ASSIGNED, capturedEvent.getEventType());
        assertEquals(AuditActorType.SYSTEM, capturedEvent.getActorType());
        assertNull(capturedEvent.getActorId());

        Ticket savedTicket = ticketRepository
                .findById(createdTicketId)
                .orElseThrow();

        Specialist savedSpecialist = specialistRepository
                .findById(createdSpecialistId)
                .orElseThrow();

        assertEquals(TicketStatus.CREATED, savedTicket.getStatus());
        assertNull(savedTicket.getAssignedSpecialistId());
        assertEquals(SpecialistStatus.AVAILABLE, savedSpecialist.getStatus());

    }

    @AfterEach
    void cleanUp() {
        if (createdTicketId != null) {
            springDataTicketRepository.deleteById(createdTicketId);
        }

        if (createdSpecialistId != null) {
            springDataSpecialistRepository.deleteById(createdSpecialistId);
        }
    }
}
