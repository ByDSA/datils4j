package es.danisales.utils;

class RangeSimple<T extends Comparable<T>> implements Range<T> {
    final com.google.common.collect.Range<T> range;

    RangeSimple(com.google.common.collect.Range<T> range) {
        this.range = range;
    }

    @Override
    public boolean apply(T input) {
        return range.contains(input);
    }

    @Override
    public String toString() {
        return range.toString();
    }

    @Override
    public int hashCode() {
        return range.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RangeSimple)
            return range.equals(((RangeSimple) o).range);
        if (o instanceof com.google.common.collect.Range)
            return range.equals(o);

        return false;
    }

    @Override
    public boolean contains(T value) {
        return range.contains(value);
    }

    @Override
    public T lowerEndpoint() {
        return range.lowerEndpoint();
    }

    @Override
    public T upperEndpoint() {
        return range.upperEndpoint();
    }
}
