package ru.romanisupov.utils;

import java.util.ArrayList;
import java.util.List;

public class Sorter {

    private Sorter() {
        throw new IllegalStateException("Utility class");
    }

    private static class PartitionedArraySorter extends Thread {
        private final int[] array;

        private PartitionedArraySorter(final int[] array) {
            this.array = array;
        }

        @Override
        public void run() {
            quickSort(array, 0, array.length - 1);
        }
    }

    public static void sort(final int[] array, final int threadsAmount) throws IllegalArgumentException {
        System.out.println("Sorting array");
        if (threadsAmount > array.length) {
            throw new IllegalArgumentException("threadsAmount > n");
        }
        if (threadsAmount == 1) {
            quickSort(array, 0, array.length - 1);
            return;
        }
        List<int[]> arrays = split(array, threadsAmount);
        sortArrays(arrays);
        int[] sortedArray = unite(arrays);
        System.arraycopy(sortedArray, 0, array, 0, array.length);
    }

    private static List<int[]> split(final int[] array, final int threadsAmount) {
        List<int[]> arrays = new ArrayList<>();
        int arrayLength = array.length;
        int partitionLength = arrayLength / threadsAmount;
        for (int i = 0; i < threadsAmount - 1; i++) {
            int[] arrayPartition = new int[partitionLength];
            System.arraycopy(array, i * partitionLength, arrayPartition, 0, partitionLength);
            arrays.add(arrayPartition);
        }
        int remainedLength = arrayLength - (threadsAmount - 1) * partitionLength;
        int[] arrayPartition = new int[remainedLength];
        System.arraycopy(array, (threadsAmount - 1) * partitionLength, arrayPartition, 0, remainedLength);
        arrays.add(arrayPartition);
        return arrays;
    }

    private static void sortArrays(final List<int[]> arrays) {
        for (int[] array : arrays) {
            PartitionedArraySorter sorter = new PartitionedArraySorter(array);
            sorter.start();
            try {
                sorter.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static int[] unite(final List<int[]> arrays) {
        while (arrays.size() > 1) {
            int[] mergedArray = merge(arrays.get(0), arrays.get(1));
            arrays.set(0, mergedArray);
            arrays.remove(1);
        }
        return arrays.get(0);
    }

    private static void quickSort(final int[] array, final int begin, final int end) {
        if (begin < end) {
            int partitionIndex = partition(array, begin, end);
            quickSort(array, begin, partitionIndex - 1);
            quickSort(array, partitionIndex + 1, end);
        }
    }

    private static int partition(final int[] array, final int begin, final int end) {
        int pivot = array[end];
        int i = begin - 1;
        for (int j = begin; j < end; j++) {
            if (array[j] <= pivot) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, end);
        return i + 1;
    }

    private static void swap(final int[] array, final int position1, final int position2) {
        int temp = array[position1];
        array[position1] = array[position2];
        array[position2] = temp;
    }

    private static int[] merge(final int[] array1, final int[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        int[] mergedArray = new int[length1 + length2];
        int i1 = 0;
        int i2 = 0;
        int i = 0;
        while (i1 < length1 && i2 < length2) {
            if (array1[i1] <= array2[i2]) {
                mergedArray[i] = array1[i1];
                i1++;
            }
            else {
                mergedArray[i] = array2[i2];
                i2++;
            }
            i++;
        }
        while (i1 < length1) {
            mergedArray[i] = array1[i1];
            i++;
            i1++;
        }
        while (i2 < length2) {
            mergedArray[i] = array2[i2];
            i++;
            i2++;
        }
        return mergedArray;
    }
}
