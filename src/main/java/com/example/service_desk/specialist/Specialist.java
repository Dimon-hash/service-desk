package com.example.service_desk.specialist;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "specialists")
public class Specialist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;
    @Column(name = "location", nullable = false, length = 255)
    private String location;
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 30)
    private SpecialistLevel level;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SpecialistStatus status;
    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    protected Specialist() {
    }

    public Specialist(String fullName, String location, SpecialistLevel level) {
        this.fullName = fullName;
        this.location = location;
        this.level = level;
        status = SpecialistStatus.OFF_DUTY;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public SpecialistLevel getLevel() {
        return level;
    }

    public SpecialistStatus getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void startShift() {
        if (status != SpecialistStatus.OFF_DUTY) {
            throw new InvalidSpecialistStateException("Cannot start shift while specialist is not off");
        }
        this.status = SpecialistStatus.AVAILABLE;
    }

    public void startWork() {
        if (status != SpecialistStatus.AVAILABLE) {
            throw new InvalidSpecialistStateException("Cannot start work while specialist is not available");
        }
        this.status = SpecialistStatus.BUSY;
    }

    public void finishWork() {
        if (status != SpecialistStatus.BUSY) {
            throw new InvalidSpecialistStateException("Cannot finish work while specialist is not busy");
        }
        this.status = SpecialistStatus.AVAILABLE;
    }

    public void finishShift() {
        if (status != SpecialistStatus.AVAILABLE) {
            throw new InvalidSpecialistStateException("Cannot finish shift while specialist is not available");
        }
        this.status = SpecialistStatus.OFF_DUTY;
    }

}
