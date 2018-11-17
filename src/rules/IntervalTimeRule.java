package rules;

import java.time.LocalTime;

import time.Time;

public class IntervalTimeRule extends IntervalRule<LocalTime> {
	public IntervalTimeRule(LocalTime a, LocalTime b) {
		super(a, b);
	}

	public boolean check() {
		return Time.isBetween( ini, end );
	}
}
