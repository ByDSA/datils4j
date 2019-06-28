package es.danisales.tasks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import es.danisales.listeners.ConsumerListener;
import es.danisales.rules.Rule;

public abstract class Action implements Runnable, Rule, Cloneable {
	protected AtomicBoolean done;
	protected AtomicBoolean running;
	protected Object _lock;
	protected List<Action> next;
	protected List<Action> previous;
	protected long checkingTime = 100;
	protected Object context;
	protected Thread thread;
	AtomicBoolean ending;
	final Mode mode;
	final ConsumerListener<Action> atEndListeners = new ConsumerListener();

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

		if (mode == Mode.CONCURRENT)
			thread = new Thread(() -> {
				doAction();
			});

		_lock = new Object();
	}

	public Action getCopy() {
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

	public final void addAtEndListener(Consumer<Action> f) {
		atEndListeners.add( f );
	}

	public final boolean isDone() {
		return done.get();
	}

	public synchronized void interrupt() {
		ending.set(true);
		Thread.currentThread().interrupt();
	}

	public boolean equals(Object o) {
		if (o.getClass().equals(this.getClass()) && o instanceof Action) {
			Action a = (Action)o;
			return a._lock == _lock;
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

	private void nextPreviousChecker() {
		if (next == null)
			next = new CopyOnWriteArrayList<>();
		if (previous == null)
			previous = new CopyOnWriteArrayList<>();
	}

	public synchronized final void addNext(Action a) {
		nextPreviousChecker();
		if (!next.contains( a ))
			next.add(a);
		a.nextPreviousChecker();
		if (!a.previous.contains( this ))
			a.addPrevious( this );
	}

	public synchronized final void addPrevious(Action a) {
		nextPreviousChecker();
		if (!previous.contains( a ))
			previous.add(a);
		a.nextPreviousChecker();
		if (!a.next.contains( this ))
			a.addNext( this );
	}

	public synchronized final void run() {
		if (isRunning())
			throw new RunningException();

		if (ending.get())
			return;

		running.set(true);
		if (thread == null)
			doAction();
		else {
			thread.setName("Thread-Action-" + this);
			thread.start();
		}
	}

	private void doAction() {
		atEndListeners.add(ac -> {
			if (ac.next != null)
				for (Action a : ac.next)
					a.run();
		});

		if (previous != null)
			for (Action a : previous) {
				try {
					a.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		while (!check()) {
			try {
				Thread.sleep( checkingTime );
			} catch ( InterruptedException e ) { }
		}
		innerRun();
		running.set(false);
		done.set( true );
		atEndListeners.call( this );
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

	public synchronized void forceCheck() {
		if (thread != null)
			thread.interrupt();
	}

	public void join() throws InterruptedException {
		if (thread != null)
			thread.join();
		else
			while (!isDone()) {
				try {
					Thread.sleep( checkingTime );
				} catch ( InterruptedException e ) { }
			}
	}

	public void joinAll() throws InterruptedException {
		join();
		if (next != null)
			for (Action a : next)
				a.joinAll();
	}
}