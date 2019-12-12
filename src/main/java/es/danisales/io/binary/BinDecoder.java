package es.danisales.io.binary;

import es.danisales.io.binary.types.Bin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public final class BinDecoder {
    private static Map<Class, BiFunction<ByteBuffer, DecoderSettings, ?>> map = new HashMap<>();

    static {
        map.put(Integer.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> byteBuffer.getInt());
        map.put(int.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> byteBuffer.getInt());
        map.put(Float.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> byteBuffer.getFloat());
        map.put(float.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> byteBuffer.getFloat());
        map.put(Double.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> byteBuffer.getDouble());
        map.put(double.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> byteBuffer.getDouble());
        map.put(Byte.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> byteBuffer.get());
        map.put(byte.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> byteBuffer.get());
        map.put(Boolean.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> byteBuffer.getInt());
        map.put(boolean.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> byteBuffer.getInt());

        map.put(boolean[].class, (ByteBuffer byteBuffer, DecoderSettings settings) -> {
            throw new UnsupportedOperationException(); // todo
        });

        map.put(Boolean[].class, (ByteBuffer byteBuffer, DecoderSettings settings) -> {
            throw new UnsupportedOperationException(); // todo
        });

        map.put(Map.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> {
            int size = byteBuffer.getInt();

            Class tKeyClass = (Class) settings.get("keyClass");
            Class tValueClass = (Class) settings.get("valueClass");

            Map map = new HashMap<>();

            for (int i = 0; i < size; i++) {
                Object key = readObjectFromBuffer(tKeyClass, byteBuffer);
                Object value = readObjectFromBuffer(tValueClass, byteBuffer);

                map.put(key, value);
            }

            return map;
        });

        map.put(String.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> {
            StringBuilder sb = new StringBuilder();
            char r;
            while (true) {
                r = (char) byteBuffer.get();
                if (r == 0)
                    break;
                sb.append(r);
            }

            return sb.toString();
        });
    }

    private static <T> T readObjectFromBuffer(Class<T> tClass, ByteBuffer byteBuffer) {
        return (T) map.get(tClass).apply(byteBuffer, null);
    }

    private BinDecoder() {
    }

    public static void register(Class<?> tClass, BiFunction<ByteBuffer, DecoderSettings, ?> function) {
        map.put(tClass, function);
    }

    private static @Nullable <T> T decodeFromBinAnnotation(Class<T> tClass, ByteBuffer byteBuffer) {
        T obj;
        try {
            obj = tClass.newInstance();
        } catch (Exception e) {
            return null;
        }
        for (Field field : tClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Bin.class)) {
                try {
                    Object newValue = BinDecoder.builder(field.getType())
                            .from(byteBuffer)
                            .get();
                    field.set(obj, newValue);
                } catch (IllegalAccessException | IllegalArgumentException ignored) {
                }
            }
        }

        return obj;
    }

    public static class DecoderSettings extends HashMap<String, Object> {
    }

    public static <T> Getter<T> builder(Class<T> tClass) {
        return new Getter<>(tClass);
    }

    public static class Getter<T> {
        private ByteBuffer fromBuffer;
        private Class<T> tClass;
        private DecoderSettings decoderSettings;

        private Getter(Class<T> tClass) {
            this.tClass = tClass;
        }

        public Getter<T> from(@NonNull ByteBuffer byteBuffer) {
            fromBuffer = Objects.requireNonNull(byteBuffer);

            return this;
        }

        public Getter<T> setSettings(DecoderSettings settings) {
            this.decoderSettings = settings;

            return this;
        }

        public T get() {
            BiFunction<ByteBuffer, DecoderSettings, Object> consumer = (BiFunction<ByteBuffer, DecoderSettings, Object>) map.get(tClass);
            if (consumer == null) {
                return decodeFromBinAnnotation(tClass, fromBuffer);
            } else {
                return (T) consumer.apply(fromBuffer, decoderSettings);
            }
        }
    }
}
