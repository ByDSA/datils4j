package es.danisales.log;

import es.danisales.io.FileAppendable;
import es.danisales.io.FileReadable;
import es.danisales.io.binary.BinaryFile;
import es.danisales.io.binary.types.Binary;
import es.danisales.log.string.Logging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class BinaryLog<A extends Supplier<Integer>, L extends BinaryLine<A>>
		extends BinaryFile
		implements Log<L>, FileReadable, FileAppendable<L> {
	private CopyOnWriteArrayList<L> _buffer;
	private List<L> lines;

	public BinaryLog(Path path) {
		super(path);
	}

	@Override
	public List<L> buffer() {
		return _buffer;
	}

	@Override
	public void append(L f) {
		try {
			Logging.info("appendAll: " + this);
			Files.write( toPath(),  f.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND );
		} catch ( IOException e ) {
			callOnIOExceptionListeners(e);
		}
	}

	@Override
	public void appendAll(List<L> f) {
		ByteBuffer buff = ByteBuffer.allocate(Binary.sizeBytes( f ));
		for(L l : f)
			buff.put( l.getBytes() );
		try {
			Logging.info("appendAll list: " + this);
			Files.write( toPath(), buff.array(), StandardOpenOption.CREATE, StandardOpenOption.APPEND );
		} catch ( IOException e ) {
			callOnIOExceptionListeners(e);
		}
	}

	@Override
	public void load() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int sizeBytes() {
		return Binary.sizeBytes( lines );
	}

	@Override
	public void write(ByteBuffer buff) {
		for(L l : lines)
			l.write( buff );
	}

	@Override
	public void read(ByteBuffer buff) {
		Logging.fatalError();
	}
}
