package io;

import concurrency.Lockable;

public abstract class FileRWAdapter extends FileWritableAdapterAbstract implements FileReadable, FileDeletable, FileMovable, Lockable {
	final Object _lock = new Object();

	public FileRWAdapter(final String folder, final String filename, final String extension) {
		super(folder, filename, extension);
	}

	@Override
	public final Object lock() {
		return _lock;
	}

	public final boolean delete() {
		synchronized(lock()) {
			try{
				java.io.File file = new java.io.File(path().toString());

				if(file.delete()){
					return true;
				}else{
					return false;
				}

			}catch(Exception e){
				return false;

			}
		}
	}
}
