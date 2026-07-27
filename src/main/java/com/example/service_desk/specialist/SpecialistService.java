package com.example.service_desk.specialist;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpecialistService {

    private final SpecialistRepository specialistRepository;

    public SpecialistService(SpecialistRepository specialistRepository) {
        this.specialistRepository = specialistRepository;
    }

    @Transactional
    public Specialist registerSpecialist(
            String fullName,
            String location,
            SpecialistLevel level
    ) {
        Specialist specialist = new Specialist(fullName, location, level);
        return specialistRepository.save(specialist);
    }

    @Transactional
    public Specialist startShift(long specialistId) {
        Specialist specialist = getSpecialist(specialistId);
        specialist.startShift();
        return specialistRepository.save(specialist);
    }

    @Transactional
    public Specialist finishShift(long specialistId) {
        Specialist specialist = getSpecialist(specialistId);
        specialist.finishShift();
        return specialistRepository.save(specialist);
    }

    public Specialist getSpecialist(long specialistId) {
        return specialistRepository
                .findById(specialistId)
                .orElseThrow(() -> new SpecialistNotFoundException(
                        "Specialist not found. Id: " + specialistId
                ));
    }
    public List<Specialist> getAllSpecialists() {
        return specialistRepository.findAll();
    }
}
