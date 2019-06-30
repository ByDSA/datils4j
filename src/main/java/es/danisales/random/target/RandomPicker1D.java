package es.danisales.random.target;

public class RandomPicker1D<T> extends RandomPicker<T, T> {
    @Override
    public T pickDart(long dart) {
        return get((int)dart);
    }

    @Override
    public long getSurface() {
        return size();
    }

    @Override
    public void beforeOnPick() {
    }

    @Override
    public void afterOnPick(T picked) {
    }

    @Override
    public void next() {
    }
}
