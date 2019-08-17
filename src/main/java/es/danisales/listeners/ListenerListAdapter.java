package es.danisales.listeners;

import es.danisales.datastructures.ListAdapter;
import es.danisales.datastructures.ListAdapterThreadSafe;
import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

abstract class ListenerListAdapter<T> implements ListenerList<T> {
    final private Action.Mode modeConcurrency;
    private final List<T> listAdapter;

    @SuppressWarnings("unchecked")
    ListenerListAdapter(Action.Mode mode, Safety safety) {
        if (safety == Safety.NonThreadSafe)
            listAdapter = ListAdapter.of(new ArrayList<T>());
        else if (safety == Safety.ThreadSafe)
            listAdapter = ListAdapterThreadSafe.of(new ArrayList<T>());
        else
            throw new IllegalArgumentException();

        this.modeConcurrency = mode;
    }

    @Override
    public Action.Mode getMode() {
        return modeConcurrency;
    }

    @Override
    public int size() {
        return listAdapter.size();
    }

    @Override
    public boolean isEmpty() {
        return listAdapter.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return listAdapter.contains(o);
    }

    @Override
    public @NonNull Iterator<T> iterator() {
        return listAdapter.iterator();
    }

    @Override
    public @NonNull Object[] toArray() {
        return listAdapter.toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    @NonNull
    public <T1> T1[] toArray(@NonNull T1[] a) {
        return listAdapter.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return listAdapter.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return listAdapter.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return listAdapter.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        return listAdapter.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        return listAdapter.addAll(c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return listAdapter.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return listAdapter.retainAll(c);
    }

    @Override
    public void clear() {
        listAdapter.clear();
    }

    @Override
    public T get(int index) {
        return listAdapter.get(index);
    }

    @Override
    public T set(int index, T element) {
        return listAdapter.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        listAdapter.add(index, element);
    }

    @Override
    public T remove(int index) {
        return listAdapter.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return listAdapter.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return listAdapter.lastIndexOf(o);
    }

    @Override
    public @NonNull ListIterator<T> listIterator() {
        return listAdapter.listIterator();
    }

    @Override
    public @NonNull ListIterator<T> listIterator(int index) {
        return listAdapter.listIterator(index);
    }

    @Override
    public @NonNull List<T> subList(int fromIndex, int toIndex) {
        return listAdapter.subList(fromIndex, toIndex);
    }

    enum Safety {
        ThreadSafe, NonThreadSafe
    }
}
