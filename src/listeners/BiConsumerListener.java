package listeners;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

import datastructures.ListWrapper;

public class BiConsumerListener<T, U> extends ListWrapper<BiConsumer<T, U>> {

	public BiConsumerListener() {
		super( new CopyOnWriteArrayList<BiConsumer<T, U>>() );
	}
	
	public void call(T t, U u) {
		forEach( (BiConsumer<T, U> f) -> {
			f.accept( t, u );
		});
	}
}