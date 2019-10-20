package es.danisales.utils.datastructures;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class ListMap<ID, T> implements Map<ID, T>, Iterable<Map.Entry<ID, T>> {
    List<Map.Entry<ID, T>> list = new ArrayList<>();
    Map<ID, T> map = new HashMap<>();

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public T get(Object key) {
        return map.get(key);
    }

    @Override
    @NonNull
    public ListMapIterator<ID, T> iterator() {
        return new ListMapIterator<>(this);
    }

    public T setIndex(int index, T v) {
        return list.get(index).setValue(v);
    }

    @Override
    public T put(ID id, T t) {
        list.add(new Pair<>(id, t));
        return map.put(id, t);
    }

    @Override
    public T remove(Object o) {
        T oldValue = map.remove(o);
        list.remove(new Pair<>(o, oldValue));

        return oldValue;
    }

    @Override
    public void putAll(@NonNull Map<? extends ID, ? extends T> m) {
        for (Map.Entry<? extends ID, ? extends T> entry : m.entrySet())
            list.add(new Pair<>(entry.getKey(), entry.getValue()));

        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
        list.clear();
    }

    @Override
    @NonNull
    public Set<ID> keySet() {
        return map.keySet();
    }

    @Override
    @NonNull
    public Collection<T> values() {
        return map.values();
    }

    @Override
    @NonNull
    public Set<Map.Entry<ID, T>> entrySet() {
        return map.entrySet();
    }

    public int indexOf(T o) {
        return list.indexOf(o);
    }

    public int lastIndexOf(T o) {
        return list.lastIndexOf(o);
    }

    public Map.Entry<ID, T> getIndex(int index) {
        return list.get(index);
    }

    public Map.Entry<ID, T> removeIndex(int index) {
        Map.Entry<ID, T> ret = list.remove(index);
        map.remove(ret.getKey());

        return ret;
    }


}
