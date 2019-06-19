package es.danisales.tasks;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActionManager extends CopyOnWriteArrayList<Action> implements Runnable, List<Action> {
	public static int defaultTime = 100;

	ConcurrentHashMap<Action, Integer> times;
	long accuracyMs;
	AtomicBoolean running, parallel, sameThread, ending;

	public ActionManager(long a, boolean par) {
		super();
		times = new ConcurrentHashMap<>();
		running = new AtomicBoolean(false);
		parallel = new AtomicBoolean(par);
		sameThread = new AtomicBoolean(par);
		ending = new AtomicBoolean(false);

		accuracyMs = a;
	}

	public void run() {
		if (running.getAndSet( true ))
			return;

		if (sameThread.get())
			loop();
		else {
			Thread thread = new Thread() {
				public void run() {
					loop();
				}
			};

			thread.start();
		}
	}

	private void loop() {
		if (ending.get())
			return;
		if (isParallel())
			loopParallel();
		else
			loopSequential();
	}

	public boolean isParallel() {
		return parallel.get();
	}

	public boolean isSameThread() {
		return sameThread.get();
	}

	public ActionManager() {
		this(defaultTime, true);
	}

	protected void loopParallel() {
		while(size() > 0) {
			checkAndDoParallel();
			es.danisales.time.Sleep.sleep( accuracyMs );
		}
	}

	protected void loopSequential() {
		while(size() > 0) {
			checkAndDoSequential();
			es.danisales.time.Sleep.sleep( accuracyMs );
		}
	}

	protected void checkAndDoParallel() {
		for (final Action task : this) {
			new Thread() {
				@Override
				public void run() {
					checkAndDoCommon(task);
				}
			}.start();
		}
	}

	protected void checkAndDoSequential() {
		for (final Action task : this) {
			checkAndDoCommon(task);
		}
	}

	private void checkAndDoCommon(Action task) {
		assert task != null;
		if (ending.get())
			return;

		boolean condition = true;
		task.setContext(this);
		condition &= task.check();
		condition &= !task.isApplying();

		if ( condition ) {
			assert times != null;
			Integer n = times.get( task );
			if (n == null)
				n = 0;
			task.run();
			times.put( task, n+1 );
		}
	}

	public void end() {
		ending.set(true);
		for (final Action task : this) {
			new Thread() {
				@Override
				public void run() {
					if (task.isApplying())
						task.interrupt();
					else
						remove(task);
				}
			};
		}

		clear();
	}
}
