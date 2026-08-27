package com.example.service_desk.ticket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class TicketOptimisticLockingIntegrationTest {
    private final TicketRepository ticketRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Autowired
    public TicketOptimisticLockingIntegrationTest(TicketRepository ticketRepository, PlatformTransactionManager platformTransactionManager) {
        this.ticketRepository = ticketRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Test
    void oneOfConcurrentAssignmentsShouldFailWithOptimisticLocking() throws Exception {
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        Ticket ticket = new Ticket(100L,
                "Не работает проектор",
                "Аудитория 301",
                TicketPriority.HIGH);
        Long ticketId = transactionTemplate.execute(status -> {
                    ticketRepository.save(ticket);
                    return ticket.getId();
                }
        );
        Ticket ticketAfter = ticketRepository.findById(ticketId).orElse(null);
        assertNotNull(ticketAfter);
        assertEquals(TicketStatus.CREATED, ticketAfter.getStatus());

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

        try {
            Future<Boolean> firstFuture = executorService.submit(
                    () -> assignInNewTransaction(
                            ticketId,
                            50L,
                            transactionTemplate,
                            cyclicBarrier
                    )
            );

            Future<Boolean> secondFuture = executorService.submit(
                    () -> assignInNewTransaction(
                            ticketId,
                            60L,
                            transactionTemplate,
                            cyclicBarrier
                    )
            );
            boolean first = firstFuture.get(5, TimeUnit.SECONDS);
            boolean second = secondFuture.get(5, TimeUnit.SECONDS);
            assertNotEquals(first, second);
            Ticket saves = ticketRepository.findById(ticketId).orElseThrow();
            assertEquals(TicketStatus.ASSIGNED, saves.getStatus());
            if (first) {
                assertEquals(50L, saves.getAssignedSpecialistId());
            }
            if (second) {
                assertEquals(60L, saves.getAssignedSpecialistId());
            }
        } finally {
            executorService.shutdownNow();
        }
    }

    private boolean assignInNewTransaction(
            long ticketId,
            long specialistId,
            TransactionTemplate transactionTemplate,
            CyclicBarrier barrier
    ) {
        try {
            transactionTemplate.executeWithoutResult(
                    status -> {

                        Ticket parallelisation = ticketRepository.findById(ticketId).orElseThrow();
                        try {
                            barrier.await();
                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(exception);
                        } catch (BrokenBarrierException exception) {
                            throw new RuntimeException(exception);
                        }

                        parallelisation.assignTo(specialistId);

                    });
            return true;
        } catch (ObjectOptimisticLockingFailureException exception) {
            return false;
        }
    }

}
