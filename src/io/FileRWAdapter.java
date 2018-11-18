package io;

import concurrency.Lockable;

public abstract class FileRWAdapter extends FileSavableAdapterAbstract implements FileReadable, Lockable {
	final Object _lock = new Object();
	
	public FileRWAdapter(final String folder, final String filename, final String extension) {
		super(folder, filename, extension);
	}
	
	@Override
	public final Object lock() {
		return _lock;
	}
}
