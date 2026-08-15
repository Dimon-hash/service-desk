package com.example.service_desk.algorithm.old;

import java.util.ArrayList;
import java.util.List;

public class Merge {
    public static List<Integer> merge(int[] numbers1, int[] numbers2) {
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        for (int number : numbers1) list1.add(number);
        for (int number : numbers2) list2.add(number);

        List<Integer> listResult = new ArrayList<>();
        int indexFirst = 0;
        int indexSecond = 0;

        for (int index = 0; index < list1.size()+list2.size(); index++) {

            if (indexFirst >= list1.size()) {
                listResult.add(list2.get(indexSecond++));
            } else if (indexSecond >= list2.size()) {
                listResult.add(list1.get(indexFirst++));
            } else if (list1.get(indexFirst) <= list2.get(indexSecond)) {
                listResult.add(list1.get(indexFirst++));
            } else {
                listResult.add(list2.get(indexSecond++));
            }
        }
        return listResult;
    }
}
