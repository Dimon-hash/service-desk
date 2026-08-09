package com.example.service_desk.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AsyncAssignmentSummaryTest {

    @Test
    void combineShouldReturnAssignmentSummary() {
        AsyncAssignmentSummary asyncAssignmentSummary = new AsyncAssignmentSummary();
        CompletableFuture<String> ticket = CompletableFuture.completedFuture("Ticket 15");
        CompletableFuture<String> specialist = CompletableFuture.completedFuture("Ivan");
        CompletableFuture<String> result = asyncAssignmentSummary.combine(ticket, specialist);
        String result1 = result.join();
        assertEquals("Ticket 15 assigned to Ivan", result1);
    }

    @Test
    void failedTicketFutureShouldMakeCombinedFutureFail() {
        AsyncAssignmentSummary asyncAssignmentSummary = new AsyncAssignmentSummary();

        CompletableFuture<String> ticketFuture = CompletableFuture.failedFuture(
                new IllegalStateException("Ticket service unavailable")
        );

        CompletableFuture<String> specialistFuture = CompletableFuture.completedFuture("Ivan");
        assertThrows(
                CompletionException.class,
                () -> asyncAssignmentSummary
                        .combine(ticketFuture, specialistFuture)
                        .join()
        );
    }

    @Test
    void failedFutureShouldReturnFallbackSummary() {
        AsyncAssignmentSummary asyncAssignmentSummary = new AsyncAssignmentSummary();

        CompletableFuture<String> ticketFuture = CompletableFuture.failedFuture(
                new IllegalStateException("Ticket service unavailable")
        );

        CompletableFuture<String> specialistFuture = CompletableFuture.completedFuture("Ivan");
        assertEquals("Assignment information unavailable",
                asyncAssignmentSummary
                        .combineWithFallback(ticketFuture, specialistFuture)
                        .join()
        );

    }

}
