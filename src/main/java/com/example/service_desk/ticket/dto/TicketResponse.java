package com.example.service_desk.ticket.dto;

import com.example.service_desk.ticket.Ticket;
import com.example.service_desk.ticket.TicketPriority;
import com.example.service_desk.ticket.TicketStatus;

import java.time.Instant;

public record TicketResponse(
        long id,
        long studentId,
        String description,
        String location,
        TicketStatus status,
        TicketPriority priority,
        Instant createdAt,
        Long assignedSpecialistId,
        String failureReason) {
    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getStudentId(),
                ticket.getDescription(),
                ticket.getLocation(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedAt(),
                ticket.getAssignedSpecialistId(),
                ticket.getFailureReason()
        );
    }
}
