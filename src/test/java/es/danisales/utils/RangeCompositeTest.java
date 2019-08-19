package es.danisales.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RangeCompositeTest {
    @Test
    public void closed() {
        Range<Integer> range = Range.closed(6, 4);

        assertTrue(range.contains(4));
        assertTrue(range.contains(4));
        assertTrue(range.contains(-1234));
        assertTrue(range.contains(1234));
        assertFalse(range.contains(5));
    }

    @Test
    public void contains() {
        Range<Integer> range = Range.from(Range.closed(1, 4),
                Range.closed(6, 10));

        assertTrue(range.contains(1));
        assertTrue(range.contains(3));
        assertTrue(range.contains(4));
        assertTrue(range.contains(6));
        assertTrue(range.contains(7));
        assertTrue(range.contains(10));
        assertFalse(range.contains(5));
        assertFalse(range.contains(0));
        assertFalse(range.contains(11));
    }
}