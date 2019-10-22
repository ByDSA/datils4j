package es.danisales.random.target;

import es.danisales.random.RandomMode;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class RandomPicker1DRemoverTest {
    private RandomPicker<Integer> randomPicker1DRemover;

    @Before
    public void init() {
        randomPicker1DRemover = new RandomPickerBuilder<Integer>()
                .setRandomMode(RandomMode.Normal)
                .from(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                .removeOnPick()
                .build();

        assertTrue(randomPicker1DRemover instanceof RandomPicker1DRemover);
    }

    @Test
    public void pickSize() {
        assertEquals(10, randomPicker1DRemover.size());
        randomPicker1DRemover.pick();
        assertEquals(9, randomPicker1DRemover.size());
    }

    @Test
    public void pickRemoved() {
        Integer picked = randomPicker1DRemover.pick();
        assertFalse(randomPicker1DRemover.contains(picked));
    }
}