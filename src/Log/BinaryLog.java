package Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import Log.String.Logging;
import io.FileAppendable;
import io.FileWritable;
import io.Binary.Binary;
import io.Binary.FileBinary;
import others.Codeable;

public class BinaryLog<A extends Codeable, L extends BinaryLine<A>> extends FileBinary implements Log<L>, FileWritable, FileAppendable<L> {
	CopyOnWriteArrayList<L> _buffer;
	protected List<L> lines;

	public BinaryLog(String fn) {
		super( "log", fn, "binlog" );
	}

	@Override
	public List<L> buffer() {
		return _buffer;
	}

	@Override
	public boolean append(L f) {
		try {
			Logging.info( "append: " + path() );
			Files.write( path(),  f.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND );
			return true;
		} catch ( IOException e ) {
			return false;
		}
	}

	@Override
	public boolean append(List<L> f) {
		ByteBuffer buff = ByteBuffer.allocate( (int) Binary.size( f ) );
		for(L l : f)
			buff.put( l.getBytes() );
		try {
			Logging.info( "append list: " + path() );
			Files.write( path(), buff.array(), StandardOpenOption.CREATE, StandardOpenOption.APPEND );
			return true;
		} catch ( IOException e ) {
			return false;
		}
	}

	@Override
	public boolean load() {
		Logging.fatalError();
		return false;
	}

	@Override
	public int size() {
		return Binary.size( lines );
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
