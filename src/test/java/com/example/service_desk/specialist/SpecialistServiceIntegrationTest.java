package com.example.service_desk.specialist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SpecialistServiceIntegrationTest {

    private final SpecialistService specialistService;

    @Autowired
    public SpecialistServiceIntegrationTest(
            SpecialistService specialistService
    ) {
        this.specialistService = specialistService;
    }

    @Test
    void registerSpecialistShouldSaveNewOffDutySpecialist() {
        Specialist specialist = specialistService.registerSpecialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );
        assertNotNull(specialist.getId());
        assertEquals("Petrov", specialist.getFullName());
        assertEquals("House 12", specialist.getLocation());
        assertEquals(SpecialistLevel.JUNIOR, specialist.getLevel());
        assertEquals(SpecialistStatus.OFF_DUTY, specialist.getStatus());
        assertNull(specialist.getRating());

    }

    @Test
    void offDutySpecialistShouldStartShiftThroughService() {
        Specialist specialist = specialistService.registerSpecialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );
        assertEquals(SpecialistStatus.OFF_DUTY, specialist.getStatus());
        Specialist updated = specialistService.startShift(specialist.getId());

        assertEquals(SpecialistStatus.AVAILABLE, updated.getStatus());
        assertEquals(specialist.getId(), updated.getId());
    }

    @Test
    void startingShiftForMissingSpecialistShouldThrowNotFoundException() {
        assertThrows(SpecialistNotFoundException.class,
                () -> specialistService.startShift(Long.MAX_VALUE));

    }

    @Test
    void availableSpecialistShouldFinishShiftThroughService() {
        Specialist specialist = specialistService.registerSpecialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );
        assertEquals(SpecialistStatus.OFF_DUTY, specialist.getStatus());
        specialistService.startShift(specialist.getId());
        assertEquals(SpecialistStatus.AVAILABLE, specialist.getStatus());
        Specialist updated = specialistService.finishShift(specialist.getId());
        assertEquals(SpecialistStatus.OFF_DUTY, updated.getStatus());

    }
}
