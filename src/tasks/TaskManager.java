package tasks;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TaskManager extends CopyOnWriteArrayList<Task> implements Runnable, List<Task> {
	public static int defaultTime = 100;

	ConcurrentHashMap<Task, Integer> times;
	long accuracyMs;
	AtomicBoolean running, parallel, sameThread, ending;

	public TaskManager(long a, boolean par, boolean nt) {
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
		if (parallel.get())
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

	public TaskManager() {
		this(defaultTime, true, false);
	}

	protected void loopParallel() {
		while(size() > 0) {
			checkAndDoParallel();
			time.Sleep.sleep( accuracyMs, false );
		}
	}

	protected void loopSequential() {
		while(size() > 0) {
			checkAndDoSequential();
			time.Sleep.sleep( accuracyMs, false );
		}
	}

	protected void checkAndDoParallel() {
		assert parallelStream() != null;
		parallelStream().forEach((task) -> {
			checkAndDoCommon(task);
		});
	}

	protected void checkAndDoSequential() {
		assert parallelStream() != null;
		forEach((task) -> {
			checkAndDoCommon(task);
		});
	}

	private void checkAndDoCommon(Task task) {
		assert task != null;
		if (ending.get())
			return;
		if (task.check() && !task.isApplying()) {
			assert times != null;
			Integer n = times.getOrDefault( task, 0 );
			if ( !task.apply(n) )
				remove( task );
			times.put( task, n+1 );
		}
	}

	public void end() {
		ending.set(true);
		parallelStream().forEach((task) -> {
			if (task.isApplying())
				task.interrupt();
			else
				remove(task);
		});
		clear();
	}
}
