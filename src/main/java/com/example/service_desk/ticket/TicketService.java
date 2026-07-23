package com.example.service_desk.ticket;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(long studentId, String description,
                               String location, TicketPriority priority) {
        Ticket ticket = new Ticket(
                studentId,
                description,
                location,
                priority
        );
        ticketRepository.save(ticket);
        return ticket;
    }

    public Ticket getTicket(long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found  Id:" + ticketId));

    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket assignTicket(long ticketId, long specialistId) {
        Ticket ticket = getTicket(ticketId);
        ticket.assignTo(specialistId);
        ticketRepository.save(ticket);
        return ticket;
    }

    public Ticket startWork(long ticketId) {
        Ticket ticket = getTicket(ticketId);
        ticket.startWork();
        ticketRepository.save(ticket);
        return ticket;
    }

    public Ticket block(long ticketId, String reason) {
        Ticket ticket = getTicket(ticketId);
        ticket.block(reason);
        ticketRepository.save(ticket);
        return ticket;
    }

    public Ticket complete(long ticketId) {
        Ticket ticket = getTicket(ticketId);
        ticket.complete();
        ticketRepository.save(ticket);
        return ticket;
    }


}
