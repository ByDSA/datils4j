package es.danisales.utils;

import java.util.ArrayList;

public class MathUtils {
    private MathUtils() {
    }

    public static float decimalPart(float f) {
        return f - (int) f;
    }

    public static int rotativeTrim(int n, int max) {
        n %= max;
        if (n < 0)
            n += max;

        return n;
    }

    public static byte[] dec2bytes(int n) {
        ArrayList<Byte> array = new ArrayList<>();

        while (n > 0) {
            byte b = (byte) (n & 0xff);
            array.add(0, b);
            n >>= 8;
        }

        byte[] bytes = new byte[array.size()];
        for (int i = 0; i < array.size(); i++) {
            bytes[i] = array.get(i);
        }

        return bytes;
    }

}
