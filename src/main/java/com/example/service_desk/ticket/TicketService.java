package com.example.service_desk.ticket;

import com.example.service_desk.audit.AuditActorType;
import com.example.service_desk.audit.TicketEvent;
import com.example.service_desk.audit.TicketEventRepository;
import com.example.service_desk.audit.TicketEventType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TicketEventRepository ticketEventRepository;
    private final TicketAuthorizationService ticketAuthorizationService;


    public TicketService(TicketRepository ticketRepository,
                         TicketEventRepository ticketEventRepository,
                         TicketAuthorizationService ticketAuthorizationService) {
        this.ticketRepository = ticketRepository;
        this.ticketEventRepository = ticketEventRepository;
        this.ticketAuthorizationService = ticketAuthorizationService;
    }


    @Transactional
    public Ticket createTicket(long studentId, String description,
                               String location, TicketPriority priority) {
        Ticket ticket = new Ticket(
                studentId,
                description,
                location,
                priority
        );
        Ticket ticketSave = ticketRepository.save(ticket);

        TicketEvent ticketEvent = new TicketEvent(
                ticketSave.getId(),
                TicketEventType.CREATED,
                AuditActorType.STUDENT,
                studentId,
                "Ticket created"
        );

        ticketEventRepository.save(ticketEvent);
        return ticketSave;
    }

    public Ticket getTicket(long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found  Id:" + ticketId));

    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketForViewing(long ticketId, String login) {
        Ticket ticket = getTicket(ticketId);
        ticketAuthorizationService.checkCanView(login, ticket);
        return ticket;
    }

    @Transactional
    public Ticket assignTicket(long ticketId, long specialistId) {
        Ticket ticket = getTicket(ticketId);
        ticket.assignTo(specialistId);
        return ticket;
    }

    @Transactional
    public Ticket startWork(long ticketId) {
        Ticket ticket = getTicket(ticketId);
        ticket.startWork();
        return ticket;
    }

    @Transactional
    public Ticket block(long ticketId, String reason) {
        Ticket ticket = getTicket(ticketId);
        ticket.block(reason);
        return ticket;
    }

    @Transactional
    public Ticket complete(long ticketId) {
        Ticket ticket = getTicket(ticketId);
        ticket.complete();
        return ticket;
    }


}
