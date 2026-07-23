package com.example.service_desk.ticket.dto;

import jakarta.validation.constraints.Positive;

public record AssignTicketRequest(@Positive long specialistId) {
}
