package es.danisales.io.text.csv;

import es.danisales.others.Keyable;
import es.danisales.utils.datastructures.Pair;

class KeyValueLine<K, V> extends Pair<K, V> implements Keyable<K> {
    KeyValueLine(K key, V v) {
        super(key, v);
    }
}
