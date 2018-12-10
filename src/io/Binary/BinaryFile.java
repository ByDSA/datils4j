package io.Binary;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import Log.String.Logging;
import binary.Binary;
import io.FileAutosavable;
import io.FileReadable;

/**
 * A readable/writable binary File
 */
public abstract class BinaryFile extends FileAutosavable implements Binary, FileReadable {	

	/**
	 * Instantiates a new file binary.
	 *
	 * @param folder
	 * @param filename
	 * @param extension
	 */
	public BinaryFile(String str) {
		super( str );
	}

	/* (non-Javadoc)
	 * @see io.FileSavable#save()
	 */
	@Override
	public boolean save() {
		try {
			Logging.info( "write: " + toPath().toAbsolutePath() );
			if (getParentFile() != null)
				getParentFile().mkdirs();
			Files.write( toPath(), getBytes());
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
			buff = ByteBuffer.wrap( Files.readAllBytes(toPath()) );
		} catch ( IOException e ) {
			return false;
		}
		read(buff);
		return true;
	}
}
