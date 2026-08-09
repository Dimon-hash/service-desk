package com.example.service_desk.algorithm;

import java.util.HashSet;
import java.util.Set;

public class PairSum {
    public static boolean hasPairWithSum(int[] numbers, int target) {
        Set<Integer> set = new HashSet<>();

        for (int number : numbers) {
            if (set.contains(target - number)) {
                return true;
            }
            set.add(number);
        }
        return false;
    }
}
