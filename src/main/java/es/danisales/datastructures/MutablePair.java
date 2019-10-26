package es.danisales.datastructures;

import es.danisales.others.Keyable;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class MutablePair<K, V> implements Serializable, Keyable<K>, Map.Entry<K, V> {
    private K key;
    private V value;

    public MutablePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> MutablePair<K, V> from(Pair<K, V> pair) {
        return new MutablePair<>(pair.getKey(), pair.getValue());
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V newValue) {
        V old = value;
        value = newValue;
        return old;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public int hashCode() {
        // name's hashCode is multiplied by an arbitrary prime number (13)
        // in order to make sure there is a difference in the hashCode between
        return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof MutablePair) {
            MutablePair pair = (MutablePair) o;
            if (!Objects.equals(key, pair.key)) return false;
            return Objects.equals(value, pair.value);
        }
        return false;
    }

    @SuppressWarnings("WeakerAccess")
    public Pair<K, V> getInmutable() {
        return new Pair<>(key, value);
    }
}

