package com.example.service_desk.algorithm.old;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        int n = Integer.parseInt(reader.readLine());
        int k = Integer.parseInt(reader.readLine());
        StringTokenizer tokenizer = new StringTokenizer(reader.readLine());

        Deque<Integer> deque = new ArrayDeque<>();
        int[] arr = new int[n];
        int[] answers = new int[n - k + 1];
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(tokenizer.nextToken());
        }
        for (int i = 0; i < n; i++) {

            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            while (!deque.isEmpty() && arr[deque.peekLast()] <= arr[i]) {
                deque.pollLast();
            }
            deque.offerLast(i);
            if (i >= k - 1) {
                answers[i - k + 1] = arr[deque.peekFirst()];
            }
        }
        for (int i = 0; i < answers.length; i++) {
            if (i > 0) {
                writer.write(" ");
            }
            writer.write(String.valueOf(answers[i]));
        }
        writer.newLine();

        reader.close();
        writer.close();
    }
}
