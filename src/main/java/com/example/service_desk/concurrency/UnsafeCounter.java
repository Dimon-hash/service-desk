package com.example.service_desk.concurrency;

public class UnsafeCounter implements Counter {

    private long value = 0;

    @Override
    public void increment() {
        value++;
    }

    @Override
    public long get() {
        return value;
    }
}
