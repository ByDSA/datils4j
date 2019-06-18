package es.danisales.listeners;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import es.danisales.datastructures.ListWrapper;

public class ConsumerListener<T> extends ListWrapper<Consumer<T>> {
	public ConsumerListener() {
		super( new CopyOnWriteArrayList<Consumer<T>>() );
	}
	
	public void call(T t) {
		forEach( (Consumer<T> f) -> {
			f.accept( t );
		});
	}
}