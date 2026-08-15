package com.example.service_desk.audit;

import com.example.service_desk.ticket.TicketPriority;
import com.example.service_desk.ticket.TicketRepository;
import com.example.service_desk.ticket.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TicketCreationRollbackIntegrationTest {
    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

    @MockitoBean
    private TicketEventRepository ticketEventRepository;

    @Autowired
    public TicketCreationRollbackIntegrationTest(TicketService ticketService,
                                                 TicketRepository ticketRepository) {
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
    }

    @Test
    void ticketShouldNotBeStoredWhenAuditSavingFails() {
        long countTicket1 = ticketRepository.findAll().size();
        when(ticketEventRepository.save(any(TicketEvent.class)))
                .thenThrow(new RuntimeException("Audit unavailable"));
        assertThrows(RuntimeException.class,
                () -> ticketService.createTicket(100L,
                        "Не работает проектор",
                        "Аудитория 301",
                        TicketPriority.HIGH));
        verify(ticketEventRepository,times(1)).save(any(TicketEvent.class));
        long countTicket2 = ticketRepository.findAll().size();
        assertEquals(countTicket1, countTicket2);

    }


}
