package com.example.service_desk.specialist.dto;

import com.example.service_desk.specialist.SpecialistLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterSpecialistRequest(
        @NotBlank @Size(max = 255) String fullName,
        @NotBlank @Size(max = 255) String location,
        @NotNull SpecialistLevel level
) {
}
