package functions;

public interface ForEachThrows<T, C extends ThrowingConsumer<T, E>, E extends Exception> {
	void forEach(C c) throws E;

	default void forEachThrows(C c) {
		try {
			forEach(c);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
