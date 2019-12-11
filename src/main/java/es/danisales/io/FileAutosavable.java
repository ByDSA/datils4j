package es.danisales.io;

import es.danisales.listeners.ListenerListOne;
import es.danisales.tasks.Action;
import es.danisales.tasks.ActionList;
import es.danisales.tasks.LoopTask;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class FileAutosavable extends File {
	private final static ActionList threads = ActionList.of(Action.Mode.CONCURRENT);

	private AtomicBoolean _dirty = new AtomicBoolean(false);
	private AtomicBoolean _autosaving = new AtomicBoolean(false);

	private ListenerListOne<IOException> _onIOExceptionListeners = ListenerListOne.newInstanceSequential();

	public FileAutosavable(Path path) {
		super(path.toString());
	}

	public final String filename() {
		return FileUtils.Paths.stripExtensionFrom(this.getName());
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

		Action action = LoopTask.builder()
				.setMode(Action.Mode.CONCURRENT)
				.setRun((LoopTask self) -> {
					synchronized (this) {
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
	public final boolean delete() {
		synchronized (this) {
			return super.delete();
		}
	}

	public abstract void save();

	public void addOnIOException(@NonNull Consumer<IOException> c) {
		_onIOExceptionListeners.add(c);
	}

	protected void callOnIOExceptionListeners(IOException ioException) {
		_onIOExceptionListeners.call(ioException);
	}
}
