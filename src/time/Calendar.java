package time;

import java.util.ArrayList;
import java.util.List;

import rules.DayOfWeekRule;
import rules.IntervalDayOfWeekRule;
import rules.Rule;

public class Calendar extends ArrayList<Rule> implements CalendarInterface {	
	List<Rule> exceptions;

	public static final Calendar NO_LABORABLE = new Calendar() {
		{
			add( new DayOfWeekRule( java.util.Calendar.SATURDAY ) );
			add( new DayOfWeekRule( java.util.Calendar.SUNDAY ) );
		}
	};
	
	public static final Calendar LABORABLE = new Calendar() {
		{
			addException(NO_LABORABLE);
		}
	};
	
	

	public Calendar() {
		exceptions = new ArrayList<Rule>();
	}

	public boolean check() {
		boolean rulesCheck = true;
		for (Rule r : this) {
			if ( !r.check() ) {
				rulesCheck = false;
				break;
			}
		}

		if (!rulesCheck)
			return false;

		assert exceptions != null;
		boolean rulesException = false;
		for (Rule r : exceptions) {
			if ( r.check() ) {
				rulesException = true;
				break;
			}
		}

		if (rulesException)
			return false;

		return true;
	}

	public void addException(Rule r) {
		exceptions.add( r );
	}

	@Override
	public List<Rule> getExceptions() {
		return exceptions;
	}

}
