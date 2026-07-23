package com.example.service_desk.ticket;

import java.time.Instant;


public class Ticket {
    private Long id;
    private long studentId;
    private String description;
    private String location;
    private TicketStatus status;
    private TicketPriority priority;
    private Instant createdAt;
    private Long assignedSpecialistId;
    private String failureReason;

    public Ticket(long studentId, String description, String location, TicketPriority priority) {
        this.studentId = studentId;
        this.description = description;
        this.location = location;
        this.status = TicketStatus.CREATED;
        this.priority = priority;
        this.createdAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Long getAssignedSpecialistId() {
        return assignedSpecialistId;
    }

    public Long getId() {
        return id;
    }

    public long getStudentId() {
        return studentId;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    void assignId(long id) {

        if (this.id != null) {
            throw new IllegalStateException("Ticket id is already set");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
        this.id = id;
    }

    public void assignTo(long assignedSpecialistId) {
        if (this.status != TicketStatus.ASSIGNED && this.status != TicketStatus.CREATED && this.status != TicketStatus.BLOCKED) {
            throw new InvalidTicketStateException("Ticket status is not assigned or blocked or created");
        }

        if (assignedSpecialistId <= 0) {
            throw new IllegalArgumentException("assignedSpecialistId must be greater than 0");
        }
        this.assignedSpecialistId = assignedSpecialistId;
        this.status = TicketStatus.ASSIGNED;

    }

    public void startWork() {
        if (this.status != TicketStatus.ASSIGNED) {
            throw new InvalidTicketStateException("Ticket status is not ASSIGNED");
        }
        this.status = TicketStatus.IN_PROGRESS;
    }

    public void block(String reason) {
        if (this.status != TicketStatus.IN_PROGRESS) {
            throw new InvalidTicketStateException("Ticket status is not IN_PROGRESS");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be empty");
        }
        this.status = TicketStatus.BLOCKED;
        this.failureReason = reason;
    }

    public void complete() {
        if (this.status != TicketStatus.IN_PROGRESS) {
            throw new InvalidTicketStateException("Ticket status is not IN_PROGRESS");
        }
        this.status = TicketStatus.COMPLETED;
    }

}
