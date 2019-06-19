package es.danisales.random;

import es.danisales.random.target.PackTarget;
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
        PackTarget t = new PackTarget();

        assertEquals(0, t.getSurface());
    }

    @Test(expected = PackTarget.EmptyException.class)
    public void defaultPickException() {
        PackTarget t = new PackTarget();

        t.pickDart(0);
    }

    @Test(expected = PackTarget.EmptyException.class)
    public void defaultPickException2() {
        PackTarget t = new PackTarget();

        t.pick();
    }

    @Test(expected = PackTarget.NoSurfaceException.class)
    public void surfaceException() {
        PackTarget t = new PackTarget();
        SimpleTarget st = new SimpleTarget();
        st.setSurface(0);
        t.add(st);

        t.pick();
    }

    @Test
    public void surfaceNoException() {
        PackTarget t = new PackTarget();
        SimpleTarget st = new SimpleTarget();
        st.setSurface(-1);
        t.add(st);
        t.add(new SimpleTarget());

        t.pick();
    }
}
