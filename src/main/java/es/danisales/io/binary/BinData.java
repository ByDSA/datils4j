package es.danisales.io.binary;

public class BinData {
    private BinData() {
    }

    public static <T> BinEncoder<T> encoder() {
        return new BinEncoder<>();
    }

    @SuppressWarnings("WeakerAccess")
    public static <T> BinDecoder<T> decoder(Class<T> type) {
        return new BinDecoder<>(type);
    }
}
