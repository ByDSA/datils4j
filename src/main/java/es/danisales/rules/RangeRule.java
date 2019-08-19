package es.danisales.rules;

import es.danisales.utils.Range;

public abstract class RangeRule<T extends Comparable<T>> implements Rule {
	protected Range<T> range;

	@SuppressWarnings("WeakerAccess")
	public RangeRule(T a, T b) {
		range = Range.closed(a, b);
	}

	@SuppressWarnings("WeakerAccess")
	public RangeRule(Range<T> range) {
		this.range = range;
	}
}
