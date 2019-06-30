package es.danisales.listeners;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import es.danisales.datastructures.ListWrapper;

public class ConsumerListener<T> extends ListWrapper<Consumer<T>> {
	public ConsumerListener() {
		super(new CopyOnWriteArrayList<>() );
	}
	
	public void call(T t) {
		for (Consumer<T> f : this) {
			f.accept( t );
		}
	}
}