package com.example.service_desk.ticket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PendingTicketQueueTest {

    private PendingTicketQueue pendingTicketQueue;

    @BeforeEach
    void setUp() {
        pendingTicketQueue = new PendingTicketQueue();
    }

    @Test
    void ticketsShouldBeReturnedInFifoOrder() {
        pendingTicketQueue.add(1);
        pendingTicketQueue.add(3);
        pendingTicketQueue.add(2);
        assertEquals(3, pendingTicketQueue.size());
        assertEquals(1, pendingTicketQueue.poll());
        assertEquals(3, pendingTicketQueue.peek());
        assertEquals(3, pendingTicketQueue.poll());
        assertEquals(1, pendingTicketQueue.size());
    }

    @Test
    void nonPositiveTicketIdShouldBeRejected() {
        pendingTicketQueue.add(1);
        pendingTicketQueue.add(3);

        assertThrows(IllegalArgumentException.class, () -> pendingTicketQueue.add(0));
        assertThrows(IllegalArgumentException.class, () -> pendingTicketQueue.add(-1));
    }

    @Test
    void emptyQueueShouldReturnNullForPeekAndPoll() {
        assertNull(pendingTicketQueue.poll());
        assertNull(pendingTicketQueue.peek());
    }

}
