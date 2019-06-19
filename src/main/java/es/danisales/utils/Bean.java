package es.danisales.utils;

public interface Bean<T> {
    void setValue(T v);
    T getValue();
}
