package es.danisales.rules;

import es.danisales.time.Time;

public class DayRule implements Rule {
	int day;

	public DayRule(int d) {
		day = d;
	}

	public boolean check() {
		return Time.dayOfMonth() == day;
	}

}