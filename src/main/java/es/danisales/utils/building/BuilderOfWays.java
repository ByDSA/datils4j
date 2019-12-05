package es.danisales.utils.building;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface BuilderOfWays<T> {
    @NonNull
    default T build() throws BuildingException {
        for (Class<? extends BuildingWay<T>> c : getBuildingWaysClasses()) {
            try {
                Constructor<? extends BuildingWay<T>> constructor = c.getDeclaredConstructor(this.getClass());
                constructor.setAccessible(true);
                BuildingWay<T> b = constructor.newInstance(this);
                if (b.isReadyToBuild())
                    return b.build();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
            }
        }

        throw new RuntimeException("There's no way to create the ChromaticChord. More arguments are needed.");
    }

    Iterable<Class<? extends BuildingWay<T>>> getBuildingWaysClasses();
}
