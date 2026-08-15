package com.example.service_desk.audit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TicketEventTest {

    @Test
    void validSpecialistEventShouldExposeData(){
        TicketEvent ticketEvent = new TicketEvent(
                15,
                TicketEventType.ASSIGNED,
                AuditActorType.SPECIALIST,
                50L,
                 "Automatically assigned");
        assertNull(ticketEvent.getId());
        assertNotNull(ticketEvent.getOccurredAt());
    }
    @Test
    void nonPositiveTicketIdShouldBeRejected(){
        assertThrows(IllegalArgumentException.class, () -> new TicketEvent(
                0,
                TicketEventType.ASSIGNED,
                AuditActorType.SPECIALIST,
                50L,
                "Automatically assigned"));
    }
    @Test
    void actorIdShouldMatchActorType(){
        assertDoesNotThrow(() -> new TicketEvent(
                15,
                TicketEventType.CREATED,
                AuditActorType.SYSTEM,
                null,
                null
        ));


        assertThrows(IllegalArgumentException.class, () -> new TicketEvent(
                15,
                TicketEventType.ASSIGNED,
                AuditActorType.SPECIALIST,
                null,
                null
        ));


        assertThrows(IllegalArgumentException.class, () -> new TicketEvent(
                15,
                TicketEventType.CREATED,
                AuditActorType.SYSTEM,
                50L,
                null
        ));

    }

    @Test
    void nullEventTypeShouldBeRejected() {
        assertThrows(IllegalArgumentException.class, () -> new TicketEvent(
                15,
                null,
                AuditActorType.STUDENT,
                50L,
                "!223"
        ));

    }
}
