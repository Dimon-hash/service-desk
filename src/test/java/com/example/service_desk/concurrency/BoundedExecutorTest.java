package com.example.service_desk.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BoundedExecutorTest {

    @Test
    void thirdTaskShouldBeRejectedWhenWorkerAndQueueAreFull() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1),
                new ThreadPoolExecutor.AbortPolicy()
        );
        CountDownLatch releaseFirstTask = new CountDownLatch(1);

        try {
            executor.submit(() -> {
                try {
                    releaseFirstTask.await();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            });
            executor.submit(() -> {

            });
            assertThrows(
                    RejectedExecutionException.class,
                    () -> executor.submit(() -> {
                    })
            );

        } finally {
            releaseFirstTask.countDown();
            executor.shutdownNow();
        }
    }
}