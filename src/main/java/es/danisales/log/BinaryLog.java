package es.danisales.log;

import es.danisales.io.FileAppendable;
import es.danisales.io.FileReadable;
import es.danisales.io.binary.BinData;
import es.danisales.io.binary.BinFile;
import es.danisales.io.binary.BinSize;
import es.danisales.log.string.Logging;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class BinaryLog<A extends Supplier<Integer>, L extends BinaryLine<A>>
		extends BinFile
		implements Log<L>, FileReadable, FileAppendable<L> {
	private CopyOnWriteArrayList<L> _buffer;
	private List<L> lines;

	public BinaryLog(Path path) {
		super(path);
	}

	@Override
	protected byte[] encode() {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		for (L line : lines) {
			byte[] encoded = BinData.encoder()
					.from(line)
					.to(dataOutputStream, byteArrayOutputStream)
					.getBytes();
			try {
				dataOutputStream.write(encoded);
			} catch (IOException ignored) {
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	@Override
	protected void decode(byte[] byteArray) {
		throw new UnsupportedOperationException();
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
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(BinSize.getBinarySizeOf(f));
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		for(L l : f)
			BinData.encoder()
					.from(l)
					.to(dataOutputStream, byteArrayOutputStream)
					.putIntoStream();
		try {
			Logging.info("appendAll list: " + this);
			Files.write(toPath(), byteArrayOutputStream.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch ( IOException e ) {
			callOnIOExceptionListeners(e);
		}
	}
}
