package es.danisales.io.text.csv;

import es.danisales.datastructures.KeyValuePair;
import es.danisales.others.Keyable;

class KeyValueLine<K, V> extends KeyValuePair<K, V> implements Keyable<K> {
    KeyValueLine(K key, V v) {
        super(key, v);
    }
}
