package time;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class Time {
	public static int dayOfWeek() {
		return dayOfWeek(now());
	}

	public static int dayOfMonth() {
		return dayOfMonth(now());
	}

	public static int dayOfWeek(Date d) {
		return dayOfWeek(dateToCalendar(d));
	}

	public static int dayOfWeek(Calendar c) {
		return c.get(Calendar.DAY_OF_WEEK);
	}
	
	public static Calendar dateToCalendar(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		
		return c;
	}

	public static int dayOfMonth(Date d) {
		return dayOfMonth(dateToCalendar(d));
	}

	public static int dayOfMonth(Calendar c) {
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public static Calendar now() {
		return dateToCalendar(new Date());
	}

	public static boolean isBetweenDayOfMonth(Calendar c, int a, int b) {
		int n = c.get( Calendar.DAY_OF_MONTH );

		return compare(n, a, b);
	}
	
	public static boolean isBetweenDayOfMonth(int a, int b) {
		return isBetweenDayOfMonth(now(), a, b);
	}

	public static boolean isBetweenDayOfWeek(Calendar c, int a, int b) {
		int n = c.get( Calendar.DAY_OF_WEEK );
		n = dayOfWeekEurope(n);
		a = dayOfWeekEurope(a);
		b = dayOfWeekEurope(b);
		return compare(n, a, b);
	}
	
	private static int dayOfWeekEurope(int n) {
		return (n + 5) % 7;
	}
	
	public static boolean isBetweenDayOfWeek(int a, int b) {
		return isBetweenDayOfWeek(now(), a, b);
	}
	
	public static boolean isBetweenDayOfWeekInMonth(Calendar c, int a, int b) {
		int n = c.get( Calendar.DAY_OF_WEEK_IN_MONTH );
		return compare(n, a, b);
	}
	
	public static boolean isBetween(LocalTime n, LocalTime a, LocalTime b) {
		  return !n.isBefore(a) && n.isBefore(b);
	}
	
	public static boolean isBetween(LocalTime a, LocalTime b) {
		  return isBetween(LocalTime.now(), a, b);
	}
	
	
	
	private static boolean compare(int n, int a, int b) {
		if (a < b)
			return (n >= a && n <= b);
		else if (a > b)
			return (n > a || n < b);
		else
			return n == a;
	}
}
