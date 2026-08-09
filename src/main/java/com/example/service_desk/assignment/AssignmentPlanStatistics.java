package com.example.service_desk.assignment;

import java.util.List;

public class AssignmentPlanStatistics {
    public static List<Long> findUniqueTicketIds(List<AssignmentPlan> plans){
        return plans.stream()
                .flatMap(plan -> plan.ticketIds().stream())
                .distinct()
                .sorted()
                .toList();
    }
    public static long totalTicketCount(List<AssignmentPlan> plans){
        return plans.stream()
                .map(AssignmentPlan::ticketCount)
                .reduce(0L, Long::sum);
    }
}
