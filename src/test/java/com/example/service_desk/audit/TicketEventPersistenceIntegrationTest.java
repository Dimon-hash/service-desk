package com.example.service_desk.audit;

import com.example.service_desk.ticket.Ticket;
import com.example.service_desk.ticket.TicketPriority;
import com.example.service_desk.ticket.TicketService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class TicketEventPersistenceIntegrationTest {

    private final TicketEventRepository ticketEventRepository;
    private final TicketService ticketService;
    private final EntityManager entityManager;

    @Autowired
    public TicketEventPersistenceIntegrationTest(TicketEventRepository ticketEventRepository,
                                                 TicketService ticketService, EntityManager entityManager) {
        this.ticketEventRepository = ticketEventRepository;
        this.ticketService = ticketService;
        this.entityManager = entityManager;

    }


    @Test
    void eventsShouldBeStoredAndReturnedInChronologicalOrder() {
        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        TicketEvent createdEvent1 = new TicketEvent(
                ticket.getId(),
                TicketEventType.CREATED,
                AuditActorType.SYSTEM,
                null,
                "Ticket created"
        );
        TicketEvent createdEvent2 = new TicketEvent(
                ticket.getId(),
                TicketEventType.ASSIGNED,
                AuditActorType.SYSTEM,
                null,
                "Specialist assigned"
        );
        ticketEventRepository.save(createdEvent1);
        ticketEventRepository.save(createdEvent2);
        entityManager.flush();
        entityManager.clear();

        List<TicketEvent> ticketEventList = ticketEventRepository.findByTicketIdOrderByOccurredAtAsc(ticket.getId());
        assertEquals(3, ticketEventList.size());
        assertEquals(TicketEventType.CREATED, ticketEventList.get(1).getEventType());
        assertEquals(TicketEventType.ASSIGNED, ticketEventList.getLast().getEventType());
        assertNotNull(ticketEventList.get(1).getId());
        assertNotNull(ticketEventList.getLast().getId());
    }

    @Test
    void creatingTicketShouldAutomaticallyStoreCreatedEvent(){
        Ticket ticket = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        entityManager.flush();
        entityManager.clear();
        List<TicketEvent> events = ticketEventRepository
                .findByTicketIdOrderByOccurredAtAsc(ticket.getId());
        assertEquals(1, events.size());
        assertEquals(TicketEventType.CREATED, events.getFirst().getEventType());
        assertEquals(AuditActorType.STUDENT, events.getLast().getActorType());
        assertEquals(100L, events.getLast().getActorId());

    }
}
