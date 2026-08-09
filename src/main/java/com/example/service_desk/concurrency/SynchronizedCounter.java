package com.example.service_desk.concurrency;

public class SynchronizedCounter implements Counter {

    private long value = 0;

    @Override
    public synchronized void increment() {
        value++;
    }

    @Override
    public synchronized long get() {
        return value;
    }
}
