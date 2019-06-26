package es.danisales.random;

import es.danisales.random.target.RandomPicker;
import es.danisales.random.target.SimpleTarget;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PackTargetTest {
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
        RandomPicker t = new RandomPicker();

        assertEquals(0, t.getSurface());
    }

    @Test(expected = RandomPicker.EmptyException.class)
    public void defaultPickException() {
        RandomPicker t = new RandomPicker();

        t.pickDart(0);
    }

    @Test(expected = RandomPicker.EmptyException.class)
    public void defaultPickException2() {
        RandomPicker t = new RandomPicker();

        t.pick();
    }

    @Test(expected = RandomPicker.NoSurfaceException.class)
    public void surfaceException() {
        RandomPicker t = new RandomPicker();
        SimpleTarget st = new SimpleTarget();
        st.setSurface(0);
        t.add(st);

        t.pick();
    }

    @Test
    public void surfaceNoException() {
        RandomPicker t = new RandomPicker();
        SimpleTarget st = new SimpleTarget();
        st.setSurface(-1);
        t.add(st);
        t.add(new SimpleTarget());

        t.pick();
    }
}
