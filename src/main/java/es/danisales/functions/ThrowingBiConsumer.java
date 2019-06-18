package es.danisales.functions;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ThrowingBiConsumer<T, U, E extends Exception> {
	void accept(T t, U u) throws E;

	default void acceptThrows(T param1, U param2) {
		try {
			accept(param1, param2);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	static<T, U, E extends Exception> BiConsumer<T, U> consumerWrapper(ThrowingBiConsumer<T, U, E> consumer) {

		return (i, j) -> {
			try {
				consumer.accept(i, j);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		};
	}
}
