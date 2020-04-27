package es.danisales.time;

import es.danisales.utils.Range;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.*;
import java.util.Calendar;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static es.danisales.strings.StringUtils.PadChar.zerosLeft;

public class TimeUtils {
    private TimeUtils() {
    } // noninstantiable

    @SuppressWarnings("WeakerAccess")
    public static boolean isBetweenDayOfMonth(@NonNull LocalDateTime localDateTime, Range<Integer> range) {
        checkDayOfMonth(range);
        int n = localDateTime.getDayOfMonth();

        return range.contains(n);
    }

    private static void checkDayOfMonth(Range<Integer> range) {
        checkArgument(range.lowerEndpoint() >= 1 && range.lowerEndpoint() <= 31);
        checkArgument(range.upperEndpoint() >= 1 && range.upperEndpoint() <= 31);
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean isBetweenDayOfWeek(@NonNull LocalDateTime localDateTime, Range<DayOfWeek> range) {
        DayOfWeek localDateTimeDayOfWeek = localDateTime.getDayOfWeek();
        return range.contains(localDateTimeDayOfWeek);
    }

    public static boolean isBetweenDayOfWeekInMonth(@NonNull Calendar calendar, Range<Integer> range) {
        int n = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        return range.contains(n);
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean isBetween(LocalTime n, Range<LocalTime> range) {
        return range.contains(n);
    }

    private static boolean compare(int n, int ini, int end) {
        if (ini < end)
            return (n >= ini && n <= end);
        else if (ini > end)
            return (n > ini || n < end);
        else
            return n == ini;
    }

    public static class Current {
        private static LocalDateTime fake = null;

        private Current() {
        } // noninstantiable

        static void setFake(@Nullable LocalDateTime localDateTime) {
            fake = localDateTime;
        }

        public static boolean isBetweenDayOfMonth(Range<Integer> range) {
            return TimeUtils.isBetweenDayOfMonth(now(), range);
        }

        public static boolean isBetweenDayOfWeek(Range<DayOfWeek> range) {
            return TimeUtils.isBetweenDayOfWeek(now(), range);
        }

        public static LocalDateTime now() {
            return fake != null ? fake : LocalDateTime.now();
        }

        public static String timestamp() {
            return Timestamp.stringFrom(now());
        }

        public static double daysFrom(@NonNull LocalDate localDate) {
            checkNotNull(localDate);
            Period period = Period.between(localDate, now().toLocalDate());
            return period.getDays();
        }

        public static boolean isBetween(Range<LocalTime> range) {
            return TimeUtils.isBetween(LocalTime.now(), range);
        }
    }

    public static class Timestamp {
        private Timestamp() {
        } // noninstantiable

        @SuppressWarnings("WeakerAccess")
        public static String stringFrom(LocalDateTime localDateTime) {
            String year = zerosLeft(localDateTime.getYear(), 4);
            String month = zerosLeft(localDateTime.getMonthValue(), 2);
            String day = zerosLeft(localDateTime.getDayOfMonth(), 2);
            String hour = zerosLeft(localDateTime.getHour(), 2);
            String min = zerosLeft(localDateTime.getMinute(), 2);
            String sec = zerosLeft(localDateTime.getSecond(), 2);

            return year + month + day + "_" + hour + min + sec;
        }
    }
}