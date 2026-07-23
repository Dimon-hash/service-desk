package com.example.service_desk.ticket;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidTicketStateException extends IllegalStateException {
    public InvalidTicketStateException(String message) {
        super(message);
    }
}
