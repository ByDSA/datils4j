package es.danisales.random;

import es.danisales.random.ValueGenerator;
import es.danisales.random.target.SimpleTarget;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleTargetTest {
    @Test
    public void pick() {
        SimpleTarget t = new SimpleTarget();

        int SIZE = 1000;
        long[] randomArray = ValueGenerator.longArray(SIZE);

        for (int i = 0; i < SIZE; i++) {
            assertEquals(t, t.pickDart(randomArray[i]));
            assertEquals(t, t.pick());
        }
    }

    @Test
    public void defaultSurface() {
        SimpleTarget t = new SimpleTarget();

        assertEquals(1, t.getSurface());
        t.pickDart(0);
        t.pickDart(1);
        t.beforeOnPick();
        assertEquals(1, t.getSurface());
    }
}
