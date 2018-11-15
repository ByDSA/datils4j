package functions;

import java.util.function.BiConsumer;

public interface BiForEachThrows<T, U, E extends Exception> {
	void forEach(BiConsumer<? super T, ? super U> c);

	default public void forEachThrowing(ThrowingBiConsumer<? super T, ? super U, E> b) {
		forEach( ThrowingBiConsumer.consumerWrapper(b) );
	}
}
