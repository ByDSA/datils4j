package functions;

import java.util.function.Consumer;

import Log.String.Logging;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
	void accept(T t) throws E;

	default void acceptThrows(T param) {
		try {
			accept(param);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
