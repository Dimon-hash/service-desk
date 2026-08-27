package com.example.service_desk.account.dto;

import com.example.service_desk.account.UserAccount;
import com.example.service_desk.account.UserRole;

import java.time.Instant;

public record AccountResponse(Long id,
                              String login,
                              UserRole role,
                              boolean enabled,
                              Instant createdAt) {
    public static AccountResponse from(UserAccount account) {
        return new AccountResponse(
                account.getId(),
                account.getLogin(),
                account.getRole(),
                account.isEnabled(),
                account.getCreatedAt()
        );
    }
}
