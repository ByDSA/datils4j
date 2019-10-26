package es.danisales.datastructures;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class ListMap<ID, T> extends ListProxy<Map.Entry<ID, T>> {
    Map<ID, Integer> mapKeyPosition = new LinkedHashMap<>();
    private Set<Map.Entry<ID, T>> _entrySet = new HashSet<>();
    private List<T> _values = new ArrayList<>();

    public ListMap() {
        super(new ArrayList<>());
    }

    public boolean contains(ID key, T value) {
        return contains(new MutablePair<>(key, value));
    }

    @SuppressWarnings("WeakerAccess")
    public boolean containsKey(ID key) {
        return mapKeyPosition.containsKey(key);
    }

    @SuppressWarnings({"WeakerAccess"})
    public boolean containsValue(T value) {
        return _values.contains(value);
    }

    public T getByKey(ID key) {
        Integer index = mapKeyPosition.get(key);
        if (index == null)
            return null;

        return get(index).getValue();
    }

    @Override
    @NonNull
    public ListIterator<Map.Entry<ID, T>> iterator() {
        return listIterator();
    }

    @Override
    public boolean add(Map.Entry<ID, T> entry) {
        put(entry.getKey(), entry.getValue());
        return true;
    }

    @SuppressWarnings("WeakerAccess")
    public T setValue(int index, T v) {
        Map.Entry<ID, T> entry = get(index);
        return entry.setValue(v);
    }

    public T put(ID id, T t) {
        Integer index = mapKeyPosition.get(id);

        T oldValue = null;
        if (index != null) {
            Map.Entry<ID, T> entry = get(index);
            oldValue = entry.getValue();
            entry.setValue(t);
        } else {
            Map.Entry<ID, T> entry = new MutablePair<>(id, t);
            _entrySet.add(entry);
            _values.add(entry.getValue());
            mapKeyPosition.put(entry.getKey(), size());
            super.add(entry);
        }

        return oldValue;
    }

    @SuppressWarnings("WeakerAccess")
    public T removeByKey(ID key) {
        Integer oldIndex = mapKeyPosition.get(key);
        if (oldIndex == null)
            return null;
        Map.Entry<ID, T> oldEntry = remove(oldIndex.intValue());
        T oldValue = oldEntry.getValue();

        updateMapValuesFrom(oldIndex);

        return oldValue;
    }

    @Override
    public Map.Entry<ID, T> remove(int index) {
        Map.Entry<ID, T> oldEntry = super.remove(index);
        _entrySet.remove(oldEntry);
        _values.remove(oldEntry.getValue());
        mapKeyPosition.remove(oldEntry.getKey());

        return oldEntry;
    }

    private void updateMapValuesFrom(int fromIndex) {
        for (int i = fromIndex; i < size(); i++) {
            mapKeyPosition.put(get(i).getKey(), i);
        }
    }

    @Override
    public void clear() {
        super.clear();
        mapKeyPosition.clear();
    }

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public Set<ID> keySet() {
        return mapKeyPosition.keySet();
    }

    @NonNull
    public Collection<T> values() {
        return _values;
    }

    @NonNull
    public Set<Map.Entry<ID, T>> entrySet() {
        return _entrySet;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int indexOf(Object entry) {
        Map.Entry<ID, T> entryCasted;
        try {
            entryCasted = (Map.Entry<ID, T>) entry;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Parameter 'entry' has to be 'Map.Entry' extended type; was " + entry.getClass());
        }
        int i = indexOfKey(entryCasted.getKey());
        if (i == -1 || !entryCasted.getValue().equals(get(i).getValue()))
            return -1;

        return i;
    }

    @SuppressWarnings("WeakerAccess")
    public int indexOfKey(ID key) {
        Integer i = mapKeyPosition.get(key);
        if (i == null)
            return -1;

        return i;
    }

    @Override
    public int lastIndexOf(Object o) {
        return indexOf(o); // Como 'key' es unique, sólo puede aparecer una vez.
    }

    @SuppressWarnings("WeakerAccess")
    public int lastIndexOfValue(T o) {
        for (int i = size() - 1; i >= 0; i--) {
            Map.Entry<ID, T> entry = get(i);
            if (entry.getValue().equals(o))
                return i;
            i--;
        }

        return -1;
    }

    @NonNull
    public ListIterator<Map.Entry<ID, T>> listIteratorByKey(ID key) {
        return listIterator(mapKeyPosition.get(key));
    }
}
