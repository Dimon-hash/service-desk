package com.example.service_desk.specialist;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpecialistTest {
    @Test
    void newSpecialistShouldBeOffDutyAndHaveNoRating() {
        Specialist specialist = new Specialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );

        assertEquals(SpecialistStatus.OFF_DUTY, specialist.getStatus());
        assertNull(specialist.getRating());
    }

    @Test
    void offDutySpecialistShouldStartShift(){
        Specialist specialist = new Specialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );
        specialist.startShift();
        assertEquals(SpecialistStatus.AVAILABLE, specialist.getStatus());
    }

    @Test
    void availableSpecialistShouldNotStartShiftAgain(){
        Specialist specialist = new Specialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );
        specialist.startShift();
        assertThrows(InvalidSpecialistStateException.class, specialist::startShift);
    }

    @Test
    void availableSpecialistShouldBecomeBusyWhenWorkStarts(){
        Specialist specialist = new Specialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );
        specialist.startShift();
        specialist.startWork();
        assertEquals(SpecialistStatus.BUSY, specialist.getStatus());
    }

    @Test
    void busySpecialistShouldNotStartWorkAgain(){
        Specialist specialist = new Specialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );
        specialist.startShift();
        specialist.startWork();
        assertThrows(InvalidSpecialistStateException.class, specialist::startWork);
    }

    @Test
    void busySpecialistShouldBecomeAvailableWhenWorkFinishes(){
        Specialist specialist = new Specialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );
        specialist.startShift();
        specialist.startWork();
        specialist.finishWork();
        assertEquals(SpecialistStatus.AVAILABLE, specialist.getStatus());

    }

    @Test
    void availableSpecialistShouldNotFinishWorkAgain(){
        Specialist specialist = new Specialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );
        specialist.startShift();
        specialist.startWork();
        specialist.finishWork();
        assertThrows(InvalidSpecialistStateException.class, specialist::finishWork);
    }
    @Test
    void availableSpecialistShouldFinishShift(){
        Specialist specialist = new Specialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );
        specialist.startShift();
        specialist.finishShift();
        assertEquals(SpecialistStatus.OFF_DUTY, specialist.getStatus());

    }

    @Test
    void busySpecialistShouldNotFinishShift(){
        Specialist specialist = new Specialist(
                "Petrov",
                "House 12",
                SpecialistLevel.JUNIOR
        );
        specialist.startShift();
        specialist.startWork();
        assertThrows(InvalidSpecialistStateException.class, specialist::finishShift);
        assertEquals(SpecialistStatus.BUSY, specialist.getStatus());

    }

}
