package com.example.service_desk.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AccountServiceIntegrationTest {

    private final AccountService accountService;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountServiceIntegrationTest(UserAccountRepository userAccountRepository,
                                         AccountService accountService,
                                         PasswordEncoder passwordEncoder
    ) {
        this.userAccountRepository = userAccountRepository;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;

    }

    @Test
    void registeredUserShouldStoreEncodedPassword() {

        String rawPassword = "strong-password-123";
        accountService.register("dima2", rawPassword);
        UserAccount userAccount = userAccountRepository.findByLogin("dima2")
                .orElseThrow();
        assertNotEquals(rawPassword, userAccount.getPasswordHash());
        assertTrue(passwordEncoder.matches(rawPassword, userAccount.getPasswordHash()));
        assertEquals(UserRole.STUDENT, userAccount.getRole());
    }

    @Test
    void duplicateLoginShouldBeRejected() {
        String rawPassword = "strong-password-123";
        accountService.register("dima2", rawPassword);
        assertThrows(
                DuplicateLoginException.class,
                () -> accountService.register("dima2", rawPassword + "123")
        );
        UserAccount userAccount = userAccountRepository.findByLogin("dima2")
                .orElseThrow();
        assertTrue(passwordEncoder.matches(rawPassword, userAccount.getPasswordHash()));
        assertFalse(passwordEncoder.matches(rawPassword + "123", userAccount.getPasswordHash()));
    }
}
