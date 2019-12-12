package es.danisales.io.binary;

import es.danisales.io.binary.types.Bin;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class BinDecoderTest {

    @Test
    public void intParsing() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
        byteBuffer.putInt(1);
        byteBuffer.position(0);

        int ret = BinDecoder.builder(Integer.class)
                .from(byteBuffer)
                .get();

        assertEquals(1, ret);
    }

    @Test
    public void floatParsing() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Float.BYTES);
        byteBuffer.putFloat(1.1f);
        byteBuffer.position(0);

        float ret = BinDecoder.builder(Float.class)
                .from(byteBuffer)
                .get();

        assertEquals(1.1f, ret, 0.01f);
    }

    @Test
    public void doubleParsing() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
        byteBuffer.putDouble(1.1d);
        byteBuffer.position(0);

        double ret = BinDecoder.builder(Double.class)
                .from(byteBuffer)
                .get();

        assertEquals(1.1d, ret, 0.01d);
    }

    @Test
    public void byteParsing() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Byte.BYTES);
        byteBuffer.put((byte) 0x64);
        byteBuffer.position(0);

        byte ret = BinDecoder.builder(Byte.class)
                .from(byteBuffer)
                .get();

        assertEquals((byte) 0x64, ret, 0.01f);
    }

    @Test
    public void compountAnnotation() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + Float.BYTES);
        SimpleClassAnnotation simpleClassAnnotation = new SimpleClassAnnotation();
        simpleClassAnnotation.a = 3;
        simpleClassAnnotation.b = 2.5f;
        simpleClassAnnotation.notParsedFloat = 123f;
        simpleClassAnnotation.notParsedInt = 123;
        BinEncoder.builder()
                .from(simpleClassAnnotation)
                .to(byteBuffer)
                .get();
        byteBuffer.position(0);

        SimpleClassAnnotation ret = BinDecoder.builder(SimpleClassAnnotation.class)
                .from(byteBuffer)
                .get();

        assertEquals(3, ret.a);
        assertEquals(2.5f, ret.b, 0.01f);
        assertEquals(0, ret.notParsedInt);
        assertEquals(0, ret.notParsedFloat, 0.01f);
    }

    @Test
    public void compountAnnotationCall() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + Float.BYTES + Float.BYTES);
        ComplexClassAnnotation complexClassAnnotation = new ComplexClassAnnotation();
        SimpleClassAnnotation simpleClassAnnotation = new SimpleClassAnnotation();
        simpleClassAnnotation.a = 3;
        simpleClassAnnotation.b = 2.5f;
        simpleClassAnnotation.notParsedFloat = 123f;
        simpleClassAnnotation.notParsedInt = 123;
        complexClassAnnotation.compoundAnnotation = simpleClassAnnotation;
        complexClassAnnotation.notParsedcompoundAnnotation = new SimpleClassAnnotation();
        complexClassAnnotation.b = 12.5f;
        complexClassAnnotation.notParsedFloat = 123f;
        complexClassAnnotation.notParsedInt = 123;

        BinEncoder.builder()
                .from(complexClassAnnotation)
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
        int a;

        @Bin
        float b;

        float notParsedFloat;
        int notParsedInt;
    }

    @SuppressWarnings("unused")
    static class ComplexClassAnnotation {
        @Bin
        SimpleClassAnnotation compoundAnnotation;

        SimpleClassAnnotation notParsedcompoundAnnotation;

        @Bin
        float b;

        float notParsedFloat;
        int notParsedInt;
    }
}