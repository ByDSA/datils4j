package es.danisales.utils;

import java.util.function.Supplier;

public interface Bean<T> extends Supplier<T> {
    void set(T v);
}
