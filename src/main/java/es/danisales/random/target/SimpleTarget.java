package es.danisales.random.target;

public class SimpleTarget implements Target {
    private long surface = 1;

    public SimpleTarget() {
    }

    @Override
    public void beforeOnPick() {
    }

    @Override
    public void afterOnPick() {
    }

    @Override
    public SimpleTarget pickDart(long dart) {
        return pick();
    }

    @Override
    public SimpleTarget pick() {
        beforeOnPick();
        afterOnPick();
        return this;
    }

    @Override
    public final long getSurface() {
        return surface;
    }

    @SuppressWarnings("unused")
    public final void setSurface(long s) {
        surface = s;
    }

    @Override
    public void next() {
    }
}
