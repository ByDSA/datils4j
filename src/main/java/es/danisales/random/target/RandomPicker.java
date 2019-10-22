package es.danisales.random.target;

import es.danisales.random.RandomMode;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Set;

public interface RandomPicker<RET> extends Target<RET>, Set<RET> {
    static <RET> @NonNull RandomPicker<RET> removeOnPickFrom(RET... values) {
        RandomPickerBuilder<RET> builder = new RandomPickerBuilder<>();
        builder.removeOnPick();
        RandomPicker<RET> picker = builder.build();
        picker.addAll(Arrays.asList(values));

        return picker;
    }

    @SuppressWarnings("unchecked")
    static <RET> @NonNull RandomPicker<RET> from(RET... values) {
        RandomPickerBuilder<RET> builder = (RandomPickerBuilder<RET>) builder(values[0].getClass());
        RandomPicker<RET> picker = builder.build();
        picker.addAll(Arrays.asList(values));
        return picker;
    }

    static <T> @NonNull RandomPickerBuilder<T> builder(@NonNull Class<T> tClass) {
        return new RandomPickerBuilder<>();
    }

    void setRandomMode(@NonNull RandomMode randomMode);
}
