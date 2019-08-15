package es.danisales.random.target;

import es.danisales.datastructures.InOutConversion;
import es.danisales.random.RandomMode;

import java.security.SecureRandom;
import java.util.Random;

abstract class RandomPickerImp<RET> extends InOutConversion<RET, SimpleTargetBean<RET>> implements RandomPicker<RET> {
    private Random rand;

    RandomPickerImp(RandomPickerBuilder builder) {
        setRandomMode(builder.randomMode);
    }

    @Override
    public SimpleTargetBean<RET> createInternal(RET ret) {
        return new SimpleTargetBean<>(ret);
    }

    @SuppressWarnings("unused")
    public void setRandomMode(RandomMode randomMode) {
        switch (randomMode) {
            case Normal:
                if (rand instanceof SecureRandom)
                    rand = new Random();
                break;
            case Secure:
                if (!(rand instanceof SecureRandom))
                    rand = new SecureRandom();
                break;
        }
    }

    long randUntil(long max) {
        return (rand.nextLong() & Long.MAX_VALUE) % max;
    }

    void checkNotEmpty() {
        if (size() == 0)
            throw new IllegalStateException("PackTarget vacío");
    }

    @Override
    public RET pick() {
        checkNotEmpty();

        long dart = randUntil(size());

        RET t = pickDart(dart);
        return t;
    }
}
