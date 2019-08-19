package es.danisales.rules;

import es.danisales.time.TimeUtils;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DayOfWeekRule implements Rule {
    private static final Map<DayOfWeek, DayOfWeekRule> daysOfTheWeekRules = new ConcurrentHashMap<>();

    private final DayOfWeek day;

    private DayOfWeekRule(DayOfWeek d) {
        day = d;
    }

    public static DayOfWeekRule of(DayOfWeek day) {
        return getOrCreateDayOfTheWeekRule(day);
    }

    private static DayOfWeekRule getOrCreateDayOfTheWeekRule(final DayOfWeek day) {
        DayOfWeekRule ret = daysOfTheWeekRules.get(day);
        if (ret == null) {
            ret = new DayOfWeekRule(day);
            daysOfTheWeekRules.put(day, ret);
        }

        return ret;
    }

    @Override
    public boolean check() {
        return TimeUtils.Current.now().getDayOfWeek() == day;
    }
}
