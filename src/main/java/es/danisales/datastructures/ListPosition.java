package es.danisales.datastructures;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListPosition<T> extends ListProxy<T> implements List<T> {
    private Map<T, Integer> mapPosition = new LinkedHashMap<>();

    protected ListPosition() {
        super(new ArrayList<T>());
    }

    @Override
    public boolean add(T entry) {
        mapPosition.put(entry, size());
        return super.add(entry);
    }

    @Override
    public int indexOf(Object entry) {
        return mapPosition.get(entry);
    }

    @Override
    public T remove(int index) {
        T oldEntry = super.remove(index);
        mapPosition.remove(oldEntry);

        return oldEntry;
    }

    @Override
    public void clear() {
        mapPosition.clear();
        super.clear();
    }
}
