package com.example.service_desk.specialist;

import java.util.List;
import java.util.Optional;

public interface SpecialistRepository {
    Specialist save(Specialist specialist);

    Optional<Specialist> findById(long id);

    List<Specialist> findAll();
}
