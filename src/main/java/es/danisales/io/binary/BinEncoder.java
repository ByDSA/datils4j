package es.danisales.io.binary;

import es.danisales.io.binary.types.Bin;
import es.danisales.utils.PrimitiveTypes;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static com.google.common.base.Preconditions.checkState;

public final class BinEncoder {
    private static Map<Class, BiConsumer<?, EncoderSettings>> mapEncoder = new HashMap<>();
    private static Map<Class, BiFunction<?, EncoderSettings, Integer>> mapSize = new HashMap<>();

    static {
        mapEncoder.put(Integer.class, (Integer self, EncoderSettings settings) -> settings.byteBuffer.putInt(self));
        mapSize.put(Integer.class, (Integer i, EncoderSettings settings) -> Integer.BYTES);
        mapEncoder.put(int.class, (Integer self, EncoderSettings settings) -> settings.byteBuffer.putInt(self));
        mapSize.put(int.class, (Integer i, EncoderSettings settings) -> Integer.BYTES);

        mapEncoder.put(Double.class, (Double self, EncoderSettings settings) -> settings.byteBuffer.putDouble(self));
        mapSize.put(Double.class, (Double i, EncoderSettings settings) -> Double.BYTES);
        mapEncoder.put(double.class, (Double self, EncoderSettings settings) -> settings.byteBuffer.putDouble(self));
        mapSize.put(double.class, (Double i, EncoderSettings settings) -> Double.BYTES);

        mapEncoder.put(Float.class, (Float self, EncoderSettings settings) -> settings.byteBuffer.putFloat(self));
        mapSize.put(Float.class, (Float i, EncoderSettings settings) -> Float.BYTES);
        mapEncoder.put(float.class, (Float self, EncoderSettings settings) -> settings.byteBuffer.putFloat(self));
        mapSize.put(float.class, (Float i, EncoderSettings settings) -> Float.BYTES);

        mapEncoder.put(Byte.class, (Byte self, EncoderSettings settings) -> settings.byteBuffer.put(self));
        mapSize.put(Byte.class, (Byte i, EncoderSettings settings) -> Byte.BYTES);
        mapEncoder.put(byte.class, (Byte self, EncoderSettings settings) -> settings.byteBuffer.put(self));
        mapSize.put(byte.class, (Byte i, EncoderSettings settings) -> Byte.BYTES);

        mapEncoder.put(boolean[].class, (boolean[] self, EncoderSettings settings) -> {
            int n = -1;
            byte[] encodedBytes = new byte[getBinarySizeOf(self)];
            for (boolean b : self) {
                n++;
                if (!b)
                    continue;

                switch (n % 8) {
                    case 0:
                        encodedBytes[n / 8] |= 0x1;
                        break;
                    case 1:
                        encodedBytes[n / 8] |= 0x2;
                        break;
                    case 2:
                        encodedBytes[n / 8] |= 0x4;
                        break;
                    case 3:
                        encodedBytes[n / 8] |= 0x8;
                        break;
                    case 4:
                        encodedBytes[n / 8] |= 0x10;
                        break;
                    case 5:
                        encodedBytes[n / 8] |= 0x20;
                        break;
                    case 6:
                        encodedBytes[n / 8] |= 0x40;
                        break;
                    case 7:
                        encodedBytes[n / 8] |= 0x80;
                        break;
                }
            }
            settings.byteBuffer.put(encodedBytes);
        });
        mapSize.put(boolean[].class, (boolean[] self, EncoderSettings settings) -> {
            int s = self.length;
            return s == 0 ? 0 : s / 8 + 1;
        });
        mapSize.put(Boolean[].class, (Boolean[] self, EncoderSettings settings) -> {
            int s = self.length;
            return s == 0 ? 0 : s / 8 + 1;
        });
        mapEncoder.put(Boolean[].class, (Boolean[] self, EncoderSettings settings) -> {
            boolean[] self2 = PrimitiveTypes.getPrimitiveArrayFrom(self);

            @SuppressWarnings("unchecked")
            BiConsumer<boolean[], EncoderSettings> biConsumer = (BiConsumer<boolean[], EncoderSettings>) mapEncoder.get(boolean[].class);
            biConsumer.accept(self2, settings);
        });

        BiConsumer<Map<?, ?>, EncoderSettings> encoderMapFunction = (Map<?, ?> self, EncoderSettings settings) -> {
            settings.byteBuffer.putInt(self.size());

            for (Map.Entry<?, ?> e : self.entrySet()) {
                builder()
                        .from(e.getKey())
                        .to(settings.byteBuffer)
                        .get();

                builder()
                        .from(e.getValue())
                        .to(settings.byteBuffer)
                        .get();
            }
        };

        mapEncoder.put(Map.class, encoderMapFunction);
        mapEncoder.put(HashMap.class, encoderMapFunction);
        mapEncoder.put(ConcurrentHashMap.class, encoderMapFunction);

        BiFunction<Map<?, ?>, EncoderSettings, Integer> sizeMapFunction = (Map<?, ?> self, EncoderSettings settings) -> {
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

        mapEncoder.put(String.class, (String self, EncoderSettings settings) -> {
            Charset charset = (Charset) settings.get("charset");
            if (charset == null)
                charset = Charset.defaultCharset();

            settings.byteBuffer.put(self.getBytes(charset));
            settings.byteBuffer.put((byte) 0);
        });

        mapSize.put(String.class, (String self, EncoderSettings settings) -> {
            Charset charset = (Charset) settingsGet(settings, "charset");
            if (charset == null)
                charset = Charset.defaultCharset();

            return self.getBytes(charset).length + 1;
        });
    }

    private static @Nullable Object settingsGet(EncoderSettings settings, String key) {
        if (settings == null)
            return null;

        return settings.get(key);
    }

    private BinEncoder() {
    }

    public static void register(Class<?> tClass, BiConsumer<?, EncoderSettings> consumer) {
        mapEncoder.put(tClass, consumer);
    }

    public static void registerSize(Class<?> tClass, BiFunction<?, EncoderSettings, Integer> function) {
        mapSize.put(tClass, function);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> int getBinarySizeOf(T o, EncoderSettings settings) {
        Class<T> tClass = (Class<T>) o.getClass();
        BiFunction<T, EncoderSettings, Integer> function = (BiFunction<T, EncoderSettings, Integer>) mapSize.get(tClass);
        if (function != null)
            return function.apply(o, settings);

        int s = binarySizeFromBinAnnotation(o);
        if (s >= 0)
            return s;
        else
            throw new BinarySizeUndefinedException(tClass);
    }

    public static <T> int getBinarySizeOf(T o) {
        return getBinarySizeOf(o, null);
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

    private static boolean encodeFromBinAnnotation(Object fromObject, EncoderSettings settings) {
        boolean done = false;
        Class<?> clazz = fromObject.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Bin.class)) {
                try {
                    BinEncoder.builder()
                            .from(field.get(fromObject))
                            .to(settings.byteBuffer)
                            .get();
                    done = true;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return done;
    }

    public static @Nullable <T> BiConsumer<T, EncoderSettings> getEncondingFunction(Class<T> tClass) {
        return (BiConsumer<T, EncoderSettings>) mapEncoder.get(tClass);
    }

    public static class EncoderSettings extends HashMap<String, Object> {
        ByteBuffer byteBuffer;

        public EncoderSettings() {
        }
    }


    public static <T> Getter<T> builder() {
        return new Getter<T>();
    }

    public static class Getter<T> {
        private T fromObject;
        private EncoderSettings settings = new EncoderSettings();

        private Getter() {
        }

        public Getter<T> from(@NonNull T o) {
            fromObject = Objects.requireNonNull(o);

            return this;
        }

        public Getter<T> to(@NonNull ByteBuffer byteBuffer) {
            settings.byteBuffer = Objects.requireNonNull(byteBuffer);

            return this;
        }

        @SuppressWarnings("unchecked")
        public void get() {
            checkState(settings.byteBuffer != null);

            Class<T> tClass = (Class<T>) fromObject.getClass();
            BiConsumer<T, EncoderSettings> consumer = getEncondingFunction(tClass);
            if (consumer == null) {
                if (!encodeFromBinAnnotation(fromObject, settings))
                    throw new EncoderNotFoundException(tClass);
            } else
                consumer.accept(fromObject, settings);
        }
    }

    public static class EncoderNotFoundException extends RuntimeException {
        public EncoderNotFoundException(Class tClass) {
            super(tClass.getName());
        }
    }

    public static class BinarySizeUndefinedException extends RuntimeException {
        public BinarySizeUndefinedException(Class tClass) {
            super(tClass.getName());
        }
    }
}
