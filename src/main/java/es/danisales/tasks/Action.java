package es.danisales.tasks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import es.danisales.listeners.ConsumerListener;
import es.danisales.rules.Rule;

public abstract class Action implements Runnable, Rule {
	protected final AtomicBoolean done = new AtomicBoolean(false);;
	protected final Object _lock = new Object();
	protected List<Action> next;
	protected List<Action> previous;	
	protected long checkingTime = defaultCheckingTime;
	protected Object context;
	protected final Action This = this;
	protected final Thread thread = new Thread() {
		@Override
		public void run() {
			atEndListeners.add(new Consumer<Action>() {
				@Override
				public void accept(Action ac) {
					if (ac.next != null)
						for (Action a : ac.next)
							a.run();
				}
			});
			
			if (previous != null)
				for (Action a : previous)
					try {
						a.thread.join();
					} catch ( InterruptedException e1 ) {
						e1.printStackTrace();
					}
			if (!check()) {
				do {
					try {
						Thread.sleep( checkingTime );
					} catch ( InterruptedException e ) { }
				} while(!check());
			}
			innerRun();
			done.set( true );
			atEndListeners.call( This );
		}
	};

	final ConsumerListener<Action> atEndListeners = new ConsumerListener();

	public static long defaultCheckingTime = 100;

	public final boolean isApplying() {
		return thread.isAlive();
	}
	
	public final void addAtEndListener(Consumer<Action> f) {
		atEndListeners.add( f );
	}

	public final boolean isDone() {
		return done.get();
	}

	public synchronized void interrupt() {
		Thread.currentThread().interrupt();
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
		if (thread.isAlive() || done.get())
			return;

		thread.start();
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
		thread.join();
	}

	public void joinAll() throws InterruptedException {
		join();
		if (next != null)
			for (Action a : next)
				a.joinAll();
	}
}