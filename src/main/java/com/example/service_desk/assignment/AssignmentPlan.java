package com.example.service_desk.assignment;

import java.util.List;

public record AssignmentPlan(long specialistId, List<Long> ticketIds) {
    public AssignmentPlan {
        if (specialistId <= 0)
            throw new IllegalArgumentException("specialistId must be greater than zero");
        if (ticketIds == null)
            throw new IllegalArgumentException("ticketIds must not be null");
        if (ticketIds.isEmpty())
            throw new IllegalArgumentException("ticketIds must not be empty");
        for (Long ticketId : ticketIds) {
            if (ticketId <= 0)
                throw new IllegalArgumentException("ticketId must be greater than zero");
        }
        ticketIds = List.copyOf(ticketIds);
    }
    public long ticketCount(){
        return ticketIds.size();
    }
}
