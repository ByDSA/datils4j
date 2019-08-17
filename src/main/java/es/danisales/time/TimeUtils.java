package es.danisales.time;

import java.util.Date;

import static es.danisales.strings.StringUtils.PadChar.zerosLeft;

public class TimeUtils {
    private TimeUtils() {
    } // noninstantiable

    public static String stringTimestampFrom(Date d) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(d);

        String year = zerosLeft(cal.get(java.util.Calendar.YEAR), 4);
        String month = zerosLeft(cal.get(java.util.Calendar.MONTH) + 1, 2);
        String day = zerosLeft(cal.get(java.util.Calendar.DAY_OF_MONTH), 2);
        String hour = zerosLeft(cal.get(java.util.Calendar.HOUR_OF_DAY), 2);
        String min = zerosLeft(cal.get(java.util.Calendar.MINUTE), 2);
        String sec = zerosLeft(cal.get(java.util.Calendar.SECOND), 2);

        return year + month + day + "_" + hour + min + sec;
    }
}
