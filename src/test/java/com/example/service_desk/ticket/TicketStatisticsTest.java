package com.example.service_desk.ticket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.service_desk.ticket.TicketStatistics.filter;
import static com.example.service_desk.ticket.TicketStatistics.sortByPriorityThenStudentId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class TicketStatisticsTest {

    @Test
    void countByStatusShouldCountEachStatus() {
        List<TicketStatus> tickets = new ArrayList<TicketStatus>();
        tickets.add(TicketStatus.CREATED);
        tickets.add(TicketStatus.ASSIGNED);
        tickets.add(TicketStatus.CREATED);
        tickets.add(TicketStatus.COMPLETED);
        tickets.add(TicketStatus.ASSIGNED);
        tickets.add(TicketStatus.CREATED);
        Map<TicketStatus, Integer> ticketMap = TicketStatistics.countByStatus(tickets);
        assertEquals(3, ticketMap.get(TicketStatus.CREATED));
        assertEquals(2, ticketMap.get(TicketStatus.ASSIGNED));
        assertEquals(1, ticketMap.get(TicketStatus.COMPLETED));
    }

    @Test
    void countByStatusShouldReturnEmptyMapForEmptyList() {
        List<TicketStatus> tickets = new ArrayList<TicketStatus>();
        Map<TicketStatus, Integer> ticketMap = TicketStatistics.countByStatus(tickets);
        assertEquals(0, ticketMap.size());
    }


    @Test
    void findUniqueStudentIdsShouldPreserveFirstOccurrenceOrder() {

        List<Ticket> tickets = new ArrayList<>();
        Ticket ticket1 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        Ticket ticket2 = new Ticket(
                200L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        Ticket ticket3 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        Ticket ticket4 = new Ticket(
                300L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        Ticket ticket5 = new Ticket(
                200L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );


        tickets.add(ticket1);
        tickets.add(ticket2);
        tickets.add(ticket3);
        tickets.add(ticket4);
        tickets.add(ticket5);
        List<Long> uniqueStudentIds = TicketStatistics.findUniqueStudentIds(tickets);
        assertEquals(3, uniqueStudentIds.size());
        assertEquals(100L, uniqueStudentIds.getFirst());
        assertEquals(200L, uniqueStudentIds.get(1));
        assertEquals(300L, uniqueStudentIds.getLast());

    }

    @Test
    void findUniqueStudentIdsShouldReturnEmptyListForEmptyInput() {
        List<Ticket> tickets = new ArrayList<>();
        List<Long> uniqueStudentIds = TicketStatistics.findUniqueStudentIds(tickets);
        assertEquals(0, uniqueStudentIds.size());

    }

    @Nested
    public class SortingTests {
        private List<Ticket> tickets;

        @BeforeEach
        void setUp() {
            tickets = new ArrayList<>();
            Ticket ticket1 = new Ticket(
                    100L,
                    "Не работает проектор",
                    "Аудитория 301",
                    TicketPriority.LOW
            );
            Ticket ticket2 = new Ticket(
                    200L,
                    "Не работает проектор",
                    "Аудитория 301",
                    TicketPriority.EMERGENCY
            );
            Ticket ticket3 = new Ticket(
                    100L,
                    "Не работает проектор",
                    "Аудитория 301",
                    TicketPriority.HIGH
            );
            Ticket ticket4 = new Ticket(
                    300L,
                    "Не работает проектор",
                    "Аудитория 301",
                    TicketPriority.MEDIUM
            );
            Ticket ticket5 = new Ticket(
                    200L,
                    "Не работает проектор",
                    "Аудитория 301",
                    TicketPriority.HIGH
            );


            tickets.add(ticket1);
            tickets.add(ticket2);
            tickets.add(ticket3);
            tickets.add(ticket4);
            tickets.add(ticket5);
        }

        @Test
        void ticketsShouldBeSortedByPriority() {
            List<Ticket> sorted = sortByPriorityThenStudentId(tickets);

            assertEquals(TicketPriority.EMERGENCY, sorted.getFirst().getPriority());
            assertEquals(TicketPriority.HIGH, sorted.get(1).getPriority());
            assertEquals(TicketPriority.HIGH, sorted.get(2).getPriority());
            assertEquals(TicketPriority.MEDIUM, sorted.get(3).getPriority());
            assertEquals(TicketPriority.LOW, sorted.getLast().getPriority());


        }

        @Test
        void samePriorityTicketsShouldBeSortedByStudentId() {
            List<Ticket> sorted = sortByPriorityThenStudentId(tickets);

            assertEquals(100L, sorted.get(1).getStudentId());
            assertEquals(200L, sorted.get(2).getStudentId());

        }

        @Test
        void sortingShouldNotModifyOriginalList() {
            List<Ticket> originalOrder = new ArrayList<>(tickets);

            List<Ticket> sorted = sortByPriorityThenStudentId(tickets);

            assertEquals(originalOrder, tickets);
            assertNotSame(tickets, sorted);

        }
    }

    @Test
    void filterShouldReturnOnlyHighPriorityTickets() {
        List<Ticket> tickets = new ArrayList<>();
        Ticket ticket1 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.LOW
        );
        Ticket ticket2 = new Ticket(
                200L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        Ticket ticket3 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        tickets.add(ticket1);
        tickets.add(ticket2);
        tickets.add(ticket3);

        List<Ticket> ticketsFilter = filter(tickets, ticket -> ticket.getPriority() == TicketPriority.HIGH);
        assertEquals(2, ticketsFilter.size());
        assertEquals(TicketPriority.HIGH, ticketsFilter.getFirst().getPriority());
        assertEquals(TicketPriority.HIGH, ticketsFilter.get(1).getPriority());

    }

    @Test
    void findUrgentStudentIdsShouldFilterRemoveDuplicatesAndSort() {
        List<Ticket> tickets = new ArrayList<>();
        Ticket ticket1 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.LOW
        );
        Ticket ticket2 = new Ticket(
                200L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        Ticket ticket3 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        Ticket ticket4 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        tickets.add(ticket1);
        tickets.add(ticket2);
        tickets.add(ticket3);
        tickets.add(ticket4);
        List<Long> uniqueStudentIds = TicketStatistics.findUrgentStudentIds(tickets);
        assertEquals(List.of(100L, 200L), uniqueStudentIds);

    }

    @Test
    void countByPriorityShouldReturnNumberOfTicketsForEachPriority(){
        List<Ticket> tickets = new ArrayList<>();
        Ticket ticket1 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.LOW
        );
        Ticket ticket2 = new Ticket(
                200L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        Ticket ticket3 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH
        );
        Ticket ticket4 = new Ticket(
                100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.EMERGENCY
        );
        tickets.add(ticket1);
        tickets.add(ticket2);
        tickets.add(ticket3);
        tickets.add(ticket4);
        Map<TicketPriority,Long> result = TicketStatistics.countByPriority(tickets);
        assertEquals(
                Map.of(
                        TicketPriority.HIGH, 2L,
                        TicketPriority.LOW, 1L,
                        TicketPriority.EMERGENCY, 1L
                ),
                result
        );
    }
}
