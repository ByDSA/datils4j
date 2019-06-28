package es.danisales.io;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import es.danisales.concurrency.Lockable;
import es.danisales.tasks.Action;
import es.danisales.tasks.LoopTask;
import es.danisales.tasks.ActionList;

public abstract class FileAutosavable extends File implements Lockable {
	final static ActionList threads = new ActionList(Action.Mode.CONCURRENT);
	
	protected AtomicBoolean _dirty = new AtomicBoolean(false);
	protected AtomicBoolean _autosaving = new AtomicBoolean( false );
	final Object _lock = new Object();
	
	public FileAutosavable(String pathname) {
		super( pathname );
	}

	public final String filename() {
		return FileUtils.stripExtension( this.getName() );
	}

	public final String extension() {
		return getName().substring( getName().lastIndexOf( "." ) + 1);
	}

	public final boolean isAutosaving() {
		return _autosaving.get();
	}

	public final void setAutosaving(boolean b) {
		_autosaving.set( b );
	}

	public final boolean getAndSetAutosaving(boolean b) {
		return _autosaving.getAndSet( b );
	}

	public final void autosave() {
		if (getAndSetAutosaving(true))
			return;

		threads.add( new LoopTask(Action.Mode.CONCURRENT) {
			@Override
			public boolean check() {
				return isDirty();
			}

			@Override
			public void innerRun() {
				synchronized(this._lock) {
					save();
				}
			}
		});

		threads.run();
	}

	public final Boolean isDirty() {
		return _dirty.get();
	}

	protected final void setDirty(boolean b) {
		_dirty.set( b );
	}

	@Override
	public final Object lock() {
		return _lock;
	}

	@Override
	public final boolean delete() {
		synchronized(lock()) {
			return super.delete();
		}
	}

	public abstract boolean save();
}
