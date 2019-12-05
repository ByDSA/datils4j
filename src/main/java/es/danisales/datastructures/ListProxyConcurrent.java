package es.danisales.datastructures;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListProxyConcurrent<T> extends ListProxy<T> {
    public ListProxyConcurrent(List<T> listAdapter) {
        super(listAdapter);
    }

    @Override
    public synchronized boolean add(T e) {
        return super.add(e);
    }

    @Override
    public synchronized void add(int index, T element) {
        super.add(index, element);
    }

    @Override
    public synchronized boolean addAll(@NonNull Collection<? extends T> c) {
        return super.addAll(c);
    }

    @Override
    public synchronized boolean addAll(int index, @NonNull Collection<? extends T> c) {
        return super.addAll(index, c);
    }

    @Override
    public synchronized void clear() {
        super.clear();
    }

    @Override
    public synchronized boolean contains(Object o) {
        return super.contains(o);
    }

    @Override
    public synchronized boolean containsAll(@NonNull Collection<?> c) {
        return super.containsAll(c);
    }

    @Override
    public synchronized T get(int index) {
        return super.get(index);
    }

    @Override
    public synchronized int indexOf(Object o) {
        return super.indexOf(o);
    }

    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    @NonNull
    public synchronized Iterator<T> iterator() {
        return super.iterator();
    }

    @Override
    public synchronized int lastIndexOf(Object o) {
        return super.lastIndexOf(o);
    }

    @Override
    @NonNull
    public synchronized ListIterator<T> listIterator() {
        return super.listIterator();
    }

    @Override
    @NonNull
    public synchronized ListIterator<T> listIterator(int index) {
        return super.listIterator(index);
    }

    @Override
    public synchronized boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public synchronized T remove(int index) {
        return super.remove(index);
    }

    @Override
    public synchronized boolean removeAll(@NonNull Collection<?> c) {
        return super.removeAll(c);
    }

    @Override
    public synchronized boolean retainAll(@NonNull Collection<?> c) {
        return super.retainAll(c);
    }

    @Override
    public synchronized T set(int index, T element) {
        return super.set(index, element);
    }

    @Override
    public synchronized int size() {
        return super.size();
    }

    @Override
    @NonNull
    public synchronized List<T> subList(int fromIndex, int toIndex) {
        return super.subList(fromIndex, toIndex);
    }

    @Override
    @NonNull
    public synchronized Object[] toArray() {
        return super.toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    @NonNull
    public synchronized <TT> TT[] toArray(@NonNull TT[] a) {
        return super.toArray(a);
    }
}
