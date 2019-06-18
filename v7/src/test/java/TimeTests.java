import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

public class TimeTests {
	@Test
	public void isBetweenDayOfWeek() {
		Calendar c = Calendar.getInstance();
		c.set( 2018, Calendar.OCTOBER, 29 );

		assertEquals(29, es.danisales.time.Time.dayOfMonth( c ));
		assertEquals(Calendar.MONDAY, c.get( Calendar.DAY_OF_WEEK ));
		assertEquals(Calendar.MONDAY, es.danisales.time.Time.dayOfWeek( c ));
		
		boolean b = es.danisales.time.Time.isBetweenDayOfMonth( c, 28, 30 );
		assertEquals(true, b);
		
		b = es.danisales.time.Time.isBetweenDayOfMonth( c, 30, 28 );
		assertEquals(false, b);
		
		b = es.danisales.time.Time.isBetweenDayOfMonth( c, 31, 30 );
		assertEquals(true, b);
		
		b = es.danisales.time.Time.isBetweenDayOfWeek( c, Calendar.SATURDAY, Calendar.TUESDAY );
		assertEquals(true, b);
	}
}
