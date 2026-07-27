package com.example.service_desk.specialist.dto;

import com.example.service_desk.specialist.Specialist;
import com.example.service_desk.specialist.SpecialistLevel;
import com.example.service_desk.specialist.SpecialistStatus;

import java.math.BigDecimal;

public record SpecialistResponse(
        long id,
        String fullName,
        String location,
        SpecialistLevel level,
        SpecialistStatus status,
        BigDecimal rating
) {
    public static SpecialistResponse from(Specialist specialist) {
        return new SpecialistResponse(
                specialist.getId(),
                specialist.getFullName(),
                specialist.getLocation(),
                specialist.getLevel(),
                specialist.getStatus(),
                specialist.getRating()
        );
    }
}
