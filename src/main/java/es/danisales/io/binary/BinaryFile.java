package es.danisales.io.binary;

import es.danisales.io.FileAutosavable;
import es.danisales.io.FileReadable;
import es.danisales.io.FileUtils;
import es.danisales.io.binary.types.Binary;
import es.danisales.log.string.Logging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A readable/writable es.danisales.io.binary.types File
 */
public abstract class BinaryFile extends FileAutosavable implements Binary, FileReadable {	

	/**
	 * Instantiates a new file es.danisales.io.binary.types.
	 *
     * @param path
	 */
    public BinaryFile(Path path) {
        super(path);
	}

	/* (non-Javadoc)
	 * @see es.danisales.io.FileSavable#save()
	 */
	@Override
	public boolean save() {
		try {
			Logging.info( "write: " + toPath().toAbsolutePath() );
            FileUtils.mkdirsParent(this);
			Files.write( toPath(), getBytes());
			return true;
		} catch ( IOException e ) {
			e.printStackTrace();
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see es.danisales.io.FileReadable#load()
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
