package com.example.service_desk.concurrency;


import java.util.concurrent.CompletableFuture;

public class AsyncAssignmentSummary {
    public CompletableFuture<String> combine(
            CompletableFuture<String> ticketFuture,
            CompletableFuture<String> specialistFuture
    ){
        return ticketFuture.thenCombine(
                specialistFuture,
                (ticket, specialist) -> ticket + " assigned to " + specialist
        );

    }
    public CompletableFuture<String> combineWithFallback(
            CompletableFuture<String> ticketFuture,
            CompletableFuture<String> specialistFuture
    ){
        return combine(ticketFuture, specialistFuture)
                .exceptionally(error->"Assignment information unavailable");
    }
}
