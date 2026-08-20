package com.example.service_desk.algorithm;

public class LinkedList {
    private Node head;
    private int size = 0;

    private static class Node {
        int value;
        Node next;
    }

    void addFirst(int value) {
        Node node = new Node();
        node.value = value;
        node.next = head;
        head = node;
        size++;
    }

    void addLast(int value) {
        if (head == null) {
            head = new Node();
            head.value = value;
            size++;
            return;
        }
        Node node = new Node();
        node.value = value;
        Node temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = node;
        size++;
    }

    int get(int index) {
        if (head == null) {
            throw new IndexOutOfBoundsException("");
        }

        if (index < 0) {
            throw new IndexOutOfBoundsException("index out of bound");
        }
        Node temp = head;
        while (index > 0) {
            temp = temp.next;
            if (temp == null) {
                throw new IndexOutOfBoundsException("index out of bound");
            }
            index--;
        }
        return temp.value;
    }

    int remove(int index) {
        if (head == null) {
            throw new IndexOutOfBoundsException("");
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("index out of bound");
        }
        Node temp = head;
        Node prev = null;
        while (index > 0) {
            prev = temp;
            temp = temp.next;
            if (temp == null) {
                throw new IndexOutOfBoundsException("index out of bound");
            }
            index--;
        }
        if (prev == null) {
            head = head.next;
            size--;
            return temp.value;

        }
        prev.next = temp.next;
        size--;
        return temp.value;

    }

    int size() {
        return size;
    }

}
