package es.danisales.listeners;

import es.danisales.datastructures.ListWrapper;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ConsumerListener<T> extends ListWrapper<Consumer<T>> {
	private ConsumerListener() {
		super(new CopyOnWriteArrayList<>() );
	}

	@SuppressWarnings("unchecked")
	public static <T> ConsumerListener<T> of(Consumer<T>... consumers) {
		ConsumerListener<T> consumerListener = new ConsumerListener<>();
		consumerListener.addAll(Arrays.asList(consumers));
		return consumerListener;
	}

	public void call(T t) {
		for (Consumer<T> f : this) {
			f.accept( t );
		}
	}
}