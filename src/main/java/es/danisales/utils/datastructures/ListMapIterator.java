package es.danisales.utils.datastructures;

import es.danisales.others.Keyable;

import java.util.ListIterator;
import java.util.Map;
import java.util.function.Supplier;

public class ListMapIterator<ID, T> implements Supplier<Map.Entry<ID, T>>, ListIterator<Map.Entry<ID, T>> {
    private ListMap<ID, T> reference;
    private boolean cyclic;
    private int cursor = -1;

    ListMapIterator(ListMap<ID, T> reference) {
        this.reference = reference;
    }

    public boolean hasNext() {
        return cursor != nextIndex(); // TODO: no sirve para random
    }

    @Override
    public Map.Entry<ID, T> get() {
        return reference.getIndex(cursor);
    }


    public ID getKey() {
        return reference.list.get(cursor).getKey();
    }

    public boolean goTo(ID id) {
        T obj = reference.get(id);
        int index = reference.indexOf(obj);

        return goToIndex(index);
    }

    public boolean goToIndex(int index) {
        if (index > 0 && index < reference.size() - 1) {
            cursor = index;
            return true;
        } else
            return false;
    }

    public Map.Entry<ID, T> next() {
        cursor = nextIndex();

        return get();
    }

    public boolean hasPrevious() {
        return cursor != previousIndex(); // TODO: no sirve para random
    }

    public Map.Entry<ID, T> previous() {
        cursor = previousIndex();
        return get();
    }

    public int nextIndex() {
        int cursorTmp = cursor + 1;
        if (cursorTmp >= reference.size())
            if (cyclic)
                cursorTmp = 0;
            else
                cursorTmp = reference.size();

        return cursorTmp;
    }

    public int previousIndex() {
        int cursorTmp = cursor - 1;
        if (cursorTmp < 0)
            if (cyclic)
                cursorTmp = reference.size() - 1;
            else
                cursorTmp = -1;

        return cursorTmp;
    }

    public void remove() {
        reference.removeIndex(cursor);
    }

    @Override
    public void set(Map.Entry<ID, T> entry) {
        reference.map.put(entry.getKey(), entry.getValue());
        reference.list.set(cursor, entry);
    }

    @Override
    public void add(Map.Entry<ID, T> entry) {
        reference.map.put(entry.getKey(), entry.getValue());
        reference.list.add(cursor, entry);
    }

    public void setValue(T t) {
        Map.Entry<ID, T> entry = reference.list.get(cursor);
        entry.setValue(t);
        reference.map.put(entry.getKey(), t);
    }

    public void add(ID key, T t) {
        reference.list.add(new Pair<>(key, t));
        reference.map.put(key, t);
    }

    public class Builder<ID, T extends Keyable<ID>> extends es.danisales.utils.building.Builder<Builder<ID, T>, ListMapIterator<ID, T>> {
        @Override
        public ListMapIterator<ID, T> build() {
            return null;
        }

        @Override
        protected Builder self() {
            return null;
        }
    }
}
