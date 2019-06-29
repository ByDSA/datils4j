package es.danisales.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import es.danisales.rules.Rule;

import static es.danisales.time.Sleep.sleep;

public abstract class Action implements Runnable, Rule, Cloneable {
	protected AtomicBoolean done;
	protected AtomicBoolean running;
	protected Object _lock;
	protected Thread thread;
	ActionList next;
	List<Action> previous;
	ActionList atEndActions;
	ActionList onInterruptActions;

	protected long checkingTime = 100;
	protected Object context;
	AtomicBoolean ending;
	final Mode mode;

	public enum Mode {
		CONCURRENT, SEQUENTIAL
	}

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

		if (mode == Mode.CONCURRENT)
			thread = new Thread(() -> {
				doAction();
			});
		else
			thread = null;

	}

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

	public long getCheckingTime() {
		return checkingTime;
	}

	public final boolean isRunning() {
		return running.get();
	}

	public final void addAtEndAction(Action a) {
		if (atEndActions == null)
			atEndActions= new ActionList(Mode.SEQUENTIAL);
		atEndActions.add( a );
	}

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

	public boolean isSequential() {
		return !isConcurrent();
	}

	protected abstract void innerRun();

	public synchronized final void addNext(Action a) {
		if (next == null) {
			next = new ActionList(Mode.CONCURRENT);
		}
		if (!next.contains( a ))
			next.add(a);

		if (!a.previous.contains( this ))
			a.addPrevious( this );
	}

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
			synchronized (thread) {
				thread.setName("Thread-Action-" + name);
				thread.start();
				assert thread.isAlive();
			}
		}
	}

	private void doAction() {
		// Wait for previous
		for (Action a : previous) {
			try {
				a.join();
			} catch (InterruptedException e) {	}
		}

		// Wait for conditions
		while (!check()) {
			try {
				Thread.sleep( checkingTime );
			} catch ( InterruptedException e ) { }
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

	public synchronized final void setContext(Object taskManager) {
		context = taskManager;
	}

	public synchronized final Object getContext() {
		return context;
	}
	/*
        public synchronized void forceCheck() {
            if (thread != null)
                thread.interrupt();
        }
    */
	public void join() throws InterruptedException {
		if (isConcurrent())
			synchronized (thread) {
				thread.join();
			}
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

	String name;
	public void setName(String s) {
		name = s;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}