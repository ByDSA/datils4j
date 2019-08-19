package es.danisales.utils;

import org.junit.Test;

public class RangeTest {
    @Test
    public void equals() {
        com.google.common.collect.Range rangeGuava =
                com.google.common.collect.Range.closed(1, 5);

        Range range = Range.from(rangeGuava);
    }
}