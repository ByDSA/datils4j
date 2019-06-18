package es.danisales.rules;

import es.danisales.time.Time;

public class IntervalDayOfWeekRule extends IntervalRule<Integer> {
	public IntervalDayOfWeekRule(Integer a, Integer b) {
		super(a, b);
	}

	public boolean check() {
		return Time.isBetweenDayOfWeek( ini, end );
	}
}