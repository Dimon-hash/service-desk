package com.example.service_desk.specialist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SpecialistControllerIntegrationTest {

    private final MockMvc mockMvc;
    private final SpecialistRepository specialistRepository;
    private final SpecialistService specialistService;

    @Autowired
    public SpecialistControllerIntegrationTest(MockMvc mockMvc,
                                               SpecialistRepository specialistRepository,
                                               SpecialistService specialistService) {
        this.mockMvc = mockMvc;
        this.specialistRepository = specialistRepository;
        this.specialistService = specialistService;
    }

    @Test
    void registerSpecialistShouldReturn201AndCreatedSpecialist() throws Exception {

        String requestJson = """
                {
                  "fullName": "Иван Петров",
                  "location": "Корпус 1",
                  "level": "MIDDLE"
                }
                """;


        mockMvc.perform(post("/api/specialists")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Иван Петров"))
                .andExpect(jsonPath("$.location").value("Корпус 1"))
                .andExpect(jsonPath("$.level").value("MIDDLE"))
                .andExpect(jsonPath("$.status").value("OFF_DUTY"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.rating").isEmpty());

    }

    @Test
    void registerSpecialistWithBlankNameShouldReturn400AndNotSave() throws Exception {
        int size = specialistRepository.findAll().size();

        String requestJson = """
                {
                  "fullName": "  ",
                  "location": "Корпус 1",
                  "level": "MIDDLE"
                }
                """;


        mockMvc.perform(post("/api/specialists")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
        assertEquals(size, specialistRepository.findAll().size());

    }

    @Test
    void studentShouldNotRegisterSpecialist() throws Exception {
        String requestJson = """
                {
                  "fullName": "Иван Петров",
                  "location": "Корпус 1",
                  "level": "MIDDLE"
                }
                """;

        int size = specialistRepository.findAll().size();

        mockMvc.perform(post("/api/specialists")
                        .with(csrf())
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden());
        assertEquals(size, specialistRepository.findAll().size());

    }

    @Test
    void getSpecialistByIdShouldReturn200AndSpecialist() throws Exception {
        Specialist specialist = specialistService.registerSpecialist(
                "Иван Петров",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );

        mockMvc.perform(get("/api/specialists/{specialistId}", specialist.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(specialist.getId()))
                .andExpect(jsonPath("$.fullName").value("Иван Петров"))
                .andExpect(jsonPath("$.location").value("Корпус 1"))
                .andExpect(jsonPath("$.level").value("MIDDLE"))
                .andExpect(jsonPath("$.status").value("OFF_DUTY"))
                .andExpect(jsonPath("$.rating").isEmpty());

    }

    @Test
    void getMissingSpecialistShouldReturn404() throws Exception {

        mockMvc.perform(get("/api/specialists/{specialistId}", Long.MAX_VALUE)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());

    }

    @Test
    void getAllSpecialistsShouldReturn200AndJsonArray() throws Exception {
        int size = specialistRepository.findAll().size();
        Specialist specialist1 = specialistService.registerSpecialist(
                "Иван Петров",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        Specialist specialist2 = specialistService.registerSpecialist(
                "Петр Иванов",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        mockMvc.perform(get("/api/specialists")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(size + 2));


    }

    @Test
    void startShiftShouldReturnAvailableSpecialist() throws Exception {
        Specialist specialist = specialistService.registerSpecialist(
                "Иван Петров",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        assertEquals(SpecialistStatus.OFF_DUTY, specialist.getStatus());
        mockMvc.perform(patch("/api/specialists/{specialistId}/shift/start", specialist.getId())
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(specialist.getId()))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
        Specialist savedSpecialist =
                specialistService.getSpecialist(specialist.getId());

        assertEquals(
                SpecialistStatus.AVAILABLE,
                savedSpecialist.getStatus()
        );
    }

    @Test
    void finishShiftShouldReturnOffDutySpecialist() throws Exception {
        Specialist specialist = specialistService.registerSpecialist(
                "Иван Петров",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        specialistService.startShift(specialist.getId());
        assertEquals(SpecialistStatus.AVAILABLE, specialist.getStatus());
        mockMvc.perform(patch("/api/specialists/{specialistId}/shift/finish", specialist.getId())
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(specialist.getId()))
                .andExpect(jsonPath("$.status").value("OFF_DUTY"));
        Specialist savedSpecialist =
                specialistService.getSpecialist(specialist.getId());

        assertEquals(
                SpecialistStatus.OFF_DUTY,
                savedSpecialist.getStatus()
        );

    }

    @Test
    void startingShiftTwiceShouldReturn409() throws Exception {
        Specialist specialist = specialistService.registerSpecialist(
                "Иван Петров",
                "Корпус 1",
                SpecialistLevel.MIDDLE
        );
        specialistService.startShift(specialist.getId());
        mockMvc.perform(patch("/api/specialists/{specialistId}/shift/start", specialist.getId())
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isConflict());

    }

}
