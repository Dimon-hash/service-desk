package com.example.service_desk.ticket;

import com.example.service_desk.ticket.dto.AssignTicketRequest;
import com.example.service_desk.ticket.dto.BlockTicketRequest;
import com.example.service_desk.ticket.dto.CreateTicketRequest;
import com.example.service_desk.ticket.dto.TicketResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(
            @Valid @RequestBody CreateTicketRequest request
    ) {
        Ticket ticket = ticketService.createTicket(request.studentId(),
                request.description(),
                request.location(),
                request.priority());
        return TicketResponse.from(ticket);
    }


    @GetMapping("/{ticketId}")
    public TicketResponse getTicket(@PathVariable long ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);
        return TicketResponse.from(ticket);
    }

    @GetMapping
    public List<TicketResponse> getTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return tickets.stream()
                .map(TicketResponse::from)
                .toList();
    }

    @PatchMapping("/{ticketId}/assignment")
    public TicketResponse assignTicket(@PathVariable long ticketId,
                                       @Valid @RequestBody AssignTicketRequest request) {
        Ticket ticket = ticketService.assignTicket(ticketId, request.specialistId());
        return TicketResponse.from(ticket);
    }

    @PatchMapping("/{ticketId}/start")
    public TicketResponse startWork(@PathVariable long ticketId) {
        Ticket ticket = ticketService.startWork(ticketId);
        return TicketResponse.from(ticket);
    }

    @PatchMapping("/{ticketId}/block")
    public TicketResponse block(@PathVariable long ticketId, @Valid @RequestBody BlockTicketRequest request) {
        Ticket ticket = ticketService.block(ticketId, request.reason());
        return TicketResponse.from(ticket);
    }

    @PatchMapping("/{ticketId}/complete")
    public TicketResponse complete(@PathVariable long ticketId) {
        Ticket ticket = ticketService.complete(ticketId);
        return TicketResponse.from(ticket);
    }

}
