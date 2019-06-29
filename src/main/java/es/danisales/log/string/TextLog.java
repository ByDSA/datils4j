package es.danisales.log.string;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import es.danisales.log.Log;
import es.danisales.io.text.TextFile;

@SuppressWarnings("unused")
public abstract class TextLog<L extends CharSequence> extends TextFile<L> implements Log<L> {
	private CopyOnWriteArrayList<L> _buffer;
	
	public TextLog(String path) {
		super( path );

		_buffer = new CopyOnWriteArrayList<>();
	}
	
	public List<L> buffer() {
		return _buffer;
	}
}