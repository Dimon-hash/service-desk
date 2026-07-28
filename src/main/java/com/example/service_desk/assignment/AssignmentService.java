package com.example.service_desk.assignment;

import com.example.service_desk.specialist.Specialist;
import com.example.service_desk.specialist.SpecialistRepository;
import com.example.service_desk.ticket.Ticket;
import com.example.service_desk.ticket.TicketNotFoundException;
import com.example.service_desk.ticket.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service

public class AssignmentService {

    private final TicketRepository ticketRepository;
    private final SpecialistRepository specialistRepository;
    private final SpecialistSelector specialistSelector;

    public AssignmentService(TicketRepository ticketRepository, SpecialistRepository specialistRepository, SpecialistSelector specialistSelector) {
        this.ticketRepository = ticketRepository;
        this.specialistRepository = specialistRepository;
        this.specialistSelector = specialistSelector;
    }

    @Transactional
    public Ticket assignAutomatically(long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
                ()-> new TicketNotFoundException( "Ticket not found. Id: " + ticketId));
        List<Specialist> specialists = specialistRepository.findAll();
        Optional<Specialist> selected =
                specialistSelector.select(ticket, specialists);

        if (selected.isEmpty()) {
            return ticket;
        }

        Specialist selectedSpecialist = selected.orElseThrow();

        selectedSpecialist.startWork();
        ticket.assignTo(selectedSpecialist.getId());
        specialistRepository.save(selectedSpecialist);
        ticketRepository.save(ticket);
        return ticket;


    }

}

