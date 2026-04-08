package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataProcessor {

    public static String[] processPipeline(String[] input, int k) {
        List<Integer> numbers = new ArrayList<>();
        for (String s : input) {
            try {
                numbers.add(Integer.parseInt(s.trim()));
            } catch (NumberFormatException e) {
            }
        }

        processVariant16(numbers, k);

        return numbers.stream().map(String::valueOf).toArray(String[]::new);
        //элемент в строку и массив
    }

    public static void processVariant16(List<Integer> list, int k) {
        if (list == null || list.isEmpty()) return;

        int n = list.size();
        k = k % n;
        if (k < 0) k += n; //влево на 1 = вправо на 9

        if (k == 0) return;

        reverse(list, 0, n - 1);
        reverse(list, 0, k - 1);
        reverse(list, k, n - 1);
    }

    private static void reverse(List<Integer> list, int start, int end) {
        while (start < end) {
            Integer temp = list.get(start);
            list.set(start, list.get(end));
            list.set(end, temp);
            start++;
            end--;
        }
    }
}