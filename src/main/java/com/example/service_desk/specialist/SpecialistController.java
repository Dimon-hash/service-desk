package com.example.service_desk.specialist;

import com.example.service_desk.specialist.dto.RegisterSpecialistRequest;
import com.example.service_desk.specialist.dto.SpecialistResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialists")
public class SpecialistController {
    private final SpecialistService specialistService;

    public SpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpecialistResponse registerSpecialist(
            @Valid @RequestBody RegisterSpecialistRequest request
    ) {
        Specialist specialist = specialistService.registerSpecialist(request.fullName(),
                request.location(),
                request.level());
        return SpecialistResponse.from(specialist);
    }

    @GetMapping("/{specialistId}")
    public SpecialistResponse getSpecialist(@PathVariable long specialistId) {
        return SpecialistResponse.from(specialistService.getSpecialist(specialistId));
    }

    @GetMapping()
    public List<SpecialistResponse> getSpecialists() {
        List<Specialist> specialists = specialistService.getAllSpecialists();
        return specialists.stream()
                .map(SpecialistResponse::from)
                .toList();
    }

    @PatchMapping("/{specialistId}/shift/start")
    public SpecialistResponse specialistStartShift(@PathVariable long specialistId){
        return SpecialistResponse.from(specialistService.startShift(specialistId));
    }

    @PatchMapping("/{specialistId}/shift/finish")
    public SpecialistResponse specialistFinishShift(@PathVariable long specialistId){
        return SpecialistResponse.from(specialistService.finishShift(specialistId));
    }


}
