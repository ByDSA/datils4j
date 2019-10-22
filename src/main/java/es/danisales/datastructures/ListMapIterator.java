package es.danisales.datastructures;

import java.util.ListIterator;
import java.util.Map;
import java.util.function.Supplier;

@Deprecated
public class ListMapIterator<ID, T> implements Supplier<Map.Entry<ID, T>>, ListIterator<Map.Entry<ID, T>> {
    private ListMap<ID, T> reference;
    private boolean cyclic = false;
    private int cursor = -1;

    ListMapIterator(ListMap<ID, T> reference) {
        this.reference = reference;
    }

    public boolean hasNext() {
        return cursor != nextIndex();
    }

    public void setCyclic(boolean cyclic) {
        this.cyclic = cyclic;
    }

    @Override
    public Map.Entry<ID, T> get() {
        return reference.get(cursor);
    }


    public ID getKey() {
        return reference.get(cursor).getKey();
    }

    public boolean goTo(ID id) {
        int index = reference.indexOf(id);

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
        reference.remove(cursor);
    }

    @Override
    public void set(Map.Entry<ID, T> entry) {
        reference.set(cursor, entry);
    }

    @Override
    public void add(Map.Entry<ID, T> entry) {
        //reference.mapKeyPosition.put(entry.getKey(), entry.getValue());
        reference.add(cursor, entry);
    }
/**
 public void setValue(T t) {
 Map.Entry<ID, T> entry = reference.list.get(cursor);
 entry.setValue(t);
 reference.mapKeyPosition.put(entry.getKey(), t);
 }

 public void put(ID key, T t) {
 reference.list.put(new Pair<>(key, t));
 reference.mapKeyPosition.put(key, t);
 }*/
}
