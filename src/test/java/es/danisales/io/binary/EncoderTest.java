package es.danisales.io.binary;

import es.danisales.io.binary.types.Bin;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class EncoderTest {

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