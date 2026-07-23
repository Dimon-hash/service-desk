package com.example.service_desk.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTicketRepository
        extends JpaRepository<Ticket, Long> {
}