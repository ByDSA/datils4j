package es.danisales.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RangeComposite<T extends Comparable<T>> implements Range<T> {
    private final List<com.google.common.collect.Range<T>> ranges = new ArrayList<>();

    RangeComposite(com.google.common.collect.Range<T>... rangesGuava) {
        List<com.google.common.collect.Range<T>> rangesGuavaAsList = Arrays.asList(rangesGuava);
        ranges.addAll(rangesGuavaAsList);
    }

    RangeComposite(Range<T>... rs) {
        for (Range<T> r : rs) {
            if (!(r instanceof RangeSimple))
                throw new IllegalArgumentException();
            RangeSimple<T> rangeSimple = (RangeSimple<T>) r;
            ranges.add(rangeSimple.range);
        }
    }

    @Override
    public boolean apply(T input) {
        return contains(input);
    }

    @Override
    public String toString() {
        return ranges.toString();
    }

    @Override
    public int hashCode() {
        return ranges.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RangeComposite)
            return ranges.equals(((RangeComposite) o).ranges);
        if (o instanceof com.google.common.collect.Range)
            return ranges.equals(o);

        return false;
    }

    @Override
    public boolean contains(T value) {
        for (com.google.common.collect.Range<T> r : ranges)
            if (r.contains(value))
                return true;

        return false;
    }

    @Override
    public T lowerEndpoint() {
        com.google.common.collect.Range<T> firstRange = ranges.get(0);
        T lower = firstRange.hasLowerBound() ? firstRange.lowerEndpoint() : null;
        for (int i = 1; i < ranges.size(); i++) {
            com.google.common.collect.Range<T> r = ranges.get(i);
            if (lower == null || r.lowerEndpoint().compareTo(lower) < 0)
                lower = r.lowerEndpoint();
        }
        return lower;
    }

    @Override
    public T upperEndpoint() {
        com.google.common.collect.Range<T> firstRange = ranges.get(0);
        T upper = firstRange.hasUpperBound() ? firstRange.upperEndpoint() : null;
        for (int i = 1; i < ranges.size(); i++) {
            com.google.common.collect.Range<T> r = ranges.get(i);
            if (upper == null || r.lowerEndpoint().compareTo(upper) < 0)
                upper = r.upperEndpoint();
        }
        return upper;
    }
}
