package es.danisales.io.binary;

import es.danisales.io.binary.types.auto.AutoBin;

/**
 * A readable/writeable es.danisales.io.binary.types File auto es.danisales.io.binary.types
 */
public class BinaryFileAutoBin extends BinaryFile implements AutoBin {	
	/**
	 * Instantiates a new file es.danisales.io.binary.types.
	 *
	 * @param path
	 */
	public BinaryFileAutoBin(String path) {
		super( path );
	}
}
