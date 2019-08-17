package es.danisales.arrays;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayUtilsTest {
    @Test
    public void deepLengthOneLevel() {
        Object[][] array = new Object[][]{
                {1, 2, 3},
                {4, 5, 6, new Object[]{7, new Object[]{7, 7, 7}, 7}},
                {8, 9, new Object[]{
                        10, 10
                }}
        };

        int len = ArrayUtils.deepLengthOneLevel(array);
        assertEquals(10, len);
    }

    /**
     * Concat (Two Arrays)
     */

    @Test
    public void concat1Length() {
        ConcatTestClass[] param1 = ConcatTestClass.DATA_TEST[0];
        ConcatTestClass[] param2 = ConcatTestClass.DATA_TEST[1];
        ConcatTestClass[] result = ArrayUtils.concat(param1, param2);
        assertEquals(param1.length + param2.length, result.length);
    }

    /**
     * Concat (N Arrays)
     */

    @Test
    public void concat2Length() {
        ConcatTestClass[] param1 = ConcatTestClass.DATA_TEST[0];
        ConcatTestClass[] param2 = ConcatTestClass.DATA_TEST[1];
        ConcatTestClass[] param3 = ConcatTestClass.DATA_TEST[2];
        ConcatTestClass[] result = ArrayUtils.concat(param1, param2, param3);

        assertEquals(param1.length + param2.length + param3.length, result.length);
    }

    @Test
    public void concat2EmptyLength() {
        ConcatTestClass[] param1 = ConcatTestClass.DATA_TEST[6];
        ConcatTestClass[] param2 = ConcatTestClass.DATA_TEST[7];
        ConcatTestClass[] param3 = ConcatTestClass.DATA_TEST[8];
        ConcatTestClass[] result = ArrayUtils.concat(param1, param2, param3);

        assertEquals(0, result.length);
        assertEquals(param1.length + param2.length + param3.length, result.length);
    }

    @Test
    public void concat2ArrayOfArray() {
        ConcatTestClass[] result = ArrayUtils.concat(ConcatTestClass.DATA_TEST);

        assertEquals(ArrayUtils.deepLengthOneLevel(ConcatTestClass.DATA_TEST), result.length);
    }

    @Test
    public void concat2EmptyLengthOneArgument() {
        ConcatTestClass[] param1 = ConcatTestClass.DATA_TEST[6];
        ConcatTestClass[] result = ArrayUtils.concat(param1);

        assertEquals(0, result.length);
        assertEquals(param1.length, result.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void concat2EmptyLengthNoArgument() {
        ConcatTestClass[] result = ArrayUtils.concat();

        assertEquals(0, result.length);
    }

    @Test
    public void concat2LengthArray() {
        ConcatTestClass[] result = ArrayUtils.concat(ConcatTestClass.DATA_TEST);
        int expectedLength = ArrayUtils.deepLength(ConcatTestClass.DATA_TEST);

        assertEquals(expectedLength, result.length);
    }

    @Test
    public void concat2Values() {
        ConcatTestClass[] param1 = ConcatTestClass.DATA_TEST[0];
        ConcatTestClass[] param2 = ConcatTestClass.DATA_TEST[1];
        ConcatTestClass[] param3 = ConcatTestClass.DATA_TEST[3]; // same content (different objects) as param1
        ConcatTestClass[] result = ArrayUtils.concat(param1, param2, param3);
        int i = 0;
        for (int j = 0; j < param1.length; j++, i++)
            assertSame(param1[j], result[i]);

        for (int j = 0; j < param2.length; j++, i++)
            assertSame(param2[j], result[i]);

        for (int j = 0; j < param3.length; j++, i++)
            assertSame(param3[j], result[i]);

        for (int j = 0; j < param1.length; j++, i++) {
            assertNotSame(param1[j], param3[j]);
            assertEquals(param1[j], param3[j]);
        }
    }

    /**
     * Concat (Byte)
     */

    @Test
    public void concatByteLength() {
        byte[] param1 = new byte[]{1, 2, 3, 4, 5, 6};
        byte[] param2 = new byte[]{7, 8, 9, 10};
        byte[] result = ArrayUtils.concat(param1, param2);
        assertEquals(param1.length + param2.length, result.length);
    }

    @Test
    public void concatByteValues() {
        byte[] param1 = new byte[]{1, 2, 3, 4, 5, 6};
        byte[] param2 = new byte[]{7, 8, 9, 10};
        byte[] result = ArrayUtils.concat(param1, param2);
        int i = 0;
        for (int j = 0; j < param1.length; j++, i++)
            assertSame(param1[j], result[i]);

        for (int j = 0; j < param2.length; j++, i++)
            assertSame(param2[j], result[i]);
    }

    /**
     * Deep Length
     */

    @Test
    public void deepLength() {
        Object[][] array = new Object[][]{
                {1, 2, 3},
                {4, 5, 6, 7},
                {8, 9, 10, new Object[]{
                        11, 12, 13
                }}
        };

        int expectedLength = 13;
        int currentLength = ArrayUtils.deepLength(array);

        assertEquals(expectedLength, currentLength);
    }

    @Test
    public void deepLengthEmpty() {
        Object[][] emptyArray = new Object[][]{
                {},
                {},
                {new Object[]{}}
        };

        int expectedLength = 0;
        int currentLength = ArrayUtils.deepLength(emptyArray);

        assertEquals(expectedLength, currentLength);
    }

    /**
     * Contains
     */

    @Test
    public void containsFound() {
        Object[] array = new Object[]{
                2,
                new Object[]{},
                1,
        };

        boolean found = ArrayUtils.contains(1, array);

        assertTrue(found);
    }

    @Test
    public void containsNotFound() {
        Object[] array = new Object[]{
                2,
                3,
                new Object[]{}
        };

        boolean found = ArrayUtils.contains(1, array);

        assertFalse(found);
    }

    @Test
    public void containsNotFoundSubArray() {
        Object[][] array = new Object[][]{
                {2},
                {new Object[]{1}},
                {3},
        };

        boolean found = ArrayUtils.contains(1, array);

        assertFalse(found);
    }

    /**
     * Deep Contains
     */

    @Test
    public void deepContainsFound() {
        Object[][] array = new Object[][]{
                {2},
                {3},
                {new Object[]{1}}
        };

        boolean found = ArrayUtils.deepContains(1, array);

        assertTrue(found);
    }

    @Test
    public void deepContainsNotFound() {
        Object[][] array = new Object[][]{
                {2},
                {3},
                {new Object[]{4}}
        };

        boolean found = ArrayUtils.deepContains(1, array);

        assertFalse(found);
    }

    @Test
    public void deepContainsFoundNoDeep() {
        Object[][] array = new Object[][]{
                {2},
                {1},
                {new Object[]{4}}
        };

        boolean found = ArrayUtils.deepContains(1, array);

        assertTrue(found);
    }

    @Test
    public void byteBoxing() {
    }
}