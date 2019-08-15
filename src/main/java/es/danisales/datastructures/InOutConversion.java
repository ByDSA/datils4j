package es.danisales.datastructures;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class InOutConversion<OUT, IN> implements Set<OUT> {
    private final List<IN> interalList = new ArrayList<>();
    private final List<OUT> externalList = new ArrayList<>();
    private final Map<IN, OUT> intertalToExternalMap = new HashMap<>();
    private final Map<OUT, IN> externalToInternalMap = new HashMap<>();

    @Override
    public int size() {
        return interalList.size();
    }

    @Override
    public boolean isEmpty() {
        return interalList.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return externalList.contains(o);
    }

    protected OUT get(int i) {
        checkArgument(i >= 0);
        return externalList.get(i);
    }

    protected int indexOf(OUT o) {
        return externalList.indexOf(o);
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

    @Override
    public boolean add(@Nullable OUT ret) {
        externalList.add(ret);
        IN in = createInternal(ret);
        interalList.add(in);
        intertalToExternalMap.put(in, ret);
        externalToInternalMap.put(ret, in);
        return true;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        boolean removed = externalList.remove(o);
        if (!removed)
            return false;

        IN in = externalToInternalMap.get(o);
        interalList.remove(in);
        externalToInternalMap.remove(o);
        intertalToExternalMap.remove(in);

        return true;
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
        intertalToExternalMap.clear();
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
