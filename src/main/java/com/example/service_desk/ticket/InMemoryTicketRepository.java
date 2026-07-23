package com.example.service_desk.ticket;


import java.util.*;


public class InMemoryTicketRepository implements TicketRepository {

    private final Map<Long,Ticket> tickets = new HashMap<>();
    private long nextId = 1L;

    @Override
    public Ticket save(Ticket ticket) {
        if(ticket.getId() == null) {
            ticket.assignId(nextId++);
        }
        tickets.put(ticket.getId(), ticket);
        return ticket;
    }

    @Override
    public Optional<Ticket> findById(long ticketId) {
        return Optional.ofNullable(tickets.get(ticketId));
    }

    @Override
    public List<Ticket> findAll() {
        return new ArrayList<>( tickets.values());
    }

}
