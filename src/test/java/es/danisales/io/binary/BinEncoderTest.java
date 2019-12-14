package es.danisales.io.binary;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SuppressWarnings("unused")
public class BinEncoderTest {
    @Test
    public void calendar() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(2019, Calendar.DECEMBER, 13, 21, 5, 0);

        byte[] encoded = BinData.encoder()
                .from(calendar)
                .getBytes();

        ByteBuffer read = ByteBuffer.wrap(encoded);
        assertEquals(calendar.getTimeInMillis(), read.getLong());
    }

    @Test
    public void booleanArray() {
        boolean[] array = new boolean[13];
        for (int i = 0; i < 13; i++)
            array[i] = i % 3 == 0 || i == 1 || i == 10;

        byte[] encoded = BinData.encoder()
                .from(array)
                .getBytes();

        assertEquals(2, encoded.length);

        assertEquals(0x01, (encoded[0] & 0x01)); // 0
        assertEquals(0x01, (encoded[0] >> 1 & 0x01)); // 1
        assertNotEquals(0x01, (encoded[0] >> 2 & 0x01));  // 2
        assertEquals(0x01, (encoded[0] >> 3 & 0x01)); // 3

        assertNotEquals(0x01, (encoded[0] >> 4 & 0x01)); // 4
        assertNotEquals(0x01, (encoded[0] >> 5 & 0x01)); // 5
        assertEquals(0x01, (encoded[0] >> 6 & 0x01)); // 6
        assertNotEquals(0x01, (encoded[0] >> 7 & 0x01)); // 7

        assertNotEquals(0x01, (encoded[1] & 0x01)); // 8
        assertEquals(0x01, (encoded[1] >> 1 & 0x01)); // 9
        assertEquals(0x01, (encoded[1] >> 2 & 0x01)); // 10
        assertNotEquals(0x01, (encoded[1] >> 3 & 0x01)); // 11

        assertEquals(0x01, (encoded[1] >> 4 & 0x01)); // 12
        assertNotEquals(0x01, (encoded[1] >> 5 & 0x01));
        assertNotEquals(0x01, (encoded[1] >> 6 & 0x01));
        assertNotEquals(0x01, (encoded[1] >> 7 & 0x01));
    }

    public static class PrimitiveTypesTests {
        @Test
        public void intParsing() {
            byte[] encoded = BinData.encoder()
                    .from(1)
                    .getBytes();

            ByteBuffer read = ByteBuffer.wrap(encoded);
            assertEquals(1, read.getInt());
        }

        @Test
        public void floatParsing() {
            byte[] encoded = BinData.encoder()
                    .from(1f)
                    .getBytes();

            ByteBuffer read = ByteBuffer.wrap(encoded);
            assertEquals(1f, read.getFloat(), 0.1f);
        }

        @Test
        public void doubleParsing() {
            byte[] encoded = BinData.encoder()
                    .from(1d)
                    .getBytes();

            ByteBuffer read = ByteBuffer.wrap(encoded);
            assertEquals(1d, read.getDouble(), 0.1d);
        }

        @Test
        public void byteParsing() {
            byte[] encoded = BinData.encoder()
                    .from((byte) 0x64)
                    .getBytes();

            ByteBuffer read = ByteBuffer.wrap(encoded);
            assertEquals(0x64, read.get());
        }
    }

    public static class BinAnnotationTests {
        @Test
        public void compountAnnotation() {
            byte[] encoded = BinData.encoder()
                    .from(new SimpleClassAnnotation())
                    .getBytes();

            ByteBuffer read = ByteBuffer.wrap(encoded);
            assertEquals(3, read.getInt());
            assertEquals(2.5f, read.getFloat(), 01.f);
        }

        @Test
        public void compountAnnotationCall() {
            byte[] encoded = BinData.encoder()
                    .from(new ComplexClassAnnotation())
                    .getBytes();

            ByteBuffer read = ByteBuffer.wrap(encoded);
            assertEquals(3, read.getInt());
            assertEquals(2.5f, read.getFloat(), 01.f);
            assertEquals(12.5f, read.getFloat(), 01.f);
        }

        @SuppressWarnings("unused")
        static class SimpleClassAnnotation {
            @Bin
            int a = 3;

            @Bin
            float b = 2.5f;

            float notParsedFloat = 123f;
            int notParsedInt = 123;
        }

        @SuppressWarnings("unused")
        static class ComplexClassAnnotation {
            @Bin
            SimpleClassAnnotation compoundAnnotation = new SimpleClassAnnotation();

            SimpleClassAnnotation notParsedcompoundAnnotation = new SimpleClassAnnotation();

            @Bin
            float b = 12.5f;

            float notParsedFloat = 123f;
            int notParsedInt = 123;
        }
    }

    public static class MapTests {
        @Test
        public void mapSimple() {
            Map<Integer, Integer> map = new HashMap<>();
            for (int i = 1; i <= 10; i++)
                map.put(i, i * 2);

            byte[] encoded = BinData.encoder()
                    .from(map)
                    .getBytes();

            ByteBuffer read = ByteBuffer.wrap(encoded);
            int size = read.getInt();
            assertEquals(10, size);
            for (int i = 1; i <= 10; i++) {
                int key = read.getInt();
                int value = read.getInt();
                assertEquals(i, key);
                assertEquals(i * 2, value);
            }
        }

        @Test
        public void mapComplex() {
            Map<Integer, BinAnnotationTests.SimpleClassAnnotation> map = new HashMap<>();
            for (int i = 1; i <= 3; i++) {
                BinAnnotationTests.SimpleClassAnnotation simpleClassAnnotation = new BinAnnotationTests.SimpleClassAnnotation();
                simpleClassAnnotation.a = i;
                simpleClassAnnotation.b = i * 2;
                map.put(i, simpleClassAnnotation);
            }

            byte[] encoded = BinData.encoder()
                    .from(map)
                    .getBytes();

            ByteBuffer read = ByteBuffer.wrap(encoded);
            int size = read.getInt();
            assertEquals(3, size);
            for (int i = 1; i <= 3; i++) {
                int key = read.getInt();
                int aSimpleClassAnnotation = read.getInt();
                float bSimpleClassAnnotation = read.getFloat();
                assertEquals(i, key);
                assertEquals(i, aSimpleClassAnnotation);
                assertEquals(i * 2, bSimpleClassAnnotation, .1f);
            }
        }
    }

    public static class StringTests {
        @Test
        public void string() {
            String str = "123456";
            byte[] encoded = BinData.encoder()
                    .from(str)
                    .getBytes();

            ByteBuffer read = ByteBuffer.wrap(encoded);
            assertEquals('1', (char) read.get());
            assertEquals('2', (char) read.get());
            assertEquals('3', (char) read.get());
            assertEquals('4', (char) read.get());
            assertEquals('5', (char) read.get());
            assertEquals('6', (char) read.get());
            assertEquals(0, (char) read.get());
        }
    }

    public static class ListTests {
        @Test
        public void arrayListInteger() {
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (int i = 1; i <= 10; i++)
                arrayList.add(i * 2);

            int binarySize = BinSize.getBinarySizeOf(arrayList);
            byte[] encoded = BinData.encoder()
                    .from(arrayList)
                    .getBytes();

            ByteBuffer read = ByteBuffer.wrap(encoded);
            assertEquals(10, read.getInt());
            assertEquals(2, read.getInt());
            assertEquals(4, read.getInt());
            assertEquals(6, read.getInt());
            assertEquals(8, read.getInt());
            assertEquals(10, read.getInt());
            assertEquals(12, read.getInt());
            assertEquals(14, read.getInt());
            assertEquals(16, read.getInt());
            assertEquals(18, read.getInt());
            assertEquals(20, read.getInt());
        }
    }
}