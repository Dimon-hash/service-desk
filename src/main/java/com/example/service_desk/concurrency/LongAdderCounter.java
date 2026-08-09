package com.example.service_desk.concurrency;


import java.util.concurrent.atomic.LongAdder;

public class LongAdderCounter implements Counter {

    private final LongAdder value = new LongAdder();

    @Override
    public void increment() {
        value.increment();

    }

    @Override
    public long get() {
        return value.sum();
    }
}
