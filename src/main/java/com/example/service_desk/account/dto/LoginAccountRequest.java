package com.example.service_desk.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginAccountRequest(
        @NotBlank @Size(max = 30)
        String login,
        @NotBlank @Size(max = 64)
        String password
) {
}
