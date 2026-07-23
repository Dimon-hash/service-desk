package com.example.service_desk.ticket;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class TicketPersistenceIntegrationTest {

    private final TicketService ticketService;
    private final EntityManager entityManager;

    @Autowired
    TicketPersistenceIntegrationTest(TicketService ticketService, EntityManager entityManager) {
        this.ticketService = ticketService;
        this.entityManager = entityManager;
    }

    @Test
    void createdTicketShouldBeStoredAndFoundById() {
        Ticket created = ticketService.createTicket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );


        entityManager.flush();
        entityManager.clear();

        Ticket found = ticketService.getTicket(created.getId());
        assertEquals(created.getId(), found.getId());
        assertEquals(created.getDescription(), found.getDescription());
        assertEquals(created.getStatus(), found.getStatus());


    }
}