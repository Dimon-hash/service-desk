package com.example.service_desk.specialist;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidSpecialistStateException extends RuntimeException {
    public InvalidSpecialistStateException(String message) {
        super(message);
    }
}
