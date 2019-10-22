package es.danisales.datastructures;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class ListCrossFactory<IN, OUT> implements List<OUT> {
    private final List<IN> interalList = new ArrayList<>();
    private final List<OUT> externalList = new ArrayList<>();
    private final Map<IN, OUT> internalToExternalMap = new HashMap<>();
    private final Map<OUT, IN> externalToInternalMap = new HashMap<>();

    @Override
    public int size() {
        return externalList.size();
    }

    @Override
    public boolean isEmpty() {
        return externalList.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return externalList.contains(o);
    }

    @Override
    public OUT get(int i) {
        return externalList.get(i);
    }

    protected IN getInternal(int i) {
        return interalList.get(i);
    }

    @Override
    public OUT set(int index, OUT element) {
        IN in = createInternal(element);
        interalList.set(index, in);
        crossReference(element, in);
        return externalList.set(index, element);
    }

    private void crossReference(OUT external, IN internal) {
        internalToExternalMap.put(internal, external);
        externalToInternalMap.put(external, internal);
    }

    @Override
    public void add(int index, OUT element) {
        externalList.add(index, element);
        IN in = createInternal(element);
        interalList.add(index, in);
        crossReference(element, in);
    }

    @Override
    public OUT remove(int index) {
        interalList.remove(index);
        return externalList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return externalList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return externalList.lastIndexOf(o);
    }

    @Override
    @NonNull
    public ListIterator<OUT> listIterator() {
        return externalList.listIterator();
    }

    @Override
    @NonNull
    public ListIterator<OUT> listIterator(int index) {
        return externalList.listIterator(index);
    }

    @Override
    @NonNull
    public List<OUT> subList(int fromIndex, int toIndex) {
        return externalList.subList(fromIndex, toIndex);
    }

    @Override
    @NonNull
    public Iterator<OUT> iterator() {
        return externalList.iterator();
    }

    @Override
    public void forEach(@NonNull Consumer<? super OUT> action) {
        Objects.requireNonNull(action);
        externalList.forEach(action);
    }

    @Override
    @NonNull
    public Object[] toArray() {
        return externalList.toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    @NonNull
    public <T> T[] toArray(@NonNull T[] a) {
        Objects.requireNonNull(a);
        return externalList.toArray(a);
    }

    protected abstract IN createInternal(OUT out);

    protected abstract OUT createExternal(IN in);

    @Override
    public boolean add(OUT ret) {
        externalList.add(ret);
        IN in = createInternal(ret);
        interalList.add(in);
        crossReference(ret, in);
        return true;
    }

    protected boolean addInternal(IN in) {
        interalList.add(in);
        OUT out = createExternal(in);
        externalList.add(out);
        crossReference(out, in);
        return true;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        OUT oCasted = castOut(o);
        boolean removed = externalList.remove(oCasted);
        if (!removed)
            return false;

        IN in = externalToInternalMap.get(oCasted);
        interalList.remove(in);
        deleteCrossReference(in, oCasted);

        return true;
    }

    protected boolean removeInteral(@Nullable IN in) {
        boolean removed = interalList.remove(in);
        if (!removed)
            return false;

        OUT out = internalToExternalMap.get(in);
        externalList.remove(out);
        deleteCrossReference(in, out);

        return true;
    }

    private void deleteCrossReference(IN in, OUT out) {
        externalToInternalMap.remove(out);
        internalToExternalMap.remove(in);
    }

    @SuppressWarnings("unchecked")
    private OUT castOut(Object o) {
        try {
            return (OUT) o;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        Objects.requireNonNull(c);
        return externalList.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends OUT> c) {
        Objects.requireNonNull(c);
        for (OUT r : c)
            add(r);
        return true;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends OUT> c) {
        Objects.requireNonNull(c);

        for (OUT r : c)
            add(index++, r);
        return true;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        Objects.requireNonNull(c);
        boolean ret = false;
        for (int i = 0; i < size(); ) {
            OUT currentElement = externalList.get(i);
            if (externalList.contains(c)) {
                ret |= remove(currentElement);
            } else
                i++;
        }

        return ret;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        Objects.requireNonNull(c);
        boolean ret = true;
        for (Object r : c)
            ret &= remove(r);
        return ret;
    }

    @Override
    public boolean removeIf(@NonNull Predicate<? super OUT> filter) {
        Objects.requireNonNull(filter);
        boolean ret = false;
        for (int i = 0; i < size(); ) {
            OUT currentElement = externalList.get(i);
            if (filter.test(currentElement)) {
                ret |= remove(currentElement);
            } else
                i++;
        }

        return ret;
    }

    @Override
    public void clear() {
        interalList.clear();
        externalList.clear();
        externalToInternalMap.clear();
        internalToExternalMap.clear();
    }

    @Override
    public Spliterator<OUT> spliterator() {
        return externalList.spliterator();
    }

    @Override
    public Stream<OUT> stream() {
        return externalList.stream();
    }

    @Override
    public Stream<OUT> parallelStream() {
        return externalList.parallelStream();
    }
}
