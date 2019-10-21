package es.danisales.random.target;

import es.danisales.random.RandomMode;
import es.danisales.utils.building.Builder;

import java.util.ArrayList;
import java.util.Collection;

public class RandomPickerBuilder<T> extends Builder {
    RandomMode randomMode = RandomMode.Normal;
    private boolean isRemoveOnPick = false;
    private boolean isSurfaceVariable = false;
    private Collection<T> fromList;

    RandomPickerBuilder() {
    } // Sólo se puede llamar desde RandomPicker.builder()

    public RandomPicker<T> build() {
        RandomPicker<T> ret;
        if (isRemoveOnPick)
            if (isSurfaceVariable)
                throw new RuntimeException("No existe");
            else
                ret = new RandomPicker1DRemover<>(this);
        else {
            if (isSurfaceVariable)
                ret = new RandomPicker2D(this); // Warning: si T no extiende de Target, dará error
            else
                ret = new RandomPicker1D<>(this);
        }

        if (fromList != null)
            ret.addAll(fromList);

        return ret;
    }

    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public RandomPickerBuilder<T> removeOnPick() {
        isRemoveOnPick = true;

        return self();
    }

    public RandomPickerBuilder<T> surfaceVariable() {
        isSurfaceVariable = true;

        return self();
    }

    public RandomPickerBuilder<T> from(Collection<T> list) {
        if (fromList == null)
            fromList = new ArrayList<>();

        fromList.addAll(list);

        return self();
    }

    protected RandomPickerBuilder<T> self() {
        return this;
    }

    public RandomPickerBuilder<T> setRandomMode(RandomMode randomMode) {
        this.randomMode = randomMode;

        return self();
    }

}
