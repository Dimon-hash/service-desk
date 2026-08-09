package com.example.service_desk.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class PairSumRunner {

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer header = new StringTokenizer(reader.readLine());

        int count = Integer.parseInt(header.nextToken());
        int target = Integer.parseInt(header.nextToken());

        StringTokenizer values = new StringTokenizer(reader.readLine());

        int[] numbers = new int[count];

        for (int index = 0; index < count; index++) {
            numbers[index] = Integer.parseInt(values.nextToken());
        }

        boolean result = PairSum.hasPairWithSum(numbers, target);
        System.out.println(result ? "YES" : "NO");
    }
}
