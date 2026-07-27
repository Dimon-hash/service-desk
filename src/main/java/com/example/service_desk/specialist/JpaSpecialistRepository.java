package com.example.service_desk.specialist;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaSpecialistRepository implements SpecialistRepository{

    private final SpringDataSpecialistRepository springDataRepository;

    public JpaSpecialistRepository(SpringDataSpecialistRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }


    @Override
    public Specialist save(Specialist specialist) {
        return springDataRepository.save(specialist);
    }

    @Override
    public Optional<Specialist> findById(long id) {
        return springDataRepository.findById(id);
    }

    @Override
    public List<Specialist> findAll() {
        return springDataRepository.findAll();
    }
}
