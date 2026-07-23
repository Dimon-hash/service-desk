package com.example.service_desk.ticket;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTicketRepositoryTest {
    @Test
    public void savingTicketShouldMakeItFindableById() {
        InMemoryTicketRepository ticketRepository = new InMemoryTicketRepository();
        Ticket ticket = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        ticketRepository.save(ticket);
        Optional<Ticket> found = ticketRepository.findById(ticket.getId());
        assertTrue(found.isPresent());
        Ticket foundTicket = found.orElseThrow();
        assertSame(ticket, foundTicket);

    }

    @Test
    public void findingMissingTicketShouldReturnEmptyOptional() {
        InMemoryTicketRepository ticketRepository = new InMemoryTicketRepository();
        Optional<Ticket> found = ticketRepository.findById(99L);
        assertTrue(found.isEmpty());
    }

    @Test
    public void findAllShouldReturnAllSavedTickets() {
        InMemoryTicketRepository ticketRepository = new InMemoryTicketRepository();
        Ticket ticket1 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        Ticket ticket2 = new Ticket(
                200L,
                "Не работает проектор",
                "Аудитория 302",
                TicketPriority.MEDIUM
        );

        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);
        List<Ticket> tickets = ticketRepository.findAll();
        assertEquals(2, tickets.size());
        assertSame(ticket1, tickets.getFirst());
        assertSame(ticket2, tickets.getLast());
    }

    @Test
    public void modifyingReturnedListShouldNotChangeRepository() {

        InMemoryTicketRepository ticketRepository = new InMemoryTicketRepository();

        Ticket ticket1 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );

        Ticket ticket2 = new Ticket(
                200L,
                "Не работает проектор",
                "Аудитория 302",
                TicketPriority.MEDIUM
        );

        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);

        List<Ticket> tickets = ticketRepository.findAll();
        tickets.remove(ticket1);
        assertEquals(2, ticketRepository.findAll().size());
    }

    @Test
    public void savingNewTicketsShouldAssignUniqueIds(){
        InMemoryTicketRepository ticketRepository = new InMemoryTicketRepository();
        Ticket ticket1 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );

        Ticket ticket2 = new Ticket(
                200L,
                "Не работает проектор",
                "Аудитория 302",
                TicketPriority.MEDIUM
        );
        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);
        assertNotNull(ticket1.getId());
        assertNotNull(ticket2.getId());
        assertEquals(1L, ticket1.getId());
        assertEquals(2L, ticket2.getId());
        assertNotEquals(ticket1.getId(), ticket2.getId());
    }
    @Test
    public void savingSameTicketTwiceShouldKeepItsId(){
        InMemoryTicketRepository ticketRepository = new InMemoryTicketRepository();
        Ticket ticket1 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );

        ticketRepository.save(ticket1);
        long id1 = ticket1.getId();
        ticketRepository.save(ticket1);
        long id2 = ticket1.getId();
        assertEquals(id1, id2);
        assertEquals(1, ticketRepository.findAll().size());
    }
}
