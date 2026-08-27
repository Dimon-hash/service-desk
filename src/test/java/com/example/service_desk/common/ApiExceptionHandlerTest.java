package com.example.service_desk.common;

import com.example.service_desk.assignment.AssignmentController;
import com.example.service_desk.assignment.AssignmentService;
import com.example.service_desk.ticket.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiExceptionHandlerTest {

    @Test
    void optimisticLockingFailureShouldReturn409() throws Exception {
        AssignmentService assignmentService = mock(AssignmentService.class);

        AssignmentController assignmentController = new AssignmentController(assignmentService);
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(assignmentController)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
        when(assignmentService.assignManually(1L, 50L))
                .thenThrow(
                        new ObjectOptimisticLockingFailureException(
                                Ticket.class,
                                1L
                        )
                );

        String requestJson = """
                {
                  "specialistId": 50
                }
                """;

        mockMvc.perform(patch("/api/tickets/{ticketId}/assignment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title")
                        .value("Ticket update conflict"))
                .andExpect(jsonPath("$.detail")
                        .value("Ticket was changed by another request"));
    }
}