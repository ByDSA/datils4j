package es.danisales.io.binary;

import es.danisales.io.FileAutosavable;
import es.danisales.io.FileReadable;
import es.danisales.io.FileUtils;
import es.danisales.log.string.Logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A readable/writable binary file
 */
public abstract class BinFile
        extends FileAutosavable
		implements FileReadable {

	/**
	 * Instantiates a new file es.danisales.io.binary.types.
	 *
     * @param path path
	 */
	public BinFile(Path path) {
        super(path);
	}

	/* (non-Javadoc)
	 * @see es.danisales.io.FileSavable#save()
	 */
	@Override
    public void save() {
		try {
			Logging.info( "write: " + toPath().toAbsolutePath() );
            FileUtils.mkdirsParent(this);
			Files.write(toPath(), encode());
		} catch ( IOException e ) {
            callOnIOExceptionListeners(e);
		}
	}

	protected abstract byte[] encode();

	protected abstract void decode(byte[] byteArray);

	/* (non-Javadoc)
	 * @see es.danisales.io.FileReadable#load()
	 */
	@Override
    public void load() {
		byte[] buff;
		try {
			buff = Files.readAllBytes(toPath());
		} catch ( IOException e ) {
            callOnIOExceptionListeners(e);
            return;
		}
		decode(buff);
	}
}
