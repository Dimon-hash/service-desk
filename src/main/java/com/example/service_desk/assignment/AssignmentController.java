package com.example.service_desk.assignment;


import com.example.service_desk.ticket.Ticket;
import com.example.service_desk.ticket.dto.TicketResponse;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class AssignmentController {
    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PatchMapping("/{ticketId}/automatic-assignment")
    public TicketResponse assignTicketAutomatically(@PathVariable long ticketId){
        return  TicketResponse.from(assignmentService.assignAutomatically(ticketId));

    }

    @PatchMapping("/{ticketId}/complete")
    public TicketResponse complete(@PathVariable long ticketId) {
        Ticket ticket = assignmentService.completeAssignedTicket(ticketId);
        return TicketResponse.from(ticket);
    }


}
