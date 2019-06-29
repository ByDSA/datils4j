package es.danisales.tasks;

import es.danisales.rules.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static es.danisales.time.Sleep.sleep;

public abstract class Action implements Runnable, Rule, Cloneable {
	// Non-duplicated
	private AtomicBoolean done;
	private AtomicBoolean running;
	protected Object _lock;
	private Thread thread;
	private ActionList next;
	private List<Action> previous;
	private ActionList atEndActions;
	private ActionList onInterruptActions;
	@SuppressWarnings("WeakerAccess") protected Object context;
	private AtomicBoolean ending;

	// Duplicated
	private long checkingTime = 100;
	private String name;
	private final Mode mode;

	public enum Mode {
		CONCURRENT, SEQUENTIAL
	}

	@SuppressWarnings("WeakerAccess")
	public Action(Mode m) {
		mode = m;
		initialize();
	}

	private void initialize() {
		ending = new AtomicBoolean(false);
		done = new AtomicBoolean(false);
		running = new AtomicBoolean(false);
		_lock = new Object();
		next = null;
		previous = new ArrayList<>();
		atEndActions = null;
		onInterruptActions = null;
		context = null;

		if (mode == Mode.CONCURRENT)
			thread = new Thread(this::doAction);
		else
			thread = null;
	}

	@SuppressWarnings("WeakerAccess")
	public Action newCopy() {
		try {
			Action a = (Action)clone();
			a.initialize();
			return a;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unused")
	public long getCheckingTime() {
		return checkingTime;
	}

	@SuppressWarnings("unused")
	public void setCheckingTime(long checkingTime) {
		this.checkingTime = checkingTime;
	}

	@SuppressWarnings("unused")
	public final void addAtEndAction(Action a) {
		if (atEndActions == null)
			atEndActions= new ActionList(Mode.SEQUENTIAL);
		atEndActions.add( a );
	}

	@SuppressWarnings("WeakerAccess")
	public final boolean isRunning() {
		return running.get();
	}

	@SuppressWarnings("WeakerAccess")
	public boolean isEnding() {
		return ending.get();
	}

	@SuppressWarnings("WeakerAccess")
	public final boolean isDone() {
		return done.get();
	}

	public synchronized void interrupt() {
		ending.set(true);
		Thread.currentThread().interrupt();
		if (onInterruptActions != null) {
			onInterruptActions.run();
			onInterruptActions.joinNext();
		}
	}

	public boolean equals(Object o) {
		if (o.getClass().equals(this.getClass()) && o instanceof Action) {
			Action a = (Action)o;
			return a == this;
		}

		return false;
	}

	public boolean isConcurrent() {
		return mode == Mode.CONCURRENT;
	}

	@SuppressWarnings("WeakerAccess")
	public boolean isSequential() {
		return !isConcurrent();
	}

	protected abstract void innerRun();

	@SuppressWarnings("WeakerAccess")
	public synchronized final void addNext(Action a) {
		if (next == null) {
			next = new ActionList(Mode.CONCURRENT);
		}
		if (!next.contains( a ))
			next.add(a);

		if (!a.previous.contains( this ))
			a.addPrevious( this );
	}

	@SuppressWarnings("WeakerAccess")
	public synchronized final void addPrevious(Action a) {
		if (!previous.contains( a ))
			previous.add(a);

		if (a.next == null) {
			a.next = new ActionList(Mode.CONCURRENT);
		}
		if (!a.next.contains( this ))
			a.addNext( this );
	}

	public synchronized final void run() {
		if (isRunning())
			throw new RunningException();

		if (ending.get())
			return;

		running.set(true);
		if (isSequential())
			doAction();
		else {
			thread.setName("Thread-Action-" + name);
			thread.start();
			assert thread.isAlive();
		}
	}

	private void doAction() {
		// Wait for previous
		for (Action a : previous) {
			try {
				a.join();
			} catch (InterruptedException ignored) {	}
		}

		// Wait for conditions
		while (!check()) {
			try {
				Thread.sleep( checkingTime );
			} catch ( InterruptedException ignored) { }
		}

		innerRun();

		running.set(false);
		done.set( true );

		// End Actions
		if (next != null) {
			next.setName("next of " + this);
			next.run();
		}
		if (atEndActions != null)
			atEndActions.run();
	}

	public synchronized boolean check() {
		return true;
	}

	@Deprecated
	public synchronized final Object getContext() {
		return context;
	}

	@Deprecated
	public synchronized void forceCheck() {
		if (thread != null)
			thread.interrupt();
	}

	public void join() throws InterruptedException {
		if (isConcurrent())
			thread.join();
		else
			while (!isDone()) {
				Thread.sleep( checkingTime );
			}
	}

	public void joinNext() {
		boolean error = false;
		do { // Ni idea de por qué a veces falla (en test 'tree'). Si se pone un sleep/println antes del join, no suele lanzar InterrruptedException (a veces aún así, especialmente si el sleep es pequeño)
			try {
				if (error)
					sleep(20);
				join();
				error = false;
			} catch (InterruptedException e) {
				error = true;
			}
		} while (error);


		if (next != null)
			next.joinNext();
	}

	@SuppressWarnings("WeakerAccess")
	public void setName(String s) {
		name = s;
	}

	@SuppressWarnings("unused")
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}