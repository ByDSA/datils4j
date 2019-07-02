package es.danisales.random.target;

public class SimpleTarget implements Target<SimpleTarget> {
    private long surface = 1;

    public SimpleTarget() {
    }

    @Override
    public void beforeOnPick() {
    }

    @Override
    public void afterOnPick(SimpleTarget t) {
    }

    @Override
    public SimpleTarget pickDart(long dart) {
        return pick();
    }

    @Override
    public SimpleTarget pick() {
        beforeOnPick();
        afterOnPick(this);
        return this;
    }

    @Override
    public final long getSurface() {
        return surface;
    }

    @SuppressWarnings("unused")
    public final void setSurface(long s) {
        surface = Math.max(0, s);
    }

    @Override
    public void next() {
    }
}
