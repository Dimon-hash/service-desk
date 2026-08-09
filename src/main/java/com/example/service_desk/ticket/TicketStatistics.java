package com.example.service_desk.ticket;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TicketStatistics {
    public static Map<TicketStatus, Integer> countByStatus(List<TicketStatus> statuses) {
        Map<TicketStatus, Integer> map = new HashMap<>();
        for (TicketStatus status : statuses) {
            if (!map.containsKey(status)) {
                map.put(status, 0);
            }
            map.put(status, map.get(status) + 1);
        }
        return map;

    }

    public static List<Long> findUniqueStudentIds(List<Ticket> tickets) {
        Set<Long> uniqueStudentIds = new HashSet<>();
        List<Long> uniqueStudentIdList = new ArrayList<>();

        for (Ticket ticket : tickets) {
            long studentId = ticket.getStudentId();
            if (uniqueStudentIds.add(studentId)) {
                uniqueStudentIdList.add(studentId);

            }

        }
        return uniqueStudentIdList;
    }

    public static List<Ticket> sortByPriorityThenStudentId(List<Ticket> tickets) {
        List<Ticket> sortedTickets = new ArrayList<>(tickets);
        Map<TicketPriority, Integer> map = new HashMap<>();
        map.put(TicketPriority.EMERGENCY, 1);
        map.put(TicketPriority.HIGH, 2);
        map.put(TicketPriority.MEDIUM, 3);
        map.put(TicketPriority.LOW, 4);

        Comparator<Ticket> comparator =
                Comparator.comparingInt((Ticket ticket) -> map.get(ticket.getPriority()))
                        .thenComparingLong(Ticket::getStudentId);
        sortedTickets.sort(comparator);
        return sortedTickets;

    }

    public static List<Ticket> filter(
            List<Ticket> tickets,
            Predicate<Ticket> condition
    ) {
        List<Ticket> ticketList = new ArrayList<>();

        for (Ticket ticket : tickets) {
            if (condition.test(ticket)) {
                ticketList.add(ticket);
            }

        }
        return ticketList;
    }

    public static List<Long> findUrgentStudentIds(List<Ticket> tickets) {
        return tickets.stream()
                .filter(ticket -> (ticket.getPriority() == TicketPriority.HIGH || ticket.getPriority() == TicketPriority.EMERGENCY))
                .map(Ticket::getStudentId)
                .distinct()
                .sorted()
                .toList();

    }

    public static Map<TicketPriority, Long> countByPriority(List<Ticket> tickets) {
        return tickets.stream()
                .collect(Collectors.groupingBy(
                        Ticket::getPriority,
                        Collectors.counting()
                ));
    }

}
