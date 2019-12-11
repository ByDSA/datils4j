package es.danisales.log.string;

import es.danisales.io.text.LinearStringFile;
import es.danisales.log.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unused")
public abstract class TextLog<L extends CharSequence>
		extends LinearStringFile<L>
		implements Log<L> {
	private CopyOnWriteArrayList<L> _buffer;

	public TextLog(Path path) {
		super(path);

		_buffer = new CopyOnWriteArrayList<>();
	}
	
	public List<L> buffer() {
		return _buffer;
	}

	@Override
	public void save() {
		synchronized (this) {
			if (!buffer().isEmpty()) {
				try {
					appendAll(buffer());
					buffer().clear();
				} catch (IOException e) {
					callOnIOExceptionListeners(e);
				}
			}
		}
	}
}