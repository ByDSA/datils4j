package es.danisales.functions;

public abstract class ThrowingConsumer<T, E extends Exception> {
	abstract void accept(T t) throws E;

	public void acceptThrows(T param) {
		try {
			accept(param);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
