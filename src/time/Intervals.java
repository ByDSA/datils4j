package time;

import java.util.Date;

public class Intervals {
	public static double daysFrom(Date d) {
		assert d != null;
		long sSec = d.getTime();
		long cSec = new Date().getTime();
		
		return (cSec - sSec) / (3600.0D * 24 * 1000);
	}
}
