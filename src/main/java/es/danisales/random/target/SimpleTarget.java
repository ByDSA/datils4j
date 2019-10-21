package es.danisales.random.target;

import static com.google.common.base.Preconditions.checkArgument;

public class SimpleTarget implements Target<SimpleTarget> {
    private long surface = 1;

    public SimpleTarget() {
    }

    @Override
    public SimpleTarget pickDart(long dart) {
        return pick();
    }

    @Override
    public SimpleTarget pick() {
        return this;
    }

    @Override
    public final long getSurface() {
        return surface;
    }

    @SuppressWarnings("unused")
    public final void setSurface(long s) {
        checkArgument(s >= 0, "Surface must be greater or equals than 0");
        surface = s;
    }

    @Override
    public void onRollDice() {
    }
}
