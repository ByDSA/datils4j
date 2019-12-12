package es.danisales.utils;

public final class PrimitiveTypes {
    private PrimitiveTypes() {
    }

    public static boolean[] getPrimitiveArrayFrom(Boolean[] array) {
        boolean[] ret = new boolean[array.length];
        int i = 0;
        for (boolean b : array)
            ret[i++] = b;

        return ret;
    }

    public static int[] getPrimitiveArrayFrom(Integer[] array) {
        int[] ret = new int[array.length];
        int i = 0;
        for (int b : array)
            ret[i++] = b;

        return ret;
    }
}
