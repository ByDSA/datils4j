package es.danisales.log;

import es.danisales.concurrency.Lockable;
import es.danisales.io.FileAppendable;
import es.danisales.io.FileReadable;

import java.util.List;

public interface Log<L> extends Lockable, FileAppendable<L>, FileReadable {
	@SuppressWarnings("unused")
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

	@Deprecated
	@SuppressWarnings("unused")
	default void addBuffer(L l) {
		synchronized(lock()) {
			buffer().add( l );
		}
	}

	List<L> buffer();
}
