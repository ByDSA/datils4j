package es.danisales.io.text;

import es.danisales.io.FileAppendable;
import es.danisales.io.FileAutosavable;
import es.danisales.io.FileReadable;
import es.danisales.io.FileUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public abstract class TextFile<L> extends FileAutosavable implements FileAppendable<L>, FileReadable, List<L> {
	Charset encoding = StandardCharsets.UTF_8;
	List<L> lines = new ArrayList<>();
	private String lineSeparator = "\n";

	public TextFile(File file) {
		super(file);
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
			List<String> linesStr = new ArrayList<>();
			for (L l : lines) {
				linesStr.add( l.toString() );
			}

			Files.write(path, linesStr, encoding);
			return true;
		} catch ( IOException e ) {
			return false;
		}
	}

	private boolean readLargeTextFile(Function<String, Boolean> fReadLine) {
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
		final AtomicLong i = new AtomicLong(0);
		return readLargeTextFile(
				lineStr -> {
						L l = stringToLine(i.getAndIncrement(), lineStr);
						if (l != null)
							lines.add(l);
						return true;
				}

		);
	}

	abstract protected L stringToLine(long i, String l);

	@Override
	public boolean append(L f) {
		return add(f);
	}

	@Override
	public boolean add(L f) {
		createParents();

		lines.add( f );

		try {
			byte[] bytes = (f.toString() + lineSeparator).getBytes();
			Files.write(
					toPath(),
					bytes,
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);

			return true;
		} catch ( IOException e ) {
			return false;
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void saveIfAutosave() {
		save();
	}

	private void createParents() {
		FileUtils.mkdirsParent(this);
	}

	@Override
	public void add(int index, L f) {
		createParents();

		lines.add( index, f );

		saveIfAutosave();
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean addAll(@NonNull Collection<? extends L> c) {
		boolean a = true;
		for (L l : c)
			a &= add(l);
		return a;
	}

	@Override
	public boolean addAll(int index, @NonNull Collection<? extends L> c) {
		createParents();

		boolean ret = lines.addAll(index, c);

		saveIfAutosave();

		return ret;
	}

	@Override
	public void clear() {
		lines.clear();
		saveIfAutosave();
	}

	@Override
	public boolean contains(Object o) {
		return lines.contains( o );
	}

	@Override
	public boolean containsAll(@NonNull Collection<?> c) {
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
	@NonNull
	public Iterator<L> iterator() {
		return lines.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return lines.lastIndexOf( o );
	}

	@Override
	@NonNull
	public ListIterator<L> listIterator() {
		return lines.listIterator();
	}

	@Override
	@NonNull
	public ListIterator<L> listIterator(int index) {
		return lines.listIterator( index );
	}

	@Override
	public boolean remove(Object o) {
		boolean ret = lines.remove( o );

		if (ret)
			saveIfAutosave();

		return ret;
	}

	@Override
	public L remove(int index) {
		L ret = lines.remove( index );

		if (ret != null)
			saveIfAutosave();

		return ret;
	}

	@Override
	public boolean removeAll(@NonNull Collection<?> c) {
		boolean ret = lines.removeAll( c );

		if (ret)
			saveIfAutosave();

		return ret;
	}

	@Override
	public boolean retainAll(@NonNull Collection<?> c) {
		return lines.retainAll( c );
	}

	@Override
	public L set(int index, L element) {
		L ret = lines.set( index, element );
		saveIfAutosave();
		return ret;
	}

	@Override
	public int size() {
		return lines.size();
	}

	@Override
	@NonNull
	public List<L> subList(int fromIndex, int toIndex) {
		return lines.subList( fromIndex, toIndex );
	}

	@Override
	@NonNull
	public Object[] toArray() {
		return lines.toArray();
	}

	@SuppressWarnings("SuspiciousToArrayCall")
	@Override
	@NonNull
	public <T> T[] toArray(@NonNull T[] a) {
		return lines.toArray(a);
	}
}
