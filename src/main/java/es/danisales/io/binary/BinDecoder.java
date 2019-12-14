package es.danisales.io.binary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.BiFunction;

public final class BinDecoder<T> {
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

        BiFunction<ByteBuffer, DecoderSettings, boolean[]> booleanArray = (ByteBuffer byteBuffer, DecoderSettings settings) -> {
            int size = (int) settings.get("length");
            int byteSize = (size - 1) / 8 + 1;

            boolean[] ret = new boolean[size];
            outer:
            for (int byteNumber = 0; byteNumber < byteSize; byteNumber++) {
                byte b = byteBuffer.get();
                for (int pos = 0; pos < 8; pos++) {
                    ret[byteNumber * 8 + pos] = (b >> pos & 0x1) == 0x01;

                    if (byteNumber * 8 + pos == size - 1)
                        break outer;
                }
            }

            return ret;
        };

        map.put(boolean[].class, booleanArray);

        map.put(Boolean[].class, (ByteBuffer byteBuffer, DecoderSettings settings) -> {
            boolean[] array = booleanArray.apply(byteBuffer, settings);
            Boolean[] ret = new Boolean[array.length];
            int i = 0;
            for (boolean b : array)
                ret[i++] = b;

            return ret;
        });

        map.put(Map.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> {
            int size = byteBuffer.getInt();

            Class tKeyClass = (Class) settings.get("keyClass");
            Class tValueClass = (Class) settings.get("valueClass");

            Map map = new HashMap<>();

            for (int i = 0; i < size; i++) {
                @SuppressWarnings("unchecked")
                Object key = readObjectFromBuffer(tKeyClass, byteBuffer);
                @SuppressWarnings("unchecked")
                Object value = readObjectFromBuffer(tValueClass, byteBuffer);

                //noinspection unchecked
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

        map.put(ArrayList.class, BinDecoder::arrayListFunction);

        map.put(GregorianCalendar.class, (ByteBuffer byteBuffer, DecoderSettings settings) -> {
            TimeZone timeZone = null;
            if (settings != null)
                timeZone = (TimeZone) settings.get("timezone");
            if (timeZone == null)
                timeZone = TimeZone.getTimeZone("GMT");
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(byteBuffer.getLong());

            return calendar;
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> ArrayList<T> arrayListFunction(ByteBuffer byteBuffer, DecoderSettings settings) {
        int size = byteBuffer.getInt();
        Class<T> tClass = (Class<T>) settings.get("type");
        ArrayList<T> ret = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            T o = readObjectFromBuffer(tClass, byteBuffer);
            ret.add(o);
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    private static <T> T readObjectFromBuffer(Class<T> tClass, ByteBuffer byteBuffer) {
        return (T) map.get(tClass).apply(byteBuffer, null);
    }

    public static void register(Class<?> tClass, BiFunction<ByteBuffer, DecoderSettings, ?> function) {
        map.put(tClass, function);
    }

    private ByteBuffer fromBuffer;

    @SuppressWarnings("WeakerAccess")
    public static class DecoderSettings extends HashMap<String, Object> {
    }

    private Class<T> tClass;
    private DecoderSettings decoderSettings;

    BinDecoder(Class<T> tClass) {
        this.tClass = tClass;
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
                    Object newValue = BinData.decoder(field.getType())
                            .from(byteBuffer)
                            .getInstance();
                    field.set(obj, newValue);
                } catch (IllegalAccessException | IllegalArgumentException ignored) {
                }
            }
        }

        return obj;
    }

    public BinDecoder<T> from(@NonNull ByteBuffer byteBuffer) {
        fromBuffer = Objects.requireNonNull(byteBuffer);

        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public BinDecoder<T> setSettings(DecoderSettings settings) {
        this.decoderSettings = settings;

        return this;
    }

    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public T getInstance() {
        BiFunction<ByteBuffer, DecoderSettings, Object> consumer = (BiFunction<ByteBuffer, DecoderSettings, Object>) map.get(tClass);
        if (consumer == null) {
            return decodeFromBinAnnotation(tClass, fromBuffer);
        } else {
            return (T) consumer.apply(fromBuffer, decoderSettings);
        }
    }
}
