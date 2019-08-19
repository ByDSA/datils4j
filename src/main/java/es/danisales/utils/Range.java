package es.danisales.utils;

import com.google.common.base.Predicate;

import java.io.Serializable;

public interface Range<T extends Comparable<T>> extends Predicate<T>, Serializable {
    static <T extends Comparable<T>> Range<T> closed(T ini, T end) {
        if (ini.compareTo(end) <= 0)
            return new RangeSimple<>(com.google.common.collect.Range.closed(ini, end));
        else
            return new RangeComposite<>(
                    com.google.common.collect.Range.atMost(end),
                    com.google.common.collect.Range.atLeast(ini)
            );
    }

    static <T extends Comparable<T>> Range<T> open(T ini, T end) {
        if (ini.compareTo(end) <= 0)
            return new RangeSimple<>(com.google.common.collect.Range.open(ini, end));
        else
            return null;
    }


    static <T extends Comparable<T>> Range<T> closedOpen(T ini, T end) {
        if (ini.compareTo(end) <= 0)
            return new RangeSimple<>(com.google.common.collect.Range.closedOpen(ini, end));
        else
            return null;
    }

    static <T extends Comparable<T>> Range<T> atMost(T ini) {
        return new RangeSimple<>(com.google.common.collect.Range.atMost(ini));
    }

    static <T extends Comparable<T>> Range<T> atLeast(T ini) {
        return new RangeSimple<>(com.google.common.collect.Range.atLeast(ini));
    }

    static <T extends Comparable<T>> Range from(com.google.common.collect.Range<T> rangeGuava) {
        return new RangeSimple<>(rangeGuava);
    }

    static <T extends Comparable<T>> Range from(Range<T>... ranges) {
        return new RangeComposite<T>(ranges);
    }

    boolean contains(T value);

    T lowerEndpoint();

    T upperEndpoint();
}