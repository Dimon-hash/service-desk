package com.example.service_desk.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class LoadingCache<K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

    public V get(K key, Function<K, V> loader) {
        return cache.computeIfAbsent(key, loader);
    }

    public int size() {
        return cache.size();
    }
}
