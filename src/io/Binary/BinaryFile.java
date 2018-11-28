package io.Binary;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import Log.String.Logging;
import binary.Binary;
import concurrency.Lockable;
import io.FileRWAdapter;
import io.FileReadable;
import io.FileWritable;

/**
 * A readable/writable binary File
 */
public abstract class BinaryFile extends FileRWAdapter implements Binary, FileWritable, FileReadable, Lockable {	
	
	/**
	 * Instantiates a new file binary.
	 *
	 * @param folder
	 * @param filename
	 * @param extension
	 */
	public BinaryFile(String folder, String filename, String extension) {
		super( folder, filename, extension );
	}

	/* (non-Javadoc)
	 * @see io.FileSavable#save()
	 */
	@Override
	public boolean save() {
		try {
			Logging.info( "write: " + path().toAbsolutePath() );
			Files.createDirectories( path().getParent() );
			Files.write( path(), getBytes());
			return true;
		} catch ( IOException e ) {
			e.printStackTrace();
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see io.FileReadable#load()
	 */
	@Override
	public boolean load() {
		ByteBuffer buff;
		try {
			buff = ByteBuffer.wrap( Files.readAllBytes(path()) );
		} catch ( IOException e ) {
			return false;
		}
		read(buff);
		return true;
	}
}
