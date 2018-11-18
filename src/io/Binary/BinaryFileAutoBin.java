package io.Binary;

import binary.auto.AutoBin;

/**
 * A readable/writeable binary File auto binary
 */
public class BinaryFileAutoBin extends BinaryFile implements AutoBin {	
	/**
	 * Instantiates a new file binary.
	 *
	 * @param folder the folder
	 * @param fn the filename
	 * @param ext the extension
	 */
	public BinaryFileAutoBin(String folder, String fn, String ext) {
		super( folder, fn, ext );
	}
}
