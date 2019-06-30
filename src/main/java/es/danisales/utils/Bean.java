package es.danisales.utils;

public interface Bean<T> extends Valuable<T> {
    void setValue(T v);
}
