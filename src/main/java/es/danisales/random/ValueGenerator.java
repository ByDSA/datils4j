package es.danisales.random;

import java.util.Random;

public class ValueGenerator {
    static Random rd = new Random(); // creating ValueGenerator object

    private ValueGenerator() {
    } // noninstantiable

    public static int[] integerArray(final int size) {
        int[] arr = new int[size];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rd.nextInt(); // storing random integers in an array
        }

        return arr;
    }

    public static long[] longArray(final int size) {
        long[] arr = new long[size];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rd.nextLong(); // storing random integers in an array
        }

        return arr;
    }
}
