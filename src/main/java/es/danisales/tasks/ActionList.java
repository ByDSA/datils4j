package es.danisales.tasks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class ActionList extends Action implements Runnable, List<Action> {
	CopyOnWriteArrayList<Action> list = new CopyOnWriteArrayList<>();

	public static int defaultCheckingTimeMs = 100;

	ConcurrentHashMap<Action, Integer> times;
	long checkingEveryMs;
	AtomicBoolean running, parallel, sameThread, ending;

	public ActionList(long checkingEveryMs, boolean doingParallel) {
		super();
		times = new ConcurrentHashMap<>();
		running = new AtomicBoolean(false);
		parallel = new AtomicBoolean(doingParallel);
		sameThread = new AtomicBoolean(doingParallel);
		ending = new AtomicBoolean(false);

		this.checkingEveryMs = checkingEveryMs;
	}

	@Override
	protected void innerRun() {
		if (running.getAndSet( true ))
			return;

		if (sameThread.get())
			loop();
		else {
			Thread thread = new Thread(() -> loop());

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

	public ActionList() {
		this(defaultCheckingTimeMs, true);
	}

	protected void loopParallel() {
		while(size() > 0) {
			checkAndDoParallel();
			es.danisales.time.Sleep.sleep(checkingEveryMs);
		}
	}

	protected void loopSequential() {
		while(size() > 0) {
			checkAndDoSequential();
			es.danisales.time.Sleep.sleep(checkingEveryMs);
		}
	}

	protected void checkAndDoParallel() {
		for (final Action task : this) {
			new Thread(() -> checkAndDoCommon(task)).start();
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
			new Thread(() -> {
				if (task.isApplying())
					task.interrupt();
				else
					remove(task);
			});
		}

		clear();
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public Iterator<Action> iterator() {
		return list.iterator();
	}

	@Override
	public void forEach(Consumer<? super Action> action) {
		list.forEach(action);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean add(Action action) {
		return list.add(action);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Action> c) {
		return list.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Action> c) {
		return list.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean removeIf(Predicate<? super Action> filter) {
		return list.removeIf(filter);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public void replaceAll(UnaryOperator<Action> operator) {
		list.replaceAll(operator);
	}

	@Override
	public void sort(Comparator<? super Action> c) {
		list.sort(c);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public Action get(int index) {
		return list.get(index);
	}

	@Override
	public Action set(int index, Action element) {
		return list.set(index, element);
	}

	@Override
	public void add(int index, Action element) {
		list.add(index, element);
	}

	@Override
	public Action remove(int index) {
		return list.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<Action> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<Action> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public List<Action> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Spliterator<Action> spliterator() {
		return list.spliterator();
	}

	@Override
	public Stream<Action> stream() {
		return list.stream();
	}

	@Override
	public Stream<Action> parallelStream() {
		return list.parallelStream();
	}
}
