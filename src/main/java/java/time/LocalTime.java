package java.time;

import java.sql.Timestamp;

public class LocalTime {
    private Timestamp timestamp;

    public static LocalTime now() {
        LocalTime lt = new LocalTime();
        lt.timestamp = new Timestamp(System.currentTimeMillis());
        return lt;
    }

    public boolean isBefore(LocalTime lt) {
        return timestamp.before(lt.timestamp);
    }
}
