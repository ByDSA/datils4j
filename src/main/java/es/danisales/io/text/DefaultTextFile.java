package es.danisales.io.text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultTextFile extends TextFile<String> {
	public DefaultTextFile(File file) {
		super(file);
	}

	@Override
	protected String stringToLine(long n, String l) {
		return l;
	}
	
	@Override
	public boolean save() {
		Path path = toPath();
		try {
			Files.write(path, lines, encoding);
			return true;
		} catch ( IOException e ) {
			return false;
		}
	}
}
