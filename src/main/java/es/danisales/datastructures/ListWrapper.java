package es.danisales.datastructures;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListWrapper<T> implements List<T> {
    private final List<T> listAdapter;

    protected ListWrapper(List<T> listAdapter) {
        this.listAdapter = listAdapter;
    }

    public static <T> ListWrapper of(List<T> l) {
        return new ListWrapper<>(l);
	}

	@Override
	public boolean add(T e) {
        return listAdapter.add(e);
	}

	@Override
	public void add(int index, T element) {
        listAdapter.add(index, element);
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
	public void clear() {
        listAdapter.clear();
	}

	@Override
	public boolean contains(Object o) {
        return listAdapter.contains(o);
	}

	@Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return listAdapter.containsAll(c);
	}

	@Override
	public T get(int index) {
        return listAdapter.get(index);
	}

	@Override
	public int indexOf(Object o) {
        return listAdapter.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
        return listAdapter.isEmpty();
	}

	@Override
    @NonNull
	public Iterator<T> iterator() {
        return listAdapter.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
        return listAdapter.lastIndexOf(o);
	}

	@Override
    @NonNull
	public ListIterator<T> listIterator() {
        return listAdapter.listIterator();
	}

	@Override
    @NonNull
	public ListIterator<T> listIterator(int index) {
        return listAdapter.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
        return listAdapter.remove(o);
	}

	@Override
	public T remove(int index) {
        return listAdapter.remove(index);
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
	public T set(int index, T element) {
        return listAdapter.set(index, element);
	}

	@Override
	public int size() {
        return listAdapter.size();
	}

	@Override
    @NonNull
	public List<T> subList(int fromIndex, int toIndex) {
        return listAdapter.subList(fromIndex, toIndex);
	}

	@Override
    @NonNull
	public Object[] toArray() {
        return listAdapter.toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    @NonNull
    public <TT> TT[] toArray(@NonNull TT[] a) {
        return listAdapter.toArray(a);
    }
}
