package es.danisales.time;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.time.LocalDateTime;
import java.time.Month;

public class TimeUtilsCurrentTest {
    private LocalDateTime calendarTest = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0);

    @BeforeClass
    public void startFake() {
        TimeUtils.Current.setFake(calendarTest);
    }

    @AfterClass
    public void endFake() {
        TimeUtils.Current.setFake(null);
    }
}