package es.danisales.random;

import java.util.Random;

public final class ValueGenerator {
    static Random rd = new Random(); // creating ValueGenerator object

    private ValueGenerator() {
    } // noninstantiable

    public final static class Array {
        private Array() {
        } // noninstantiable

        public static int[] integer(final int size) {
            int[] arr = new int[size];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = rd.nextInt(); // storing random integers in an array
            }

            return arr;
        }

        public static long[] longInt(final int size) {
            long[] arr = new long[size];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = rd.nextLong(); // storing random integers in an array
            }

            return arr;
        }
    }
}
