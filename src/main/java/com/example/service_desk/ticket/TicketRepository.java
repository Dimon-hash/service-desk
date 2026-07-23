package com.example.service_desk.ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {
    Ticket save(Ticket ticket);

    Optional<Ticket> findById(long ticketId);

    List<Ticket> findAll();
}
