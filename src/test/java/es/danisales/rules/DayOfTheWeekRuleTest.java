package es.danisales.rules;

import es.danisales.time.TimeUtils;
import org.junit.Test;

import java.time.DayOfWeek;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class DayOfTheWeekRuleTest {
    @Test
    public void of() {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            DayOfWeekRule dayOfTheWeekRule = DayOfWeekRule.of(dayOfWeek);
            assertSame(dayOfTheWeekRule, DayOfWeekRule.of(dayOfWeek));
        }
    }

    @Test
    public void check() {
        DayOfWeek currentDayOfTheWeek = TimeUtils.Current.now().getDayOfWeek();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            DayOfWeekRule dayOfTheWeekRule = DayOfWeekRule.of(dayOfWeek);
            assertEquals(currentDayOfTheWeek == dayOfWeek, dayOfTheWeekRule.check());
        }
    }
}