package com.example.service_desk.assignment;

import com.example.service_desk.specialist.Specialist;
import com.example.service_desk.specialist.SpecialistLevel;
import com.example.service_desk.specialist.SpecialistStatus;
import com.example.service_desk.ticket.Ticket;
import com.example.service_desk.ticket.TicketPriority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class SpecialistSelector {
    public Optional<Specialist> select(
            Ticket ticket,
            List<Specialist> specialists
    ) {

        Specialist bestAvailable = null;
        Specialist bestAtSameLocation = null;

        for (Specialist specialist : specialists) {
            if (specialist.getStatus() != SpecialistStatus.AVAILABLE) {
                continue;
            }

            bestAvailable = chooseBetterCandidate(
                    ticket,
                    specialist,
                    bestAvailable
            );

            if (Objects.equals(specialist.getLocation(), ticket.getLocation())) {
                bestAtSameLocation = chooseBetterCandidate(
                        ticket,
                        specialist,
                        bestAtSameLocation
                );

            }

        }
        if (bestAtSameLocation != null) return Optional.of(bestAtSameLocation);
        return Optional.ofNullable(bestAvailable);

    }

    private Specialist chooseBetterCandidate(Ticket ticket, Specialist candidate, Specialist currentBest) {
        if (currentBest == null) return candidate;
        int candidateScore = levelScore(ticket.getPriority(), candidate.getLevel());
        int currentBestScore = levelScore(ticket.getPriority(), currentBest.getLevel());
        if (candidateScore > currentBestScore) {
            return candidate;
        }
        return currentBest;

    }

    private int levelScore(TicketPriority priority, SpecialistLevel level) {
        return switch (priority) {
            case LOW -> switch (level) {
                case JUNIOR -> 3;
                case MIDDLE -> 2;
                case SENIOR -> 1;
            };

            case MEDIUM -> switch (level) {
                case JUNIOR -> 1;
                case MIDDLE -> 3;
                case SENIOR -> 2;
            };

            case HIGH, EMERGENCY -> switch (level) {
                case JUNIOR -> 1;
                case MIDDLE -> 2;
                case SENIOR -> 3;
            };
        };
    }
}
