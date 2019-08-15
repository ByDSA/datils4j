package es.danisales.random.target;

class RandomPicker1D<T> extends RandomPickerImp<T> {
    RandomPicker1D(RandomPickerBuilder builder) {
        super(builder);
    }

    @Override
    public T pickDart(long dart) {
        return get((int)dart);
    }

    @Override
    public long getSurface() {
        return size();
    }

    @Override
    public void next() {
    }
}
