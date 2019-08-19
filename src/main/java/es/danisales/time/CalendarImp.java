package es.danisales.time;

import es.danisales.rules.DayOfWeekRule;
import es.danisales.rules.Rule;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class CalendarImp extends ArrayList<Rule> implements CalendarInterface {
	public static final CalendarImp NO_LABORABLE = new CalendarImp() {
		{
			add(DayOfWeekRule.of(DayOfWeek.SATURDAY));
			add(DayOfWeekRule.of(DayOfWeek.SUNDAY));
		}
	};
	private final List<Rule> exceptions;

	public static final CalendarImp LABORABLE = new CalendarImp() {
		{
			addException(NO_LABORABLE);
		}
	};


	public CalendarImp() {
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
