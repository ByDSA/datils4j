package io.Text;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import io.FileAppendable;
import io.FileAutosavable;
import io.FileReadable;

public abstract class TextFile<L extends Object> extends FileAutosavable implements FileAppendable<L>, FileReadable, List<L> {
	protected Charset encoding = defaultCharset;
	protected String lineSeparator = defaultLineSeparator;
	protected List<L> lines = new ArrayList();

	public static Charset defaultCharset = StandardCharsets.UTF_8;
	public static String defaultLineSeparator = "\n";

	public TextFile(String pathname) {
		super( pathname );
	}

	@Override
	public boolean append(List<L> f) {
		Path path = toPath();
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
		Path path = toPath();
		try {
			List<String> linesStr = new ArrayList();			
			for (L l : lines) {
				linesStr.add( l.toString() );
			}

			Files.write(path, linesStr, encoding);
			return true;
		} catch ( IOException e ) {
			return false;
		}
	}

	protected boolean readLargeTextFile(Function<String, Boolean> fReadLine) {
		Path path = toPath();
		try (Scanner scanner =  new Scanner(path, encoding.name())){
			while (scanner.hasNextLine()){
				if (! fReadLine.apply( scanner.nextLine() ) )
					return true;
			}
			return true;
		} catch(IOException e) {
			return false;
		}
	}

	@Override
	public boolean load() {
		AtomicLong i = new AtomicLong(0);
		return readLargeTextFile((lineStr) -> {
			try {
				L l = stringToLine(i.getAndIncrement(), lineStr);
				if (l == null)
					return false;

				lines.add( l );
				return true;
			} catch(SkipLineException e) {
				return true;
			}
		});
	}

	abstract protected L stringToLine(long i, String l) throws SkipLineException; // Load. ret null = stop read

	@Override
	public boolean append(L f) {
		return add(f);
	}

	@Override
	public boolean add(L f) {
		// Crea las carpetas si no existe
		this.getParentFile().mkdirs();

		lines.add( f );

		try {
			byte[] bytes = (f.toString() + lineSeparator).getBytes();
			if (exists())
				Files.write(
					toPath(), 
					bytes, 
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);

			return true;
		} catch ( IOException e ) {
			return false;
		}
	}

	@Override
	public void add(int index, L f) {
		// Crea las carpetas si no existe
		this.getParentFile().mkdirs();

		lines.add( index, f );

		save();
	}

	@Override
	public boolean addAll(Collection<? extends L> c) {
		boolean a = true;
		for (L l : c)
			a &= add(l);
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends L> c) {
		// Crea las carpetas si no existe
		this.getParentFile().mkdirs();

		lines.addAll( index, c );

		return save();
	}

	@Override
	public void clear() {
		lines.clear();
		save();
	}

	@Override
	public boolean contains(Object o) {
		return lines.contains( o );
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return lines.containsAll(c);
	}

	@Override
	public L get(int index) {
		return lines.get( index );
	}

	@Override
	public int indexOf(Object o) {
		return lines.indexOf( o );
	}

	@Override
	public boolean isEmpty() {
		return lines.isEmpty();
	}

	@Override
	public Iterator<L> iterator() {
		return lines.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return lines.lastIndexOf( o );
	}

	@Override
	public ListIterator<L> listIterator() {
		return lines.listIterator();
	}

	@Override
	public ListIterator<L> listIterator(int index) {
		return lines.listIterator( index );
	}

	@Override
	public boolean remove(Object o) {
		boolean ret = lines.remove( o );

		if (ret)
			save();

		return ret;
	}

	@Override
	public L remove(int index) {
		L ret = lines.remove( index );

		if (ret != null)
			save();

		return ret;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean ret = lines.removeAll( c );

		if (ret)
			save();

		return ret;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return lines.retainAll( c );
	}

	@Override
	public L set(int index, L element) {
		L ret = lines.set( index, element );
		save();
		return ret;
	}

	@Override
	public int size() {
		return lines.size();
	}

	@Override
	public List<L> subList(int fromIndex, int toIndex) {
		return lines.subList( fromIndex, toIndex );
	}

	@Override
	public Object[] toArray() {
		return lines.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return lines.toArray(a);
	}

	public static class SkipLineException extends Exception {

	}
}
