package com.example.service_desk.common;

import java.util.List;

public class ListUtils {
    public static <T> T last(List<T> values) {
        if (values == null || values.isEmpty()) throw new IllegalArgumentException();
        return values.getLast();
    }

    public static <T> void copyAll(
            List<? extends T> source,
            List<? super T> destination
    ) {
        if (source == null || destination == null) throw new IllegalArgumentException();

        for (T item : source) {
            destination.add(item);
        }

    }
}
