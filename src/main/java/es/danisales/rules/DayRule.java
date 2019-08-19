package es.danisales.rules;

import es.danisales.time.TimeUtils;

import static com.google.common.base.Preconditions.checkArgument;

public class DayRule implements Rule {
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 31;
    private static final DayRule[] daysOfMonthRules = new DayRule[MAX_VALUE - MIN_VALUE + 1];

    private final int day;

    private DayRule(int d) {
		day = d;
	}

    public static DayRule of(int day) {
        checkArgument(day >= MIN_VALUE && day <= MAX_VALUE);
        return getOrCreateDayOfTheWeekRule(day);
    }

    private static DayRule getOrCreateDayOfTheWeekRule(final int day) {
        int dayIndex = day - 1;
        DayRule ret = daysOfMonthRules[dayIndex];
        if (ret == null) {
            ret = new DayRule(day);
            daysOfMonthRules[dayIndex] = ret;
        }

        return ret;
    }

    @Override
    public boolean check() {
        return TimeUtils.Current.now().getDayOfMonth() == day;
    }
}