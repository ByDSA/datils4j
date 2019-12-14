package es.danisales.io.binary;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BinDecoderTest {

    @Test
    public void intParsing() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
        byteBuffer.putInt(1);
        byteBuffer.position(0);

        int ret = BinData.decoder(Integer.class)
                .from(byteBuffer)
                .getInstance();

        assertEquals(1, ret);
    }

    @Test
    public void floatParsing() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Float.BYTES);
        byteBuffer.putFloat(1.1f);
        byteBuffer.position(0);

        float ret = BinData.decoder(Float.class)
                .from(byteBuffer)
                .getInstance();

        assertEquals(1.1f, ret, 0.01f);
    }

    @Test
    public void doubleParsing() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
        byteBuffer.putDouble(1.1d);
        byteBuffer.position(0);

        double ret = BinData.decoder(Double.class)
                .from(byteBuffer)
                .getInstance();

        assertEquals(1.1d, ret, 0.01d);
    }

    @Test
    public void byteParsing() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Byte.BYTES);
        byteBuffer.put((byte) 0x64);
        byteBuffer.position(0);

        byte ret = BinData.decoder(Byte.class)
                .from(byteBuffer)
                .getInstance();

        assertEquals((byte) 0x64, ret, 0.01f);
    }

    @Test
    public void compountAnnotation() {
        SimpleClassAnnotation simpleClassAnnotation = new SimpleClassAnnotation();
        simpleClassAnnotation.a = 3;
        simpleClassAnnotation.b = 2.5f;
        simpleClassAnnotation.notParsedFloat = 123f;
        simpleClassAnnotation.notParsedInt = 123;
        byte[] encoded = BinData.encoder()
                .from(simpleClassAnnotation)
                .getBytes();

        ByteBuffer byteBuffer = ByteBuffer.wrap(encoded);
        SimpleClassAnnotation ret = BinData.decoder(SimpleClassAnnotation.class)
                .from(byteBuffer)
                .getInstance();

        assertEquals(3, ret.a);
        assertEquals(2.5f, ret.b, 0.01f);
        assertEquals(0, ret.notParsedInt);
        assertEquals(0, ret.notParsedFloat, 0.01f);
    }

    @Test
    public void compountAnnotationCall() {
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

        byte[] encoded = BinData.encoder()
                .from(complexClassAnnotation)
                .getBytes();
        ByteBuffer byteBuffer = ByteBuffer.wrap(encoded);
        assertEquals(3, byteBuffer.getInt());
        assertEquals(2.5f, byteBuffer.getFloat(), .1f);
        assertEquals(12.5f, byteBuffer.getFloat(), .1f);
    }

    @Test
    public void map() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + 10 * (Integer.BYTES + Integer.BYTES));
        byteBuffer.putInt(10);
        for (int i = 1; i <= 10; i++) {
            byteBuffer.putInt(i);
            byteBuffer.putInt(i * 2);
        }
        byteBuffer.position(0);

        BinDecoder.DecoderSettings decoderSettings = new BinDecoder.DecoderSettings();
        decoderSettings.put("keyClass", Integer.class);
        decoderSettings.put("valueClass", Integer.class);

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> map = (Map<Integer, Integer>) BinData.decoder(Map.class)
                .from(byteBuffer)
                .setSettings(decoderSettings)
                .getInstance();

        assertEquals(10, map.size());
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            assertEquals(entry.getKey() * 2, (int) entry.getValue());
        }
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

    @Test
    public void string() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(7);
        byteBuffer.put((byte) 49);
        byteBuffer.put((byte) 50);
        byteBuffer.put((byte) 51);
        byteBuffer.put((byte) 52);
        byteBuffer.put((byte) 53);
        byteBuffer.put((byte) 54);
        byteBuffer.put((byte) 0);

        byteBuffer.position(0);

        String str = BinData.decoder(String.class)
                .from(byteBuffer)
                .getInstance();

        assertEquals("123456", str);
    }

    @Test
    public void arrayList() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(11 * Integer.BYTES);
        byteBuffer.putInt(10);
        byteBuffer.putInt(2);
        byteBuffer.putInt(4);
        byteBuffer.putInt(6);
        byteBuffer.putInt(8);
        byteBuffer.putInt(10);
        byteBuffer.putInt(12);
        byteBuffer.putInt(14);
        byteBuffer.putInt(16);
        byteBuffer.putInt(18);
        byteBuffer.putInt(20);

        byteBuffer.position(0);

        BinDecoder.DecoderSettings decoderSettings = new BinDecoder.DecoderSettings();
        decoderSettings.put("type", Integer.class);
        @SuppressWarnings("unchecked")
        ArrayList<Integer> decoded = BinData.decoder(ArrayList.class)
                .from(byteBuffer)
                .setSettings(decoderSettings)
                .getInstance();

        assertEquals(10, decoded.size());
        for (int i = 0; i < 10; i++)
            assertEquals((i + 1) * 2, (int) decoded.get(i));
    }

    @Test
    public void calendar() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.putLong(1576271100940L);

        byteBuffer.position(0);
        GregorianCalendar gregorianCalendar = BinData.decoder(GregorianCalendar.class)
                .from(byteBuffer)
                .getInstance();

        assertEquals(2019, gregorianCalendar.get(Calendar.YEAR));
        assertEquals(Calendar.DECEMBER, gregorianCalendar.get(Calendar.MONTH));
        assertEquals(13, gregorianCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(21, gregorianCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(5, gregorianCalendar.get(Calendar.MINUTE));
        assertEquals(0, gregorianCalendar.get(Calendar.SECOND));
    }

    @Test
    public void booleanArray() {
        byte[] binary = new byte[2];
        binary[0] = (byte) 0x4B; // 0100 1011
        binary[1] = (byte) 0x16; // 0001 0110

        BinDecoder.DecoderSettings decoderSettings = new BinDecoder.DecoderSettings();
        decoderSettings.put("length", 13);
        boolean[] array = BinData.decoder(boolean[].class)
                .from(ByteBuffer.wrap(binary))
                .setSettings(decoderSettings)
                .getInstance();

        for (int i = 0; i < array.length; i++)
            assertEquals(Integer.toString(i), i % 3 == 0 || i == 1 || i == 10, array[i]);
    }

    @Test
    public void booleanWrapArray() {
        byte[] binary = new byte[2];
        binary[0] = (byte) 0x4B; // 0100 1011
        binary[1] = (byte) 0x16; // 0001 0110

        BinDecoder.DecoderSettings decoderSettings = new BinDecoder.DecoderSettings();
        decoderSettings.put("length", 13);
        Boolean[] array = BinData.decoder(Boolean[].class)
                .from(ByteBuffer.wrap(binary))
                .setSettings(decoderSettings)
                .getInstance();

        for (int i = 0; i < array.length; i++)
            assertEquals(Integer.toString(i), i % 3 == 0 || i == 1 || i == 10, array[i]);
    }
}