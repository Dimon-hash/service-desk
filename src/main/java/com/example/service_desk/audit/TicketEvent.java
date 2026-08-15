package com.example.service_desk.audit;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "ticket_events")
public class TicketEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private long ticketId;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketEventType eventType;

    @Column(name = "actor_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditActorType actorType;

    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    public TicketEvent(long ticketId, TicketEventType eventType,
                       AuditActorType actorType, Long actorId, String details) {

        if (ticketId <= 0) {
            throw new IllegalArgumentException("ticketId must be greater than 0");
        }
        if (actorType == null) {
            throw new IllegalArgumentException("actorType must not be null");
        }
        if (actorType == AuditActorType.SYSTEM && actorId != null) {
            throw new IllegalArgumentException("actorId must be null for SYSTEM");
        }
        if (actorType != AuditActorType.SYSTEM && actorId == null) {
            throw new IllegalArgumentException("actorId must not be null");
        }
        if (actorId != null && actorId <= 0) {
            throw new IllegalArgumentException("actorId must be greater than 0");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("eventType must not be null");
        }

        this.ticketId = ticketId;
        this.eventType = eventType;
        this.actorType = actorType;
        this.actorId = actorId;
        this.details = details;
        this.occurredAt = Instant.now();
    }

    public TicketEvent() {
    }

    public Long getId() {
        return id;
    }

    public long getTicketId() {
        return ticketId;
    }

    public TicketEventType getEventType() {
        return eventType;
    }

    public AuditActorType getActorType() {
        return actorType;
    }

    public Long getActorId() {
        return actorId;
    }

    public String getDetails() {
        return details;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

}
