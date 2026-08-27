package com.example.service_desk.account;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserAccountPersistenceIntegrationTest {
    private final UserAccountRepository userAccountRepository;
    private final EntityManager entityManager;

    @Autowired
    public UserAccountPersistenceIntegrationTest(UserAccountRepository userAccountRepository,
                                                 EntityManager entityManager) {
        this.userAccountRepository = userAccountRepository;
        this.entityManager = entityManager;

    }

    @Test
    void savedUserShouldBeFoundByLogin() {
        UserAccount userAccount = new UserAccount(
                "dima",
                "encoded-password",
                UserRole.STUDENT
        );
        userAccountRepository.save(userAccount);
        entityManager.flush();
        entityManager.clear();
        UserAccount foundUser = userAccountRepository
                .findByLogin("dima")
                .orElseThrow();
        assertNotNull(foundUser.getId());
        assertEquals("dima", foundUser.getLogin());
        assertEquals("encoded-password", foundUser.getPasswordHash());
        assertEquals(UserRole.STUDENT, foundUser.getRole());
        assertTrue(foundUser.isEnabled());
        assertNotNull(foundUser.getCreatedAt());

    }

}
