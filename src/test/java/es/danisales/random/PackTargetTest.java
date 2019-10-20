package es.danisales.random;

import es.danisales.random.target.RandomPicker;
import es.danisales.random.target.SimpleTarget;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PackTargetTest {
    private RandomPicker<SimpleTarget> randomPicker;

    @Before
    public void init() {
        randomPicker = RandomPicker.builder(SimpleTarget.class).surfaceVariable().build();
    }

    @Test
    public void pick() {
        SimpleTarget t = new SimpleTarget();

        int SIZE = 1000;
        long[] randomArray = ValueGenerator.Array.longInt(SIZE);

        for (int i = 0; i < SIZE; i++) {
            assertEquals(t, t.pickDart(randomArray[i]));
            assertEquals(t, t.pick());
        }
    }

    @Test
    public void defaultSurface() {
        assertEquals(0, randomPicker.getSurface());
    }

    @Test(expected = IllegalStateException.class)
    public void defaultPickException() {
        randomPicker.pickDart(0);
    }

    @Test(expected = IllegalStateException.class)
    public void pickOnZeroSurfaceNewCreation() {
        randomPicker.pick();
    }

    @Test(expected = IllegalStateException.class)
    public void pickOnZeroSurface() {
        RandomPicker<SimpleTarget> t = randomPicker;
        SimpleTarget st = new SimpleTarget();
        st.setSurface(0);
        randomPicker.add(st);

        t.pick();
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeSurface() {
        RandomPicker<SimpleTarget> t = randomPicker;
        SimpleTarget st = new SimpleTarget();
        st.setSurface(-1);
        t.add(st);
        t.add(new SimpleTarget());

        t.pick();
    }
}
