package com.example.service_desk.assignment;

import com.example.service_desk.specialist.Specialist;
import com.example.service_desk.specialist.SpecialistLevel;
import com.example.service_desk.ticket.Ticket;
import com.example.service_desk.ticket.TicketPriority;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpecialistSelectorTest {
    @Test
    void shouldSelectAvailableSpecialistAtSameLocation() {

        String ticketLocation = new String("Корпус 1");

        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                ticketLocation,
                TicketPriority.HIGH);

        Specialist specialist1 = new Specialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        Specialist specialist2 = new Specialist(
                "Антон",
                "Корпус 3",
                SpecialistLevel.SENIOR
        );
        Specialist specialist3 = new Specialist(
                "Петр",
                "Корпус 1",
                SpecialistLevel.SENIOR
        );
        specialist1.startShift();
        specialist2.startShift();
        specialist3.startShift();
        specialist3.startWork();

        SpecialistSelector selector = new SpecialistSelector();

        Optional<Specialist> selected = selector.select(
                ticket,
                List.of(specialist1, specialist2, specialist3)
        );

        assertSame(specialist1, selected.orElseThrow());
    }

    @Test
    void shouldSelectHighestLevelAtSameLocation() {

        String ticketLocation = new String("Корпус 1");

        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                ticketLocation,
                TicketPriority.HIGH);

        Specialist specialist1 = new Specialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        Specialist specialist2 = new Specialist(
                "Антон",
                "Корпус 1",
                SpecialistLevel.SENIOR
        );
        specialist1.startShift();
        specialist2.startShift();

        SpecialistSelector selector = new SpecialistSelector();

        Optional<Specialist> selected = selector.select(
                ticket,
                List.of(specialist1, specialist2)
        );

        assertSame(specialist2, selected.orElseThrow());

    }

    @Test
    void shouldSelectHighestLevelAvailableSpecialistWhenNoOneAtSameLocation(){
        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                "Корпус 9",
                TicketPriority.HIGH);

        Specialist specialist1 = new Specialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        Specialist specialist2 = new Specialist(
                "Антон",
                "Корпус 3",
                SpecialistLevel.SENIOR
        );
        Specialist specialist3 = new Specialist(
                "Петр",
                "Корпус 9",
                SpecialistLevel.SENIOR
        );
        specialist1.startShift();
        specialist2.startShift();
        specialist3.startShift();
        specialist3.startWork();
        SpecialistSelector selector = new SpecialistSelector();
        Optional<Specialist> selected = selector.select(
                ticket,
                List.of(specialist1, specialist2, specialist3)
        );
        assertSame(specialist2, selected.orElseThrow());

    }

    @Test
    void shouldReturnEmptyWhenNoSpecialistIsAvailable(){

        String ticketLocation = new String("Корпус 1");

        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                ticketLocation,
                TicketPriority.HIGH);

        Specialist specialist1 = new Specialist(
                "Иван",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        Specialist specialist2 = new Specialist(
                "Антон",
                "Корпус 1",
                SpecialistLevel.SENIOR
        );

        specialist2.startShift();
        specialist2.startWork();

        SpecialistSelector selector = new SpecialistSelector();

        Optional<Specialist> selected = selector.select(
                ticket,
                List.of(specialist1, specialist2)
        );

        assertTrue(selected.isEmpty());


    }
}
