package es.danisales.rules;

import es.danisales.time.Time;

public class DayOfWeekRule implements Rule {
	int day;
	
	public DayOfWeekRule(int d) {
		day = d;
	}

	public boolean check() {
		return Time.dayOfWeek() == day;
	}
}
