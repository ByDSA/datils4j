package es.danisales.rules;

import es.danisales.time.Time;
import org.junit.Test;

import static org.junit.Assert.*;

public class DayOfTheWeekRuleTest {
    @Test
    public void of() {
        for (int i = 1; i <= 7; i++) {
            DayOfTheWeekRule dayOfTheWeekRule = DayOfTheWeekRule.of(i);
            assertSame(dayOfTheWeekRule, DayOfTheWeekRule.of(i));
        }
    }

    @Test
    public void check() {
        int currentDayOfTheWeek = Time.dayOfWeek();

        for (int i = 1; i <= 7; i++) {
            DayOfTheWeekRule dayOfTheWeekRule = DayOfTheWeekRule.of(i);
            assertEquals(currentDayOfTheWeek == i, dayOfTheWeekRule.check());
        }
    }

    @Test
    public void checkFrom() {
        for (int i = 1; i <= 7; i++) {
            DayOfTheWeekRule dayOfTheWeekRule = DayOfTheWeekRule.of(i);
            assertTrue(dayOfTheWeekRule.checkFrom(i));
        }
    }
}