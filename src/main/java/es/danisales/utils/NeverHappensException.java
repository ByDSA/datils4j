package es.danisales.utils;

public class NeverHappensException extends RuntimeException {
    private NeverHappensException(String msg) {
        super("Impossible!: " + msg);
    }

    public static void msg(String msg) throws NeverHappensException {
        throw new NeverHappensException(msg);
    }

    public static NeverHappensException make(String msg) throws NeverHappensException {
        return new NeverHappensException(msg);
    }

    public static NeverHappensException switchOf(Enum e) {
        return NeverHappensException.make("No hay más " + e.getClass().getName() + " que los del switch. Value = " + e.name());
    }
}
