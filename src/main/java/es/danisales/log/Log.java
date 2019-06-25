package es.danisales.log;

import java.util.List;

import es.danisales.concurrency.Lockable;
import es.danisales.io.FileAppendable;
import es.danisales.io.FileReadable;

public interface Log<L extends Object> extends Lockable, FileAppendable<L>, FileReadable {
	default boolean save() {
		synchronized(lock()) {
			if (buffer().isEmpty())
				return false;
			else {
				append(buffer());
				buffer().clear();

				return true;
			}
		}
	}

	default void addBuffer(L l) {
		synchronized(lock()) {
			buffer().add( l );
		}
	}

	abstract List<L> buffer();
}
