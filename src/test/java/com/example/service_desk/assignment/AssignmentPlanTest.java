package com.example.service_desk.assignment;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssignmentPlanTest {
    @Test
    void validPlanShouldExposeDataAndTicketCount() {

        long specialistId = 50L;
        List<Long> ticketIds = new ArrayList<>(List.of(1L, 2L));

        AssignmentPlan plan = new AssignmentPlan(specialistId, ticketIds);

        assertEquals(50L, plan.specialistId());
        assertEquals(List.of(1L, 2L), plan.ticketIds());
        assertEquals(2, plan.ticketCount());
    }

    @Test
    void nonPositiveSpecialistIdShouldBeRejected() {
        long specialistId = -1L;
        List<Long> ticketIds = new ArrayList<>(List.of(1L, 2L));

        assertThrows(IllegalArgumentException.class, () -> new AssignmentPlan(specialistId, ticketIds));


    }

    @Test
    void emptyTicketIdsShouldBeRejected() {
        long specialistId = 5L;
        List<Long> ticketIds = new ArrayList<>(List.of());

        assertThrows(IllegalArgumentException.class, () -> new AssignmentPlan(specialistId, ticketIds));

    }

    @Test
    void modifyingSourceListShouldNotChangePlan() {
        long specialistId = 5L;
        List<Long> ticketIds = new ArrayList<>(List.of(1L, 2L));

        AssignmentPlan plan = new AssignmentPlan(specialistId, ticketIds);
        ticketIds.add(5L);
        assertEquals(2, plan.ticketCount());

    }


}
