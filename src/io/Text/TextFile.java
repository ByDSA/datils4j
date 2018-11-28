package io.Text;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import io.FileAppendable;
import io.FileRWAdapter;
import io.FileWritable;

public abstract class TextFile<L extends CharSequence> extends FileRWAdapter implements FileWritable, FileAppendable<L> {
	protected Charset encoding = StandardCharsets.UTF_8;
	protected String lineSeparator = "\r\n";
	protected List<L> lines;

	public TextFile(String folder, String filename, String extension) {
		super( folder, filename, extension );
		lines = new ArrayList();
	}

	@Override
	public boolean append(List<L> f) {
		Path path = path();
		try {
			StringBuilder sb = new StringBuilder();
			for (L l : f) {
				sb.append( l );
				lines.add( l );
				sb.append( lineSeparator );
			}

			Files.write(
				path, 
				sb.toString().getBytes(), 
				StandardOpenOption.APPEND);
			return true;
		} catch ( IOException e ) {
			return false;
		}
	}

	@Override
	public boolean save() {
		Path path = path();
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
			return true;
		} catch ( IOException e ) {
			return false;
		}
	}

	protected boolean readLarge(Function<String, Boolean> f) {
		Path path = path();
		try (Scanner scanner =  new Scanner(path, encoding.name())){
			while (scanner.hasNextLine()){
				if (! f.apply( scanner.nextLine() ) )
					return true;
			}
			return true;
		} catch(IOException e) {
			return false;
		}
	}

	@Override
	public boolean load() {
		return readLarge((line) -> {
			lines.add( stringToLine(line) );

			return true;
		});
	}

	abstract protected L stringToLine(String l);

	@Override
	public boolean append(L f) {
		
		Path path = path();
		
		try {
			lines.add( f );

			byte[] bytes = (f.toString() + lineSeparator).getBytes();
			if (exists())
				Files.write(
					path, 
					bytes, 
					StandardOpenOption.APPEND);
			else
				Files.write(
					path, 
					bytes, 
					StandardOpenOption.WRITE, StandardOpenOption.CREATE);

			return true;
		} catch ( IOException e ) {
			return false;
		}
	}

}
