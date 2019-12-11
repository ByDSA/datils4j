package es.danisales.io.text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TextFile extends LinearStringFile<String> {
	public TextFile(Path path) {
		super(path);
	}

	@Override
	protected String stringToLine(long n, String l) {
		return l;
	}

    @Override
    protected String lineToString(long i, String s) {
        return s;
    }

	@Override
	public void save() {
		Path path = toPath();
		try {
			Files.write(path, lines, encoding);
		} catch ( IOException e ) {
			callOnIOExceptionListeners(e);
		}
	}
}
