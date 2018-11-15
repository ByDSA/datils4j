package io;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import tasks.LoopTask;

public abstract class FileAdapter implements FileSavable {
	protected AtomicReference<String> _filename,  _extension, _folder;
	
	public FileAdapter(String folder, String fn, String ext) {
		_filename = new AtomicReference<>();
		_extension = new AtomicReference<String>();
		_folder = new AtomicReference<String>();
		
		setFolder(folder);
		setFilename(fn);
		setExtension(ext);
	}
	
	@Override
	public final String filename() {
		return _filename.get();
	}

	@Override
	public final String extension() {
		return _extension.get();
	}
	
	@Override
	public final String folder() {
		return _folder.get();
	}
	
	@Override
	public final void setExtension(String ext) {
		 _extension.set(ext);
	}

	@Override
	public final void setFilename(String fn) {
		_filename.set( fn );
	}
	
	@Override
	public final void setFolder(String fn) {
		_folder.set( fn );
	}
}
