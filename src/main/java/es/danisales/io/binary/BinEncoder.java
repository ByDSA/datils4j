package es.danisales.io.binary;

import es.danisales.io.binary.types.Bin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class BinEncoder {
    private static Map<Class, BiConsumer<?, ByteBuffer>> map = new HashMap<>();

    static {
        map.put(Integer.class, (Integer self, ByteBuffer byteBuffer) -> byteBuffer.putInt(self));
        map.put(int.class, (Integer self, ByteBuffer byteBuffer) -> byteBuffer.putInt(self));

        map.put(Double.class, (Double self, ByteBuffer byteBuffer) -> byteBuffer.putDouble(self));
        map.put(double.class, (Double self, ByteBuffer byteBuffer) -> byteBuffer.putDouble(self));

        map.put(Float.class, (Float self, ByteBuffer byteBuffer) -> byteBuffer.putFloat(self));
        map.put(float.class, (Float self, ByteBuffer byteBuffer) -> byteBuffer.putFloat(self));

        map.put(Byte.class, (Byte self, ByteBuffer byteBuffer) -> byteBuffer.put(self));
        map.put(byte.class, (Byte self, ByteBuffer byteBuffer) -> byteBuffer.put(self));
    }

    private BinEncoder() {
    }

    public static void register(Class<?> tClass, BiConsumer<?, ByteBuffer> consumer) {
        map.put(tClass, consumer);
    }

    private static void encodeFromBinAnnotation(Object fromObject, ByteBuffer byteBuffer) {
        Class<?> clazz = fromObject.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Bin.class)) {
                try {
                    BinEncoder.builder()
                            .from(field.get(fromObject))
                            .to(byteBuffer)
                            .get();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Getter builder() {
        return new Getter();
    }

    public static class Getter {
        private Object fromObject;
        private ByteBuffer toBuffer;

        private Getter() {
        }

        public Getter from(@NonNull Object o) {
            fromObject = Objects.requireNonNull(o);

            return this;
        }

        public Getter to(@NonNull ByteBuffer byteBuffer) {
            toBuffer = Objects.requireNonNull(byteBuffer);

            return this;
        }

        public void get() {
            Class tClass = fromObject.getClass();
            BiConsumer<Object, ByteBuffer> consumer = (BiConsumer<Object, ByteBuffer>) map.get(tClass);
            if (consumer == null)
                encodeFromBinAnnotation(fromObject, toBuffer);
            else
                consumer.accept(fromObject, toBuffer);
        }
    }
}
