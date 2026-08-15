package com.example.service_desk.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class PrefixSumsArraysRunner {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
        int arrayLength = Integer.parseInt(tokenizer.nextToken());
        long target = Long.parseLong(tokenizer.nextToken());

        long[] array = new long[arrayLength];
        tokenizer = new StringTokenizer(reader.readLine());

        for (int i = 0; i < array.length; i++) {
            array[i] = Long.parseLong(tokenizer.nextToken());
        }

        System.out.println(PrefixSumsArrays.countTarget(array, target));
    }
}
