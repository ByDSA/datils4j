package es.danisales.rules;

import es.danisales.time.TimeUtils;
import es.danisales.utils.Range;

import java.time.LocalTime;

public class IntervalTimeRule extends RangeRule<LocalTime> {
	public IntervalTimeRule(LocalTime a, LocalTime b) {
		super(a, b);
	}

	public IntervalTimeRule(Range<LocalTime> range) {
		super(range);
	}

	public boolean check() {
		return TimeUtils.Current.isBetween(range);
	}
}
