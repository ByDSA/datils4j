package es.danisales.rules;

import es.danisales.time.TimeUtils;
import es.danisales.utils.Range;

public class IntervalDayRule extends RangeRule<Integer> {
	public IntervalDayRule(Integer a, Integer b) {
		super( a, b );
	}

	public IntervalDayRule(Range<Integer> range) {
		super(range);
	}

	public boolean check() {
		return TimeUtils.Current.isBetweenDayOfMonth(range);
	}
}
