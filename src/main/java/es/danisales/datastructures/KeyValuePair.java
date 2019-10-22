package es.danisales.datastructures;

import es.danisales.others.Keyable;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class KeyValuePair<K, V> implements Serializable, Keyable<K>, Map.Entry<K, V> {
    private K key;
    private V value;

    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
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
        if (o instanceof KeyValuePair) {
            KeyValuePair pair = (KeyValuePair) o;
            if (!Objects.equals(key, pair.key)) return false;
            return Objects.equals(value, pair.value);
        }
        return false;
    }
}

