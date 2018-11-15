package tasks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskManager extends CopyOnWriteArrayList<Task> {
	ConcurrentHashMap<Task, Integer> times;
	long accuracyMs;
	Thread thread;
	AtomicBoolean running;

	public TaskManager(long a) {
		super();
		times = new ConcurrentHashMap<>();
		running = new AtomicBoolean(false);

		accuracyMs = a;
	}

	public void run(boolean sameThread) {
		if (running.getAndSet( true ))
			return;
		
		if (sameThread)
			loop();
		else {

			thread = new Thread() {
				public void run() {
					loop();
				}
			};

			thread.start();
		}
	}

	private void loop() {
		while(size() > 0) {
			checkAndDo();
			time.Sleep.sleep( accuracyMs, false );
		}
	}

	public void checkAndDo() {
		assert parallelStream() != null;
		parallelStream().forEach((task) -> {
			assert task != null;
			if (task.check() && !task.isApplying()) {
				assert times != null;
				Integer n = times.getOrDefault( task, 0 );
				if ( !task.apply(n) )
					remove( task );
				times.put( task, n+1 );
			}
		});
	}

	public void end() {
		parallelStream().forEach((task) -> {
			task.interrupt();
		});
		clear();
	}
}
