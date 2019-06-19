package es.danisales.random.target;

import es.danisales.utils.Bean;

import java.util.Objects;

@SuppressWarnings("unused")
public class SimpleTargetBean<T> extends SimpleTarget implements Bean<T> {
	private T value;

	public SimpleTargetBean(T v) {
		super();

		setValue(v);
	}

	@Override
	public final T getValue() {
		return value;
	}

    @Override
	public final void setValue(T v) {
        Objects.requireNonNull(v);
	    value = v;
    }

	@SuppressWarnings("unchecked")
	@Override
    public SimpleTargetBean<T> pickDart(long dart) {
        return (SimpleTargetBean<T>) super.pickDart(dart);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SimpleTargetBean<T> pick() {
		return (SimpleTargetBean<T>) super.pick();
	}

    @Override
    public String toString() {
        return value.toString();
    }
}
