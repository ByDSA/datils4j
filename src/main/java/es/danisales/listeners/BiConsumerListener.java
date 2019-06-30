package es.danisales.listeners;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

import es.danisales.datastructures.ListWrapper;

public class BiConsumerListener<T, U> extends ListWrapper<BiConsumer<T, U>> {

	public BiConsumerListener() {
		super(new CopyOnWriteArrayList<>() );
	}
	
	public void call(T t, U u) {
		for (BiConsumer<T, U> f : this) {
			f.accept( t, u );
		}
	}
}