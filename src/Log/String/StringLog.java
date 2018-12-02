package Log.String;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import Log.Log;
import io.Text.TextFile;

public abstract class StringLog<L extends CharSequence> extends TextFile<L> implements Log<L> {
	CopyOnWriteArrayList<L> _buffer;
	
	public StringLog(String path) {
		super( path );

		_buffer = new CopyOnWriteArrayList<>();
	}
	
	public List<L> buffer() {
		return _buffer;
	}
}