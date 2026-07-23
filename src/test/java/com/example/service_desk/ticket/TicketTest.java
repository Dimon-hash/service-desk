package com.example.service_desk.ticket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketTest {
    @Test
    void newTicketShouldHaveCreatedStatusAndCreationTime() {
        Ticket ticket = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        assertEquals(TicketStatus.CREATED, ticket.getStatus());
        assertNotNull(ticket.getCreatedAt());

    }

    @Test
    void assigningSpecialistShouldSaveIdAndChangeStatus() {
        Ticket ticket = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        assertNull(ticket.getAssignedSpecialistId());
        ticket.assignTo(50);
        assertEquals(TicketStatus.ASSIGNED, ticket.getStatus());

        assertEquals(50L, ticket.getAssignedSpecialistId());

    }

    @Test
    void assigningNonPositiveSpecialistIdShouldFail() {

        Ticket ticket = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );

        assertThrows(IllegalArgumentException.class, () -> {
            ticket.assignTo(0L);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ticket.assignTo(-5L);
        });

        assertNull(ticket.getId());

        assertNull(ticket.getAssignedSpecialistId());

        assertEquals(TicketStatus.CREATED, ticket.getStatus());
    }

    @Test
    void assignedTicketShouldStartWork() {
        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        ticket.assignTo(50L);
        ticket.startWork();
        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());

    }

    @Test
    void createdTicketShouldNotStartWork() {
        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);


        assertThrows(InvalidTicketStateException.class,
                ticket::startWork);
        assertEquals(TicketStatus.CREATED, ticket.getStatus());

    }

    @Test
    void inProgressTicketShouldBeBlockedWithReason() {
        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        ticket.assignTo(50L);
        ticket.startWork();
        String reason = "Не смог сделать нету отвертки";
        ticket.block(reason);
        assertEquals(TicketStatus.BLOCKED, ticket.getStatus());
        assertEquals(50L, ticket.getAssignedSpecialistId());
        assertEquals(reason, ticket.getFailureReason());

    }

    @Test
    void blankReasonShouldNotBlockTicket() {
        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        ticket.assignTo(50L);
        ticket.startWork();
        String reason = "  ";
        assertThrows(IllegalArgumentException.class,
                () -> ticket.block(reason));
        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());
        assertNull(ticket.getFailureReason());


    }

    @Test
    void inProgressTicketShouldBeCompleted() {
        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        ticket.assignTo(50L);
        ticket.startWork();
        ticket.complete();
        assertEquals(TicketStatus.COMPLETED, ticket.getStatus());
        assertEquals(50L, ticket.getAssignedSpecialistId());

    }

    @Test
    void createdTicketShouldNotBeCompleted() {
        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        assertThrows(InvalidTicketStateException.class,
                ticket::complete);
        assertEquals(TicketStatus.CREATED, ticket.getStatus());

    }

    @Test
    void completedTicketShouldNotBeReassigned() {
        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        ticket.assignTo(50L);
        ticket.startWork();
        ticket.complete();

        assertThrows(InvalidTicketStateException.class,
                () -> ticket.assignTo(99L));
        assertEquals(TicketStatus.COMPLETED, ticket.getStatus());
        assertEquals(50L, ticket.getAssignedSpecialistId());

    }

    @Test
    void blockedTicketShouldBeReassigned() {
        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        ticket.assignTo(50L);
        assertEquals(50L, ticket.getAssignedSpecialistId());
        ticket.startWork();
        String reason = "Необходим другой специалист";
        ticket.block(reason);
        ticket.assignTo(99L);
        assertEquals(TicketStatus.ASSIGNED, ticket.getStatus());
        assertEquals(99L, ticket.getAssignedSpecialistId());
        assertEquals(reason, ticket.getFailureReason());

    }
}