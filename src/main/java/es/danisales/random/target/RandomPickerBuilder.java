package es.danisales.random.target;

import es.danisales.random.RandomMode;

public class RandomPickerBuilder<T> {
    RandomMode randomMode = RandomMode.Normal;
    private boolean isRemoveOnPick = false;
    private boolean isSurfaceVariable = false;

    public RandomPickerImp build() {
        if (isRemoveOnPick)
            if (isSurfaceVariable)
                throw new RuntimeException("No existe");
            else
                return new RandomPicker1DRemover<>(this);
        else {
            if (isSurfaceVariable)
                return new RandomPicker2D<>(this);
            else
                return new RandomPicker1D<>(this);
        }
    }

    @SuppressWarnings({"WeakerAccess"})
    public RandomPickerBuilder<T> removeOnPick() {
        isRemoveOnPick = true;

        return self();
    }

    public RandomPickerBuilder<T> surfaceVariable() {
        isSurfaceVariable = true;

        return self();
    }

    private RandomPickerBuilder<T> self() {
        return this;
    }

    public RandomPickerBuilder<T> setRandomMode(RandomMode randomMode) {
        this.randomMode = randomMode;

        return self();
    }

}