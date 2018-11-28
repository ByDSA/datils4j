package io;

import concurrency.Lockable;

public interface FileMovable extends File, Lockable {
	default boolean move(String folder, String filename, String ext) {
		synchronized(lock()) {
			java.io.File f = new java.io.File(path().toString());

			String prevFolder = folder();
			String prevFn = filename();
			String prevExt = filename();
			setFolder(folder);
			setFilename(filename);
			setExtension( ext );

			java.io.File f2 = new java.io.File(path().toString());

			if ( f.renameTo( f2 ) )
				return true;
			else {
				setFolder(prevFolder);
				setFilename(prevFn);
				setExtension( prevExt );
				return false;
			}
		}
	}

	default boolean rename(String filename, String ext) {
		return move(folder(), filename, ext);
	}

	default boolean rename(String filename) {
		return move(folder(), filename, extension());
	}
}
