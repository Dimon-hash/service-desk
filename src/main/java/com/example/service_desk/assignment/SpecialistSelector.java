package com.example.service_desk.assignment;

import com.example.service_desk.specialist.Specialist;
import com.example.service_desk.specialist.SpecialistStatus;
import com.example.service_desk.ticket.Ticket;
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

            if (bestAvailable == null ||
                    specialist.getLevel().compareTo(bestAvailable.getLevel()) > 0) {
                bestAvailable = specialist;
            }

            if (Objects.equals(specialist.getLocation(), ticket.getLocation())) {
                if (bestAtSameLocation == null ||
                        specialist.getLevel().compareTo(bestAtSameLocation.getLevel()) > 0) {
                    bestAtSameLocation = specialist;
                }

            }

        }
        if (bestAtSameLocation != null) return Optional.of(bestAtSameLocation);
        return Optional.ofNullable(bestAvailable);

    }
}
