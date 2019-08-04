package es.danisales.time;

import es.danisales.rules.DayOfWeekRule;
import es.danisales.rules.Rule;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static es.danisales.strings.StringUtils.PadChar.zerosLeft;

public class Calendar extends ArrayList<Rule> implements CalendarInterface {
    private List<Rule> exceptions;

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
	
	public static String timestamp(Date d) {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(d);

		String year = zerosLeft(cal.get(java.util.Calendar.YEAR), 4);
		String month = zerosLeft(cal.get(java.util.Calendar.MONTH) + 1, 2);
		String day = zerosLeft(cal.get(java.util.Calendar.DAY_OF_MONTH), 2);
		String hour = zerosLeft(cal.get(java.util.Calendar.HOUR_OF_DAY), 2);
		String min = zerosLeft(cal.get(java.util.Calendar.MINUTE), 2);
		String sec = zerosLeft(cal.get(java.util.Calendar.SECOND), 2);
		
		return year + month + day + "_" + hour + min + sec;
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

		return !rulesException;
	}

	public void addException(@NonNull Rule r) {
		exceptions.add( r );
	}

	@Override
	@NonNull
	public List<Rule> getExceptions() {
		return exceptions;
	}

}
