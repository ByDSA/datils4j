package es.danisales.random.target;

class RandomPicker1DRemover<T> extends RandomPicker1D<T> {
    RandomPicker1DRemover(RandomPickerBuilder builder) {
        super(builder);
    }

    @Override
    public T pick() {
        T picked = super.pick();
        int index = indexOf(picked);
        remove(index);
        return picked;
    }
}
