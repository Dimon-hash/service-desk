package com.example.service_desk.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class MergeRunner {
    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer header = new StringTokenizer(reader.readLine());

        int countFirstArray = Integer.parseInt(header.nextToken());
        int countSecondArray = Integer.parseInt(header.nextToken());

        StringTokenizer values = new StringTokenizer(reader.readLine());

        int[] numbers1 = new int[countFirstArray];

        for (int index = 0; index < countFirstArray; index++) {
            numbers1[index] = Integer.parseInt(values.nextToken());
        }

        values =  new StringTokenizer(reader.readLine());

        int []  numbers2 = new int[countSecondArray];
        for (int index = 0; index < countSecondArray; index++) {
            numbers2[index] = Integer.parseInt(values.nextToken());
        }

        System.out.println(Merge.merge(numbers1, numbers2));
    }
}
