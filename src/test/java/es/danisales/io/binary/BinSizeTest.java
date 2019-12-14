package es.danisales.io.binary;

import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;

public class BinSizeTest {
    @Test
    public void mapSimpleBinarySize() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 1; i <= 10; i++)
            map.put(i, i * 2);

        int binarySize = BinSize.getBinarySizeOf(map);
        assertEquals(Integer.BYTES + 10 * (Integer.BYTES + Integer.BYTES), binarySize);
    }

    @Test
    public void mapComplexBinarySize() {
        Map<Integer, BinEncoderTest.BinAnnotationTests.SimpleClassAnnotation> map = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            BinEncoderTest.BinAnnotationTests.SimpleClassAnnotation simpleClassAnnotation = new BinEncoderTest.BinAnnotationTests.SimpleClassAnnotation();
            simpleClassAnnotation.a = i;
            simpleClassAnnotation.b = i * 2;
            map.put(i, simpleClassAnnotation);
        }

        int binarySizeMap = BinSize.getBinarySizeOf(map);
        assertEquals(Integer.BYTES + 3 * (Integer.BYTES + Integer.BYTES + Float.BYTES), binarySizeMap);
    }

    @Test
    public void stringBinarySize() {
        String str = "123456";
        int binarySize = BinSize.getBinarySizeOf(str);
        assertEquals(7, binarySize);
    }

    @Test
    public void arrayListIntegerSize() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 1; i <= 10; i++)
            arrayList.add(i * 2);

        BinEncoder.EncoderSettings encoderSettings = new BinEncoder.EncoderSettings();
        encoderSettings.put("type", Integer.class);
        int size = BinSize.getBinarySizeOf(arrayList, encoderSettings);

        assertEquals(Integer.BYTES + 10 * Integer.BYTES, size);
    }

    @Test
    public void calendarSize() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(2019, Calendar.DECEMBER, 13, 21, 5, 0);

        assertEquals(Long.BYTES, BinSize.getBinarySizeOf(calendar));
    }

    @Test
    public void booleanArraySize() {
        boolean[] array = new boolean[13];
        for (int i = 0; i < 13; i++)
            array[i] = i % 3 == 0 || i == 1 || i == 10;

        assertEquals(2, BinSize.getBinarySizeOf(array));
    }
}