package com.example.service_desk.ticket;

import java.util.ArrayDeque;
import java.util.Queue;

public class PendingTicketQueue {
    private final Queue<Long> queue = new ArrayDeque<>();

    public void add(long ticketId) {
        if (ticketId <= 0) {
            throw new IllegalArgumentException("ticketId must be greater than zero");
        }
        queue.offer(ticketId);
    }

    public Long peek() {
        return queue.peek();

    }

    public Long poll() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }
}
