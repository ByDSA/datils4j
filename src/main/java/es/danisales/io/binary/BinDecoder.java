package es.danisales.io.binary;

import es.danisales.io.binary.types.Bin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class BinDecoder {
    private static Map<Class, Function<ByteBuffer, ?>> map = new HashMap<>();

    static {
        map.put(Integer.class, ByteBuffer::getInt);
        map.put(int.class, ByteBuffer::getInt);
        map.put(Float.class, ByteBuffer::getFloat);
        map.put(float.class, ByteBuffer::getFloat);
        map.put(Double.class, ByteBuffer::getDouble);
        map.put(double.class, ByteBuffer::getDouble);
        map.put(Byte.class, ByteBuffer::get);
        map.put(byte.class, ByteBuffer::get);
    }

    private BinDecoder() {
    }

    public static void register(Class<?> tClass, Function<ByteBuffer, ?> function) {
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

    public static <T> Getter<T> builder(Class<T> tClass) {
        return new Getter<>(tClass);
    }

    public static class Getter<T> {
        private ByteBuffer fromBuffer;
        private Class<T> tClass;

        private Getter(Class<T> tClass) {
            this.tClass = tClass;
        }

        public Getter<T> from(@NonNull ByteBuffer byteBuffer) {
            fromBuffer = Objects.requireNonNull(byteBuffer);

            return this;
        }

        public T get() {
            Function<ByteBuffer, Object> consumer = (Function<ByteBuffer, Object>) map.get(tClass);
            if (consumer == null) {
                return decodeFromBinAnnotation(tClass, fromBuffer);
            } else {
                return (T) consumer.apply(fromBuffer);
            }
        }
    }
}
