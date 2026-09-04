package com.example.service_desk.ticket;

import com.example.service_desk.account.AccountService;
import com.example.service_desk.account.UserAccount;
import com.example.service_desk.ticket.dto.CreateTicketRequest;
import com.example.service_desk.ticket.dto.TicketResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;
    private final AccountService accountService;

    public TicketController(TicketService ticketService, AccountService accountService) {
        this.ticketService = ticketService;
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(
            @Valid @RequestBody CreateTicketRequest request, Authentication authentication
    ) {
        String login = authentication.getName();
        UserAccount userAccount = accountService.getByLogin(login);
        Ticket ticket = ticketService.createTicket(
                userAccount.getId(),
                request.description(),
                request.location(),
                request.priority()
        );
        return TicketResponse.from(ticket);
    }


    @GetMapping("/{ticketId}")
    public TicketResponse getTicket(@PathVariable long ticketId,
                                    Authentication authentication) {
        Ticket ticket = ticketService.getTicketForViewing(
                ticketId,
                authentication.getName()
        );
        return TicketResponse.from(ticket);
    }

    @GetMapping
    public List<TicketResponse> getTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return tickets.stream()
                .map(TicketResponse::from)
                .toList();
    }


    @PatchMapping("/{ticketId}/start")
    public TicketResponse startWork(@PathVariable long ticketId) {
        Ticket ticket = ticketService.startWork(ticketId);
        return TicketResponse.from(ticket);
    }

}
