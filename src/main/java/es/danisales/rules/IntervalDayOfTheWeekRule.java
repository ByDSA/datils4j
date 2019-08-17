package es.danisales.rules;

import es.danisales.time.Time;

public class IntervalDayOfTheWeekRule extends IntervalRule<Integer> {
    public IntervalDayOfTheWeekRule(Integer a, Integer b) {
		super(a, b);
	}

	public boolean check() {
		return Time.isBetweenDayOfWeek( ini, end );
	}
}