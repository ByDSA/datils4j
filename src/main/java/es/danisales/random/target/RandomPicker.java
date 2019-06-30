package es.danisales.random.target;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

public abstract class RandomPicker<T, RET> extends ArrayList<T> implements Target<RET> {
    private static Random rand;
    static {
        setNormalRandom();
    }

    @SuppressWarnings("unused")
    public static void setSecureRandom() {
        rand = new SecureRandom();
    }

    @SuppressWarnings("WeakerAccess")
    public static void setNormalRandom() {
        rand = new Random();
    }

    static long rand(long max) {
        return ( rand.nextLong() & Long.MAX_VALUE ) % max;
    }

    @Override
    public RET pick() {
        if (size() == 0)
            throw new RandomPicker.EmptyException();

        beforeOnPick();

        long dart = rand(size());

        RET t = pickDart(dart);
        afterOnPick(t);
        return t;
    }

    public static class EmptyException extends RuntimeException {
        @SuppressWarnings("WeakerAccess")
        public EmptyException() {
            super("PackTarget vacío");
        }
    }
}
