package es.danisales.random;

import es.danisales.random.target.RandomPicker;
import es.danisales.random.target.RandomPickerBuilder;
import es.danisales.random.target.SimpleTarget;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PackTargetTest {
    private static RandomPicker createVariableSize() {
        return new RandomPickerBuilder<>().surfaceVariable().build();
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
        RandomPicker t = createVariableSize();

        assertEquals(0, t.getSurface());
    }

    @Test(expected = IllegalStateException.class)
    public void defaultPickException() {
        RandomPicker t = createVariableSize();

        t.pickDart(0);
    }

    @Test(expected = IllegalStateException.class)
    public void pickOnZeroSurfaceNewCreation() {
        RandomPicker t = createVariableSize();

        t.pick();
    }

    @Test(expected = IllegalStateException.class)
    public void pickOnZeroSurface() {
        RandomPicker t = createVariableSize();
        SimpleTarget st = new SimpleTarget();
        st.setSurface(0);
        t.add(st);

        t.pick();
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeSurface() {
        RandomPicker t = createVariableSize();
        SimpleTarget st = new SimpleTarget();
        st.setSurface(-1);
        t.add(st);
        t.add(new SimpleTarget());

        t.pick();
    }
}
