package es.danisales.io.binary;

import es.danisales.utils.PrimitiveTypes;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import static com.google.common.base.Preconditions.checkState;

public final class BinEncoder<T> {
    private static Map<Class, BiConsumer<?, EncoderSettings>> mapEncoder = new HashMap<>();

    static {
        mapEncoder.put(Integer.class, (Integer self, EncoderSettings settings) -> writeSecure(settings.dataOutputStream, self));
        mapEncoder.put(int.class, (Integer self, EncoderSettings settings) -> writeSecure(settings.dataOutputStream, self));

        mapEncoder.put(Double.class, (Double self, EncoderSettings settings) -> writeSecure(settings.dataOutputStream, self));
        mapEncoder.put(double.class, (Double self, EncoderSettings settings) -> writeSecure(settings.dataOutputStream, self));

        mapEncoder.put(Float.class, (Float self, EncoderSettings settings) -> writeSecure(settings.dataOutputStream, self));
        mapEncoder.put(float.class, (Float self, EncoderSettings settings) -> writeSecure(settings.dataOutputStream, self));

        mapEncoder.put(Byte.class, (Byte self, EncoderSettings settings) -> writeSecure(settings.dataOutputStream, self));
        mapEncoder.put(byte.class, (Byte self, EncoderSettings settings) -> writeSecure(settings.dataOutputStream, self));

        mapEncoder.put(boolean[].class, (boolean[] self, EncoderSettings settings) -> {
            int n = -1;
            byte[] encodedBytes = new byte[BinSize.getBinarySizeOf(self)];
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
            writeSecure(settings.dataOutputStream, encodedBytes);
        });
        mapEncoder.put(Boolean[].class, (Boolean[] self, EncoderSettings settings) -> {
            boolean[] self2 = PrimitiveTypes.getPrimitiveArrayFrom(self);

            @SuppressWarnings("unchecked")
            BiConsumer<boolean[], EncoderSettings> biConsumer = (BiConsumer<boolean[], EncoderSettings>) mapEncoder.get(boolean[].class);
            biConsumer.accept(self2, settings);
        });

        BiConsumer<Map<?, ?>, EncoderSettings> encoderMapFunction = (Map<?, ?> self, EncoderSettings settings) -> {
            writeSecure(settings.dataOutputStream, self.size());

            for (Map.Entry<?, ?> e : self.entrySet()) {
                BinData.encoder()
                        .from(e.getKey())
                        .toStream(settings)
                        .putIntoStream();

                BinData.encoder()
                        .from(e.getValue())
                        .toStream(settings)
                        .putIntoStream();
            }
        };

        mapEncoder.put(Map.class, encoderMapFunction);
        mapEncoder.put(HashMap.class, encoderMapFunction);
        mapEncoder.put(ConcurrentHashMap.class, encoderMapFunction);

        mapEncoder.put(String.class, (String self, EncoderSettings settings) -> {
            Charset charset = (Charset) settings.get("charset");
            if (charset == null)
                charset = Charset.defaultCharset();


            writeSecure(settings.dataOutputStream, self.getBytes(charset));
            writeSecure(settings.dataOutputStream, (byte) 0);
        });

        mapEncoder.put(ArrayList.class, (ArrayList self, EncoderSettings settings) -> {
            writeSecure(settings.dataOutputStream, self.size());
            for (Object o : self) {
                BinData.encoder()
                        .from(o)
                        .toStream(settings)
                        .getBytes();
            }
        });

        mapEncoder.put(GregorianCalendar.class, (GregorianCalendar self, EncoderSettings settings) -> writeSecure(settings.dataOutputStream, self.getTimeInMillis()));
    }

    private static void writeSecure(DataOutputStream byteArrayOutputStream, byte[] bs) {
        try {
            byteArrayOutputStream.write(bs);
        } catch (IOException ignored) {
        }
    }

    private static void writeSecure(DataOutputStream byteArrayOutputStream, int i) {
        try {
            byteArrayOutputStream.writeInt(i);
        } catch (IOException ignored) {
        }
    }

    private static void writeSecure(DataOutputStream byteArrayOutputStream, long i) {
        try {
            byteArrayOutputStream.writeLong(i);
        } catch (IOException ignored) {
        }
    }

