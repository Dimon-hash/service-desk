package com.example.service_desk.ticket.dto;

import com.example.service_desk.ticket.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(@NotBlank @Size(max = 2000) String description,
                                  @NotBlank @Size(max = 255) String location,
                                  @NotNull TicketPriority priority) {
}
