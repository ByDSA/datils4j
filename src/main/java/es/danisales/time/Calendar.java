package es.danisales.time;

import es.danisales.rules.DayOfTheWeekRule;
import es.danisales.rules.Rule;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Calendar extends ArrayList<Rule> implements CalendarInterface {
	public static final Calendar NO_LABORABLE = new Calendar() {
		{
			add(DayOfTheWeekRule.of(java.util.Calendar.SATURDAY));
			add(DayOfTheWeekRule.of(java.util.Calendar.SUNDAY));
		}
	};
	private final List<Rule> exceptions;
	
	public static final Calendar LABORABLE = new Calendar() {
		{
			addException(NO_LABORABLE);
		}
	};
	

	public Calendar() {
		exceptions = new ArrayList<>();
	}

	public boolean check() {
		return checkRules() && !checkExceptions();
	}

	private boolean checkRules() {
		for (Rule r : this)
			if (!r.check())
				return false;

		return true;
	}

	private boolean checkExceptions() {
		for (Rule r : exceptions)
			if (r.check())
				return true;

		return false;
	}

	public void addException(@NonNull Rule r) {
		exceptions.add( r );
	}

	public void addAllException(@NonNull List<? extends Rule> r) {
		exceptions.addAll(r);
	}

	@Override
	@NonNull
	public List<Rule> getExceptions() {
		return exceptions;
	}

}
