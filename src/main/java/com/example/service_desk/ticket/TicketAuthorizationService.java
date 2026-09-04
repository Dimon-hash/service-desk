package com.example.service_desk.ticket;

import com.example.service_desk.account.AccountService;
import com.example.service_desk.account.UserAccount;

import com.example.service_desk.account.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TicketAuthorizationService {

    private final AccountService accountService;

    public TicketAuthorizationService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void checkCanView(String login, Ticket ticket) {
        UserAccount userAccount = accountService.getByLogin(login);
        UserRole role = userAccount.getRole();
        if (role == UserRole.ADMIN) {
            return;
        }
        if (role == UserRole.SPECIALIST) {
            return;
        }
        if (role == UserRole.STUDENT && Objects.equals(userAccount.getId(), ticket.getStudentId())) {
            return;
        }
        throw new AccessDeniedException("You cannot view this ticket");
    }
}
