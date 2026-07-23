package com.example.service_desk.ticket;


import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TicketServiceTest {
    @Test
    void newTicketShouldHaveCreatedStatusAndCreationTime() {
        TicketRepository repository = new InMemoryTicketRepository();
        TicketService ticketService = new TicketService(repository);
        Ticket ticket = ticketService.createTicket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);

        assertEquals(1L, ticket.getId());
        assertEquals(TicketStatus.CREATED, ticket.getStatus());
        Ticket foundTicket = repository
                .findById(ticket.getId())
                .orElseThrow();
        assertSame(foundTicket, ticket);
    }

    @Test
    void getTicketShouldReturnExistingTicket() {
        TicketRepository repository = new InMemoryTicketRepository();
        TicketService ticketService = new TicketService(repository);
        Ticket ticket1 = ticketService.createTicket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        Ticket ticket2 = ticketService.getTicket(ticket1.getId());
        assertSame(ticket1, ticket2);

    }

    @Test
    void getTicketShouldThrowWhenTicketDoesNotExist() {
        TicketRepository repository = new InMemoryTicketRepository();
        TicketService ticketService = new TicketService(repository);

        TicketNotFoundException exception = assertThrows(TicketNotFoundException.class,
                () -> ticketService.getTicket(99L));

        assertTrue(exception.getMessage().contains("99"));

    }

    @Test
    void getAllTicketsShouldReturnAllTickets() {
        TicketRepository repository = new InMemoryTicketRepository();
        TicketService ticketService = new TicketService(repository);
        Ticket ticket1 = ticketService.createTicket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        Ticket ticket2 = ticketService.createTicket(100L,
                "Не работает компьютер",
                "Аудитория 302",
                TicketPriority.MEDIUM);
        List<Ticket> tickets = ticketService.getAllTickets();
        assertEquals(2, tickets.size());
        assertTrue(tickets.contains(ticket1));
        assertTrue(tickets.contains(ticket2));

    }

    @Test
    void assignTicketShouldAssignSpecialistAndChangeStatus() {
        TicketRepository repository = new InMemoryTicketRepository();
        TicketService ticketService = new TicketService(repository);
        Ticket ticket = ticketService.createTicket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        ticketService.assignTicket(ticket.getId(),50L);
        Ticket ticket1 = ticketService.getTicket(ticket.getId());
        assertEquals(50L, ticket1.getAssignedSpecialistId());
        assertEquals(TicketStatus.ASSIGNED, ticket1.getStatus());

    }

    @Test
    void startWorkShouldChangeAssignedTicketStatus(){
        TicketRepository repository = new InMemoryTicketRepository();
        TicketService ticketService = new TicketService(repository);
        Ticket ticket = ticketService.createTicket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        ticketService.assignTicket(ticket.getId(),50L);
        Ticket startedTicket = ticketService.startWork(ticket.getId());
        Ticket ticket1 = ticketService.getTicket(ticket.getId());
        assertEquals(TicketStatus.IN_PROGRESS, ticket1.getStatus());
        assertEquals(TicketStatus.IN_PROGRESS, startedTicket.getStatus());

    }

    @Test
    void blockTicketShouldSaveReasonAndBlockedStatus(){
        TicketRepository repository = new InMemoryTicketRepository();
        TicketService ticketService = new TicketService(repository);
        Ticket ticket = ticketService.createTicket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);

        String reason = "Не смог сделать нету отвертки";
        ticketService.assignTicket(ticket.getId(),50L);
        ticketService.startWork(ticket.getId());
        Ticket blockedTicket = ticketService.block(ticket.getId(),reason);
        Ticket ticket1 = ticketService.getTicket(ticket.getId());

        assertEquals(TicketStatus.BLOCKED, ticket1.getStatus());
        assertEquals(TicketStatus.BLOCKED, blockedTicket.getStatus());
        assertEquals(reason,ticket1.getFailureReason());
        assertEquals(50L,ticket1.getAssignedSpecialistId());


    }

    @Test
    void completeShouldChangeInProgressStatus() {
        TicketRepository repository = new InMemoryTicketRepository();
        TicketService ticketService = new TicketService(repository);
        Ticket ticket = ticketService.createTicket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        ticketService.assignTicket(ticket.getId(), 50L);
        ticketService.startWork(ticket.getId());
        Ticket completedTicket = ticketService.complete(ticket.getId());
        Ticket ticket1 = ticketService.getTicket(ticket.getId());

        assertEquals(TicketStatus.COMPLETED, ticket1.getStatus());
        assertEquals(TicketStatus.COMPLETED, completedTicket.getStatus());

    }
}
