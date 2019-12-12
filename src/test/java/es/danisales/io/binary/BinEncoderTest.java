package es.danisales.io.binary;

import es.danisales.io.binary.types.Bin;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unused")
public class BinEncoderTest {
    public static class PrimitiveTypesTests {
        @Test
        public void intParsing() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            BinEncoder.builder()
                    .from(1)
                    .to(byteBuffer)
                    .get();

            byteBuffer.position(0);
            assertEquals(1, byteBuffer.getInt());
        }

        @Test
        public void floatParsing() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Float.BYTES);
            BinEncoder.builder()
                    .from(1f)
                    .to(byteBuffer)
                    .get();

            byteBuffer.position(0);
            assertEquals(1f, byteBuffer.getFloat(), 0.1f);
        }

        @Test
        public void doubleParsing() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
            BinEncoder.builder()
                    .from(1d)
                    .to(byteBuffer)
                    .get();

            byteBuffer.position(0);
            assertEquals(1d, byteBuffer.getDouble(), 0.1d);
        }

        @Test
        public void byteParsing() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Byte.BYTES);
            BinEncoder.builder()
                    .from((byte) 0x64)
                    .to(byteBuffer)
                    .get();

            byteBuffer.position(0);
            assertEquals(0x64, byteBuffer.get());
        }
    }

    public static class BinAnnotationTests {
        @Test
        public void compountAnnotation() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + Float.BYTES);
            BinEncoder.builder()
                    .from(new SimpleClassAnnotation())
                    .to(byteBuffer)
                    .get();

            byteBuffer.position(0);
            assertEquals(3, byteBuffer.getInt());
            assertEquals(2.5f, byteBuffer.getFloat(), 01.f);
        }

        @Test
        public void compountAnnotationCall() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + Float.BYTES + Float.BYTES);
            BinEncoder.builder()
                    .from(new ComplexClassAnnotation())
                    .to(byteBuffer)
                    .get();

            byteBuffer.position(0);
            assertEquals(3, byteBuffer.getInt());
            assertEquals(2.5f, byteBuffer.getFloat(), 01.f);
            assertEquals(12.5f, byteBuffer.getFloat(), 01.f);
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
        public void mapSimpleBinarySize() {
            Map<Integer, Integer> map = new HashMap<>();
            for (int i = 1; i <= 10; i++)
                map.put(i, i * 2);

            int binarySize = BinEncoder.getBinarySizeOf(map);
            assertEquals(Integer.BYTES + 10 * (Integer.BYTES + Integer.BYTES), binarySize);
        }

        @Test
        public void mapSimple() {
            Map<Integer, Integer> map = new HashMap<>();
            for (int i = 1; i <= 10; i++)
                map.put(i, i * 2);

            ByteBuffer byteBuffer = ByteBuffer.allocate(BinEncoder.getBinarySizeOf(map));
            BinEncoder.builder()
                    .from(map)
                    .to(byteBuffer)
                    .get();

            byteBuffer.position(0);
            int size = byteBuffer.getInt();
            assertEquals(10, size);
            for (int i = 1; i <= 10; i++) {
                int key = byteBuffer.getInt();
                int value = byteBuffer.getInt();
                assertEquals(i, key);
                assertEquals(i * 2, value);
            }
        }

        @Test
        public void mapComplexBinarySize() {
            Map<Integer, BinAnnotationTests.SimpleClassAnnotation> map = new HashMap<>();
            for (int i = 1; i <= 3; i++) {
                BinAnnotationTests.SimpleClassAnnotation simpleClassAnnotation = new BinAnnotationTests.SimpleClassAnnotation();
                simpleClassAnnotation.a = i;
                simpleClassAnnotation.b = i * 2;
                map.put(i, simpleClassAnnotation);
            }

            int binarySizeMap = BinEncoder.getBinarySizeOf(map);
            assertEquals(Integer.BYTES + 3 * (Integer.BYTES + Integer.BYTES + Float.BYTES), binarySizeMap);
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

            int sizeBytesMap = BinEncoder.getBinarySizeOf(map);
            ByteBuffer byteBuffer = ByteBuffer.allocate(sizeBytesMap);
            BinEncoder.builder()
                    .from(map)
                    .to(byteBuffer)
                    .get();

            byteBuffer.position(0);
            int size = byteBuffer.getInt();
            assertEquals(3, size);
            for (int i = 1; i <= 3; i++) {
                int key = byteBuffer.getInt();
                int aSimpleClassAnnotation = byteBuffer.getInt();
                float bSimpleClassAnnotation = byteBuffer.getFloat();
                assertEquals(i, key);
                assertEquals(i, aSimpleClassAnnotation);
                assertEquals(i * 2, bSimpleClassAnnotation, .1f);
            }
        }
    }

    public static class StringTests {
        @Test
        public void stringBinarySize() {
            String str = "123456";
            int binarySize = BinEncoder.getBinarySizeOf(str);
            assertEquals(7, binarySize);
        }

        @Test
        public void string() {
            String str = "123456";
            ByteBuffer byteBuffer = ByteBuffer.allocate(BinEncoder.getBinarySizeOf(str));
            BinEncoder.builder()
                    .from(str)
                    .to(byteBuffer)
                    .get();

            byteBuffer.position(0);

            assertEquals('1', (char) byteBuffer.get());
            assertEquals('2', (char) byteBuffer.get());
            assertEquals('3', (char) byteBuffer.get());
            assertEquals('4', (char) byteBuffer.get());
            assertEquals('5', (char) byteBuffer.get());
            assertEquals('6', (char) byteBuffer.get());
            assertEquals(0, (char) byteBuffer.get());
        }
    }
}