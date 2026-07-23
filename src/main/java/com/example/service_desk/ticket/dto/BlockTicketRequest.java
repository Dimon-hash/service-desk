package com.example.service_desk.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlockTicketRequest(@NotBlank @Size(max = 1000) String reason) {
}
