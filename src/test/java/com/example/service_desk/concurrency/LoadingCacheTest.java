package com.example.service_desk.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoadingCacheTest {
    @Test
    void sameKeyShouldBeLoadedOnlyOnce()  {

        LoadingCache<Long, String> cache = new LoadingCache<>();
        AtomicInteger loadCount = new AtomicInteger();
        Function<Long, String> loader = key -> {
        loadCount.incrementAndGet();
            return "ticket-" + key;
        };

        String first = cache.get(10L, loader);
        String second = cache.get(10L, loader);

        assertEquals("ticket-10", first);
        assertEquals("ticket-10", second);
        assertEquals(1, loadCount.get());
        assertEquals(1, cache.size());

    }
    @Test
    void sameKeyRequestedConcurrentlyShouldBeLoadedOnce() throws Exception {

        LoadingCache<Long, String> cache = new LoadingCache<>();

        AtomicInteger loadCount = new AtomicInteger();

        Function<Long, String> loader = key -> {
            loadCount.incrementAndGet();
            return "ticket-" + key;
        };

        CyclicBarrier barrier = new CyclicBarrier(2);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<String> request = () -> {
            barrier.await();
            return cache.get(10L, loader);
        };

        try {
            Future<String> first = executor.submit(request);
            Future<String> second = executor.submit(request);

            assertEquals("ticket-10", first.get());
            assertEquals("ticket-10", second.get());

            assertEquals(1, loadCount.get());
            assertEquals(1, cache.size());
        } finally {
            executor.shutdownNow();
        }
    }
}
