package es.danisales.random.target;

import es.danisales.datastructures.ListCrossFactory;
import es.danisales.random.RandomMode;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.security.SecureRandom;
import java.util.Random;

import static com.google.common.base.Preconditions.checkState;

abstract class RandomPickerImp<RET> extends ListCrossFactory<SimpleTargetBean<RET>, RET> implements RandomPicker<RET> {
    private Random rand;

    RandomPickerImp(RandomPickerBuilder builder) {
        setRandomMode(builder.randomMode);
    }

    @Override
    public SimpleTargetBean<RET> createInternal(RET out) {
        return new SimpleTargetBean<>(out);
    }

    @Override
    public RET createExternal(SimpleTargetBean<RET> in) {
        return in.get();
    }

    @SuppressWarnings("unused")
    public void setRandomMode(@NonNull RandomMode randomMode) {
        switch (randomMode) {
            case Normal:
                if (!(rand instanceof SecureRandom))
                    rand = new Random();
                break;
            case Secure:
                if (!(rand instanceof SecureRandom))
                    rand = new SecureRandom();
                break;
        }
    }

    void checkNotEmpty() {
        checkState(size() != 0);
    }

    long randUntil(long max) {
        checkState(rand != null);
        return (rand.nextLong() & Long.MAX_VALUE) % max;
    }

    @Override
    public RET pick() {
        checkState(size() != 0, "PackTarget vacío");

        long dart = randUntil(size());

        return pickDart(dart);
    }
}
