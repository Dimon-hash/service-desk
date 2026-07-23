package com.example.service_desk.ticket;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaTicketRepository implements TicketRepository {

    private final SpringDataTicketRepository springDataRepository;

    public JpaTicketRepository(
            SpringDataTicketRepository springDataRepository
    ) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        return springDataRepository.save(ticket);
    }

    @Override
    public Optional<Ticket> findById(long ticketId) {
        return springDataRepository.findById(ticketId);
    }

    @Override
    public List<Ticket> findAll() {
        return  springDataRepository.findAll();
    }

}