package com.example.service_desk.algorithm.old;

import java.util.HashMap;

public class PrefixSumsArrays {
    public static long countTarget(long[] array, long target) {
        long[] prefixSums = new long[array.length + 1];
        prefixSums[0] = 0;
        long result = 0;
        HashMap<Long, Long> map = new HashMap<>();
        map.put(0L, 1L);

        for (int i = 0; i < array.length; i++) {
            prefixSums[i + 1] = prefixSums[i] + array[i];

            result += map.getOrDefault(prefixSums[i + 1] - target, 0L);

            map.put(prefixSums[i + 1], map.getOrDefault(prefixSums[i + 1], 0L) + 1L);

        }
        return result;

    }
}
