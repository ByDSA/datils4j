package es.danisales.random.target;

import static com.google.common.base.Preconditions.checkState;

class RandomPicker1DRemover<T> extends RandomPicker1D<T> {
    RandomPicker1DRemover(RandomPickerBuilder builder) {
        super(builder);
    }

    @Override
    public T pick() {
        T picked = super.pick();
        int index = indexOf(picked);
        checkState(index != -1);
        return remove(index);
    }
}
