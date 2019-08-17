package es.danisales.rules;

import es.danisales.time.Time;

import static com.google.common.base.Preconditions.checkArgument;

public class DayOfTheWeekRule implements Rule {
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 7;
    private static final DayOfTheWeekRule[] daysOfTheWeekRules = new DayOfTheWeekRule[MAX_VALUE - MIN_VALUE + 1];

    private final int day;

    private DayOfTheWeekRule(int d) {
        day = d;
    }

    public static DayOfTheWeekRule of(int day) {
        checkArgument(day >= MIN_VALUE && day <= MAX_VALUE);
        return getOrCreateDayOfTheWeekRule(day);
    }

    private static DayOfTheWeekRule getOrCreateDayOfTheWeekRule(final int day) {
        int dayIndex = day - 1;
        DayOfTheWeekRule ret = daysOfTheWeekRules[dayIndex];
        if (ret == null) {
            ret = new DayOfTheWeekRule(day);
            daysOfTheWeekRules[dayIndex] = ret;
        }

        return ret;
    }

    @Override
    public boolean check() {
        return checkFrom(Time.dayOfWeek());
    }

    @SuppressWarnings("WeakerAccess")
    public boolean checkFrom(int dayToCheck) {
        return dayToCheck == day;
    }
}
