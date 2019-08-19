package es.danisales.rules;

import es.danisales.time.TimeUtils;
import es.danisales.utils.Range;

import java.time.DayOfWeek;

public class IntervalDayOfWeekRule extends RangeRule<DayOfWeek> {
	public IntervalDayOfWeekRule(DayOfWeek a, DayOfWeek b) {
		super(a, b);
	}

	public IntervalDayOfWeekRule(Range<DayOfWeek> range) {
		super(range);
	}

	public boolean check() {
		return TimeUtils.Current.isBetweenDayOfWeek(range);
	}
}