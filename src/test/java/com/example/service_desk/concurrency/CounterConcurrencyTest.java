package com.example.service_desk.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CounterConcurrencyTest {

    @Test
    void synchronizedCounterShouldNotLoseIncrements() throws Exception {
        Counter counter = new SynchronizedCounter();
        assertDoesNotLoseIncrements(counter);
    }

    @Test
    void atomicCounterShouldNotLoseIncrements() throws Exception {
        Counter counter = new AtomicCounter();
        assertDoesNotLoseIncrements(counter);
    }

    @Test
    void longAdderCounterShouldNotLoseIncrements() throws Exception {
        Counter counter = new LongAdderCounter();
        assertDoesNotLoseIncrements(counter);
    }

    private void assertDoesNotLoseIncrements(Counter counter) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Runnable incrementManyTimes = () -> {
            for (int i = 0; i < 10_000; i++) {
                counter.increment();
            }
        };

        try {
            Future<?> first = executor.submit(incrementManyTimes);
            Future<?> second = executor.submit(incrementManyTimes);

            first.get();
            second.get();

            assertEquals(20_000L, counter.get());
        } finally {
            executor.shutdownNow();
        }

    }


}