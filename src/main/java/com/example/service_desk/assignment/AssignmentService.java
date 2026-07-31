package com.example.service_desk.assignment;

import com.example.service_desk.specialist.Specialist;
import com.example.service_desk.specialist.SpecialistNotFoundException;
import com.example.service_desk.specialist.SpecialistRepository;
import com.example.service_desk.ticket.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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

        Long previousSpecialistId = ticket.getAssignedSpecialistId();

        if (ticket.getStatus() == TicketStatus.BLOCKED && previousSpecialistId != null) {
            specialists = specialists.stream()
                    .filter(specialist ->
                            !Objects.equals(specialist.getId(), previousSpecialistId)
                    )
                    .toList();
        }

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
    @Transactional
    public Ticket completeAssignedTicket(long ticketId){
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
                () -> new TicketNotFoundException("Ticket not found. Id: " + ticketId));
        Long id = ticket.getAssignedSpecialistId();
        if (id == null) {
            throw new InvalidTicketStateException("Assignment not found. Id: " + ticketId);
        }
        Specialist specialist = specialistRepository.findById(id).orElseThrow(
                ()-> new SpecialistNotFoundException("Assignment not found. Id: " + id)
        );
        ticket.complete();
        specialist.finishWork();
        ticketRepository.save(ticket);
        specialistRepository.save(specialist);
        return ticket;
    }

    @Transactional
    public Ticket blockAssignedTicket(long ticketId, String reason) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
                () -> new TicketNotFoundException("Ticket not found. Id: " + ticketId));
        Long id = ticket.getAssignedSpecialistId();
        if (id == null) {
            throw new InvalidTicketStateException("Assignment not found. Id: " + ticketId);
        }
        Specialist specialist = specialistRepository.findById(id).orElseThrow(
                () -> new SpecialistNotFoundException("Assignment not found. Id: " + id)
        );
        ticket.block(reason);
        specialist.finishWork();
        ticketRepository.save(ticket);
        specialistRepository.save(specialist);
        return ticket;

    }

    @Transactional
    public Ticket assignManually(long ticketId, long specialistId){
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
                () -> new TicketNotFoundException("Ticket not found. Id: " + ticketId));

        Long previousSpecialistId = ticket.getAssignedSpecialistId();
        Specialist newSpecialist = specialistRepository.findById(specialistId).orElseThrow(
                () -> new SpecialistNotFoundException("Assignment not found. Id: " + specialistId)
        );
        if (previousSpecialistId != null && specialistId != previousSpecialistId && ticket.getStatus() == TicketStatus.ASSIGNED) {
            Specialist previousSpecialist = specialistRepository.findById(previousSpecialistId).orElseThrow(
                    () -> new SpecialistNotFoundException("Assignment not found. Id: " + previousSpecialistId));
            previousSpecialist.finishWork();
            specialistRepository.save(previousSpecialist);
        }
        newSpecialist.startWork();
        ticket.assignTo(newSpecialist.getId());
        ticketRepository.save(ticket);
        specialistRepository.save(newSpecialist);
        return ticket;
    }

}

