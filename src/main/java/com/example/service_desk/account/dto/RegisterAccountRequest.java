package com.example.service_desk.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterAccountRequest(@NotBlank @Size(max = 30, min = 3)
                                     @Pattern(regexp = "^[A-Za-z0-9._]+$",
                                             message = "login may contain only Latin letters, digits, _ and .")
                                     String login,
                                     @NotBlank @Size(max = 64, min = 12) String password) {
}
