package es.danisales.rules;

import es.danisales.time.Time;

public class IntervalDayRule extends IntervalRule<Integer> {
	public IntervalDayRule(Integer a, Integer b) {
		super( a, b );
	}

	public boolean check() {
		return Time.isBetweenDayOfMonth( ini, end );
	}
}