    private static void writeSecure(DataOutputStream byteArrayOutputStream, byte b) {
        try {
            byteArrayOutputStream.writeByte(b);
        } catch (IOException ignored) {
        }
    }

    private static void writeSecure(DataOutputStream byteArrayOutputStream, double d) {
        try {
            byteArrayOutputStream.writeDouble(d);
        } catch (IOException ignored) {
        }
    }

    private static void writeSecure(DataOutputStream byteArrayOutputStream, float f) {
        try {
            byteArrayOutputStream.writeFloat(f);
        } catch (IOException ignored) {
        }
    }

    private T fromObject;

    public static void register(Class<?> tClass, BiConsumer<?, EncoderSettings> consumer) {
        mapEncoder.put(tClass, consumer);
    }

    private EncoderSettings settings = new EncoderSettings();

    @SuppressWarnings({"WeakerAccess", "unchecked"})
    public static @Nullable <T> BiConsumer<T, EncoderSettings> getEncondingFunction(Class<T> tClass) {
        return (BiConsumer<T, EncoderSettings>) mapEncoder.get(tClass);
    }

    private static boolean encodeFromBinAnnotation(Object fromObject, EncoderSettings settings) {
        boolean done = false;
        Class<?> clazz = fromObject.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Bin.class)) {
                try {
                    BinData.encoder()
                            .from(field.get(fromObject))
                            .toStream(settings.dataOutputStream, settings.byteArrayOutputStream)
                            .putIntoStream();
                    done = true;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return done;
    }

    BinEncoder() {
    }

    public BinEncoder<T> toStream(@NonNull DataOutputStream dataOutputStream, @NonNull ByteArrayOutputStream byteArrayOutputStream) {
        settings.byteArrayOutputStream = Objects.requireNonNull(byteArrayOutputStream);
        settings.dataOutputStream = Objects.requireNonNull(dataOutputStream);

        return this;
    }

    public BinEncoder<T> from(@NonNull T o) {
        fromObject = Objects.requireNonNull(o);

        return this;
    }

    private BinEncoder<T> toStream(EncoderSettings settings) {
        return toStream(settings.dataOutputStream, settings.byteArrayOutputStream);
    }

    public byte[] getBytes() {
        checkState(
                settings.dataOutputStream == null
                        && settings.byteArrayOutputStream == null
                        || settings.dataOutputStream != null
                        && settings.byteArrayOutputStream != null
        );
        if (settings.byteArrayOutputStream == null) {
            settings.byteArrayOutputStream = new ByteArrayOutputStream();
            settings.dataOutputStream = new DataOutputStream(settings.byteArrayOutputStream);
        }
        putIntoStream();
        return settings.byteArrayOutputStream.toByteArray();
    }

    public void putIntoStream() {
        checkState(settings.byteArrayOutputStream != null);
        checkState(settings.dataOutputStream != null);
        @SuppressWarnings("unchecked")
        Class<T> tClass = (Class<T>) fromObject.getClass();
        BiConsumer<T, EncoderSettings> encondingFunction = getEncondingFunction(tClass);
        if (encondingFunction == null) {
            if (!encodeFromBinAnnotation(fromObject, settings))
                throw new EncoderNotFoundException(tClass);
        } else {
            encondingFunction.accept(fromObject, settings);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class EncoderSettings extends HashMap<String, Object> {
        DataOutputStream dataOutputStream;
        ByteArrayOutputStream byteArrayOutputStream;

        public EncoderSettings() {
        }

        @SuppressWarnings("SameParameterValue")
        static @Nullable Object getOrNull(@Nullable EncoderSettings settings, @NonNull String key) {
            if (settings == null)
                return null;

            return settings.get(key);
        }

        public DataOutputStream getDataOutputStream() {
            return dataOutputStream;
        }

        public ByteArrayOutputStream getByteArrayOutputStream() {
            return byteArrayOutputStream;
        }
    }

    static class EncoderNotFoundException extends RuntimeException {
        EncoderNotFoundException(Class tClass) {
            super(tClass.getName());
        }
    }

}
