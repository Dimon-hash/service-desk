package com.example.service_desk.assignment;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssignmentPlanStatisticsTest {
    @Test
    void findUniqueTicketIdsShouldFlattenDeduplicateAndSort() {

        AssignmentPlan plan1 = new AssignmentPlan(
                10L,
                List.of(3L, 1L)
        );
        AssignmentPlan plan2 = new AssignmentPlan(
                20L,
                List.of(2L, 3L)
        );

        List<AssignmentPlan> plans = List.of(plan1, plan2);
        List<Long> result = AssignmentPlanStatistics.findUniqueTicketIds(plans);

        assertEquals(List.of(1L, 2L, 3L), result);

    }

    @Test
    void totalTicketCountShouldCountTicketsAcrossPlans() {
        AssignmentPlan plan1 = new AssignmentPlan(
                10L,
                List.of(3L, 1L)
        );
        AssignmentPlan plan2 = new AssignmentPlan(
                20L,
                List.of(2L, 3L)
        );

        List<AssignmentPlan> plans = List.of(plan1, plan2);
        long result = AssignmentPlanStatistics.totalTicketCount(plans);

        assertEquals(4L, result);

    }
}
