package es.danisales.io.binary;

import es.danisales.io.binary.types.auto.AutoBin;

/**
 * A readable/writeable es.danisales.io.binary.types File auto es.danisales.io.binary.types
 */
public class BinaryFileAutoBin extends BinaryFile implements AutoBin {	
	/**
	 * Instantiates a new file es.danisales.io.binary.types.
	 *
	 * @param folder the folder
	 * @param fn the filename
	 * @param ext the extension
	 */
	public BinaryFileAutoBin(String path) {
		super( path );
	}
}
