package es.danisales.random.target;

@SuppressWarnings("unused")
public class RandomPicker1DRemover<T> extends RandomPicker1D<T> {
    @Override
    public void afterOnPick(T picked) {
        int index = indexOf(picked);
        remove(index);
    }
}
