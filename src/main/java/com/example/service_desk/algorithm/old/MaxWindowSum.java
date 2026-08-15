package com.example.service_desk.algorithm.old;

public class MaxWindowSum {
    public static int maxWindow(int windowLength, int[] array) {

        int temp = 0;

        for (int i = 0; i < windowLength; i++) {
            temp += array[i];
        }

        int result = temp;

        for (int i = windowLength; i < array.length; i++) {
            temp = temp - array[i - windowLength] + array[i];
            if (temp > result) {
                result = temp;
            }
        }
        return result;
    }
}
