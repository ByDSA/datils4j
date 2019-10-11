package es.danisales.log.string;

import es.danisales.io.text.TextFile;
import es.danisales.log.Log;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unused")
public abstract class TextLog<L extends CharSequence> extends TextFile<L> implements Log<L> {
	private CopyOnWriteArrayList<L> _buffer;

	public TextLog(File file) {
		super(file);

		_buffer = new CopyOnWriteArrayList<>();
	}
	
	public List<L> buffer() {
		return _buffer;
	}
}