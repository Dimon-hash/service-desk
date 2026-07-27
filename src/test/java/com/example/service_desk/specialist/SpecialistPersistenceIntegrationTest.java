package com.example.service_desk.specialist;


import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SpecialistPersistenceIntegrationTest {

    private final SpecialistRepository specialistRepository;
    private final EntityManager entityManager;

    @Autowired
    public SpecialistPersistenceIntegrationTest(SpecialistRepository specialistRepository, EntityManager entityManager) {
        this.specialistRepository = specialistRepository;
        this.entityManager = entityManager;
    }

    @Test
    void createdSpecialistShouldBeStoredAndFoundById(){
        Specialist specialist =specialistRepository.save(new Specialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        ));
        entityManager.flush();
        Long specialistId = specialist.getId();
        assertNotNull(specialistId);
        entityManager.clear();
        Specialist foundSpecialist = specialistRepository
                .findById(specialistId)
                .orElseThrow();
        assertEquals(specialist.getId(), foundSpecialist.getId());
        assertEquals(specialist.getFullName(), foundSpecialist.getFullName());
        assertEquals(specialist.getLocation(), foundSpecialist.getLocation());
        assertEquals(specialist.getLevel(), foundSpecialist.getLevel());
        assertEquals(SpecialistStatus.OFF_DUTY, foundSpecialist.getStatus());
        assertNull(foundSpecialist.getRating());
    }
}
