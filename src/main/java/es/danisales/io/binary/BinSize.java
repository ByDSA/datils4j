package es.danisales.io.binary;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public final class BinSize {
    private static Map<Class, BiFunction<?, BinEncoder.EncoderSettings, Integer>> mapSize = new HashMap<>();

    static {
        mapSize.put(Integer.class, (Integer i, BinEncoder.EncoderSettings settings) -> Integer.BYTES);
        mapSize.put(int.class, (Integer i, BinEncoder.EncoderSettings settings) -> Integer.BYTES);
        mapSize.put(Double.class, (Double i, BinEncoder.EncoderSettings settings) -> Double.BYTES);
        mapSize.put(double.class, (Double i, BinEncoder.EncoderSettings settings) -> Double.BYTES);
        mapSize.put(Float.class, (Float i, BinEncoder.EncoderSettings settings) -> Float.BYTES);
        mapSize.put(float.class, (Float i, BinEncoder.EncoderSettings settings) -> Float.BYTES);
        mapSize.put(Byte.class, (Byte i, BinEncoder.EncoderSettings settings) -> Byte.BYTES);
        mapSize.put(byte.class, (Byte i, BinEncoder.EncoderSettings settings) -> Byte.BYTES);

        mapSize.put(boolean[].class, (boolean[] self, BinEncoder.EncoderSettings settings) -> {
            int s = self.length;
            return s == 0 ? 0 : s / 8 + 1;
        });
        mapSize.put(Boolean[].class, (Boolean[] self, BinEncoder.EncoderSettings settings) -> {
            int s = self.length;
            return s == 0 ? 0 : s / 8 + 1;
        });

        BiFunction<Map<?, ?>, BinEncoder.EncoderSettings, Integer> sizeMapFunction = (Map<?, ?> self, BinEncoder.EncoderSettings settings) -> {
            int s = Integer.BYTES;
            for (Map.Entry<?, ?> e : self.entrySet()) {
                s += getBinarySizeOf(e.getKey());
                s += getBinarySizeOf(e.getValue());
            }

            return s;
        };

        mapSize.put(Map.class, sizeMapFunction);
        mapSize.put(HashMap.class, sizeMapFunction);
        mapSize.put(ConcurrentHashMap.class, sizeMapFunction);

        mapSize.put(String.class, (String self, BinEncoder.EncoderSettings settings) -> {
            Charset charset = (Charset) BinEncoder.EncoderSettings.getOrNull(settings, "charset");
            if (charset == null)
                charset = Charset.defaultCharset();

            return self.getBytes(charset).length + 1;
        });

        mapSize.put(ArrayList.class, (ArrayList self, BinEncoder.EncoderSettings settings) -> {
            int s = Integer.BYTES;
            for (Object o : self)
                s += getBinarySizeOf(o);

            return s;
        });

        mapSize.put(GregorianCalendar.class, (GregorianCalendar self, BinEncoder.EncoderSettings settings) -> Long.BYTES);
    }

    private BinSize() {
    }

    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public static <T> int getBinarySizeOf(T o, BinEncoder.EncoderSettings settings) {
        Class<T> tClass = (Class<T>) o.getClass();
        BiFunction<T, BinEncoder.EncoderSettings, Integer> function = (BiFunction<T, BinEncoder.EncoderSettings, Integer>) mapSize.get(tClass);
        if (function != null)
            return function.apply(o, settings);

        int s = binarySizeFromBinAnnotation(o);
        if (s >= 0)
            return s;
        else
            throw new BinarySizeUndefinedException(tClass);
    }

    public static void register(Class<?> tClass, BiFunction<?, BinEncoder.EncoderSettings, Integer> function) {
        mapSize.put(tClass, function);
    }

    private static int binarySizeFromBinAnnotation(Object fromObject) {
        int s = 0;
        boolean done = false;
        Class<?> clazz = fromObject.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Bin.class)) {
                try {
                    s += getBinarySizeOf(field.get(fromObject));
                    done = true;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!done)
            return -1;
        return s;
    }

    public static <T> int getBinarySizeOf(T o) {
        return getBinarySizeOf(o, null);
    }

    static class BinarySizeUndefinedException extends RuntimeException {
        BinarySizeUndefinedException(Class tClass) {
            super(tClass.getName());
        }
    }
}
