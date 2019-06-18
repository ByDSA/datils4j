package es.danisales.io.Binary;

import es.danisales.binary.auto.AutoBin;

/**
 * A readable/writeable es.danisales.binary File auto es.danisales.binary
 */
public class BinaryFileAutoBin extends BinaryFile implements AutoBin {	
	/**
	 * Instantiates a new file es.danisales.binary.
	 *
	 * @param folder the folder
	 * @param fn the filename
	 * @param ext the extension
	 */
	public BinaryFileAutoBin(String path) {
		super( path );
	}
}
