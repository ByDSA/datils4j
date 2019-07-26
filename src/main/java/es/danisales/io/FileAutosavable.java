package es.danisales.io;

import es.danisales.concurrency.Lockable;
import es.danisales.tasks.Action;
import es.danisales.tasks.ActionList;
import es.danisales.tasks.LoopTask;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class FileAutosavable extends File implements Lockable {
    final static ActionList threads = ActionList.of(Action.Mode.CONCURRENT);

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

        Action action = new LoopTask.Builder()
                .setMode(Action.Mode.CONCURRENT)
                .setRun((LoopTask self) -> {
                    synchronized (_lock) {
                        save();
                    }
                })
                .addReadyRule(this::isDirty)
                .build();

        threads.add(action);

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
