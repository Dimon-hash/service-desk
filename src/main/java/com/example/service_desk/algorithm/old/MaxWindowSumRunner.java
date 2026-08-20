package com.example.service_desk.algorithm.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class MaxWindowSumRunner {
    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer tokenizer = new StringTokenizer(reader.readLine());

        int firstArrayLength = Integer.parseInt(tokenizer.nextToken());
        int windowLength = Integer.parseInt(tokenizer.nextToken());

        tokenizer = new StringTokenizer(reader.readLine());

        int[] array = new int[firstArrayLength];

        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.parseInt(tokenizer.nextToken());
        }
        System.out.println(MaxWindowSum.maxWindow(windowLength, array));

    }
}
