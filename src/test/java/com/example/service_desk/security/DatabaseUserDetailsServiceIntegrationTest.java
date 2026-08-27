package com.example.service_desk.security;

import com.example.service_desk.account.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class DatabaseUserDetailsServiceIntegrationTest {

    private final AccountService accountService;
    private final DatabaseUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    DatabaseUserDetailsServiceIntegrationTest(
            AccountService accountService,
            DatabaseUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        this.accountService = accountService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    void registeredUserShouldBeLoadedAsUserDetails() {
        String login = "dima2";
        String rawPassword = "strong-password-123";

        accountService.register(login, rawPassword);

        UserDetails userDetails = userDetailsService.loadUserByUsername(login);

        assertEquals(login, userDetails.getUsername());
        assertTrue(passwordEncoder.matches(rawPassword, userDetails.getPassword()));
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        Objects.equals(authority.getAuthority(), "ROLE_STUDENT")));
    }

    @Test
    void missingUserShouldThrowUsernameNotFoundException() {
        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing-user")
        );
    }
}
