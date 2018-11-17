package io;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import tasks.LoopTask;

public abstract class FileSavableAdapterAbstract extends FileAdapterAbstract implements FileSavable {
	protected AtomicBoolean _dirty, _autosaving;
	
	public FileSavableAdapterAbstract(String folder, String fn, String ext) {
		super(folder, fn, ext);
		
		_dirty = new AtomicBoolean(false);
		_autosaving = new AtomicBoolean( false );
	}
	
	@Override
	public final boolean isAutosaving() {
		return _autosaving.get();
	}
	
	@Override
	public final void setAutosaving(boolean b) {
		_autosaving.set( b );
	}
	
	@Override
	public final boolean getAndSetAutosaving(boolean b) {
		return _autosaving.getAndSet( b );
	}

	@Override
	public final void autosave() {
		if (getAndSetAutosaving(true))
			return;

		threads.add( new LoopTask() {
			@Override
			public boolean check() {
				return isDirty();
			}

			@Override
			public boolean innerApply(int n) {
				synchronized(this._lock) {
					save();
				}
				return true;
			}

		});

		threads.run();
	}

	@Override
	public final Boolean isDirty() {
		return _dirty.get();
	}

	@Override
	public final void setDirty(boolean b) {
		_dirty.set( b );
	}
}
