package es.danisales.tasks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class ActionList extends Action implements List<Action> {
	private CopyOnWriteArrayList<Action> list = new CopyOnWriteArrayList<>();
	private ConcurrentHashMap<Action, Integer> times;

	public ActionList(Mode m) {
		super(m);
		times = new ConcurrentHashMap<>();
	}

	@Override
	protected void innerRun() {
		if (isConcurrent())
			new Thread(() -> {
				for (final Action action : this) {
					checkAndDoCommon(action);
				}
			}).start();
		else {
			for (final Action action : this) {
				checkAndDoCommon(action);
			}

			joinChild();
		}
	}

	@SuppressWarnings("WeakerAccess")
	public void joinChild() {
		for (Action a : this) {
			try {
				a.join();
			} catch (InterruptedException ignored) {
			}
		}
	}

	@Override
	public Action join() {
		try {
			super.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return this;
	}

	@Override
	public Action joinNext() {
		super.joinNext();


		for (Action a : this)
			a.joinNext();

		return this;
	}

	private void checkAndDoCommon(Action action) {
		assert action != null;
		if (isEnding())
			return;

		action.context = this;
		boolean condition = action.check();
		condition &= !action.isRunning();

		if ( condition ) {
			assert times != null;
			Integer n = times.get( action );
			if (n == null)
				n = 0;
			action.run();
			times.put( action, n+1 );
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();
		for (final Action action : this) {
			new Thread(() -> {
				if (action.isRunning())
					action.interrupt();
				else
					remove(action);
			}).start();
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

	@SuppressWarnings("SuspiciousToArrayCall")
	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean add(Action action) {
		for (Action a : this)
			if (a == action)
				throw new AddedException(action);
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

	@SuppressWarnings("WeakerAccess")
	class AddedException extends RuntimeException {
		public AddedException(Action a) {
			super("Action " + a + " already added in this list");
		}
	}
}
