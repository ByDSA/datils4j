package es.danisales.time;

import es.danisales.utils.Range;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class TimeUtilsTest {
    private static LocalDateTime calendarTest = LocalDateTime.of(2000, Month.JUNE, 15, 0, 0, 0);
// thursday

    @Test
    public void setFake() {
        assertNotEquals(calendarTest, TimeUtils.Current.now());
        TimeUtils.Current.setFake(calendarTest);
        assertEquals(calendarTest, TimeUtils.Current.now());
    }

    public static class isBetweenDayOfMonth {
        @Test
        public void normalTrue() {
            boolean ret = TimeUtils.isBetweenDayOfMonth(calendarTest, Range.closed(14, 16));
            assertTrue(ret);
        }

        @Test
        public void reversedTrue() {
            boolean ret = TimeUtils.isBetweenDayOfMonth(calendarTest, Range.closed(14, 12));
            assertTrue(ret);
        }

        @Test
        public void normalFalse() {
            boolean ret = TimeUtils.isBetweenDayOfMonth(calendarTest, Range.closed(16, 18));
            assertFalse(ret);
        }

        @Test
        public void closedIni() {
            boolean ret = TimeUtils.isBetweenDayOfMonth(calendarTest, Range.closed(15, 18));
            assertTrue(ret);
        }

        @Test
        public void closedEnd() {
            boolean ret = TimeUtils.isBetweenDayOfMonth(calendarTest, Range.closed(12, 15));
            assertTrue(ret);
        }

        @Test
        public void reversedFalse() {
            boolean ret = TimeUtils.isBetweenDayOfMonth(calendarTest, Range.closed(16, 14));
            assertFalse(ret);
        }

        @Test(expected = IllegalArgumentException.class)
        public void invalidParams1() {
            TimeUtils.isBetweenDayOfMonth(calendarTest, Range.closed(0, 14));
        }

        @Test(expected = IllegalArgumentException.class)
        public void invalidParams2() {
            TimeUtils.isBetweenDayOfMonth(calendarTest, Range.closed(14, 0));
        }

        @Test(expected = IllegalArgumentException.class)
        public void invalidParams3() {
            TimeUtils.isBetweenDayOfMonth(calendarTest, Range.closed(14, 32));
        }

        @Test(expected = IllegalArgumentException.class)
        public void invalidParams4() {
            TimeUtils.isBetweenDayOfMonth(calendarTest, Range.closed(32, 14));
        }
    }

    public static class isBetweenDayOfWeek {
        @Test
        public void normalTrue() {
            boolean ret = TimeUtils.isBetweenDayOfWeek(calendarTest, Range.closed(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
            assertTrue(ret);
        }

        @Test
        public void reversedTrue() {
            boolean ret = TimeUtils.isBetweenDayOfWeek(calendarTest, Range.closed(DayOfWeek.WEDNESDAY, DayOfWeek.MONDAY));
            assertTrue(ret);
        }

        @Test
        public void normalFalse() {
            boolean ret = TimeUtils.isBetweenDayOfWeek(calendarTest, Range.closed(DayOfWeek.FRIDAY, DayOfWeek.SUNDAY));
            assertFalse(ret);
        }

        @Test
        public void reversedFalse() {
            boolean ret = TimeUtils.isBetweenDayOfWeek(calendarTest, Range.closed(DayOfWeek.FRIDAY, DayOfWeek.WEDNESDAY));
            assertFalse(ret);
        }
    }
}