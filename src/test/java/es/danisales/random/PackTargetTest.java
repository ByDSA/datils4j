package es.danisales.random;

import es.danisales.random.target.RandomPicker2D;
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
        RandomPicker2D t = new RandomPicker2D();

        assertEquals(0, t.getSurface());
    }

    @Test(expected = RandomPicker2D.EmptyException.class)
    public void defaultPickException() {
        RandomPicker2D t = new RandomPicker2D();

        t.pickDart(0);
    }

    @Test(expected = RandomPicker2D.EmptyException.class)
    public void defaultPickException2() {
        RandomPicker2D t = new RandomPicker2D();

        t.pick();
    }

    @Test(expected = RandomPicker2D.NoSurfaceException.class)
    public void surfaceException() {
        RandomPicker2D t = new RandomPicker2D();
        SimpleTarget st = new SimpleTarget();
        st.setSurface(0);
        t.add(st);

        t.pick();
    }

    @Test
    public void surfaceNoException() {
        RandomPicker2D t = new RandomPicker2D();
        SimpleTarget st = new SimpleTarget();
        st.setSurface(-1);
        t.add(st);
        t.add(new SimpleTarget());

        t.pick();
    }
}
