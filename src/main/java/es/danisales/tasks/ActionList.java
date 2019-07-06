package es.danisales.tasks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class ActionList extends Action implements List<Action> {
	private final List<Action> list = new ArrayList<>();
	private final ConcurrentHashMap<Action, Integer> times;
	private final List<Consumer<Action>> beforeEachList = new ArrayList<>();
    private final List<Consumer<Action>> onRemoveListeners = new ArrayList<>();
    private final List<Consumer<Action>> onAddListeners = new ArrayList<>();

	public ActionList(Mode m) {
		super(m);
		times = new ConcurrentHashMap<>();
	}

	@Override
	protected void innerRun() {
		secureForEach((Action action) -> {
			synchronized (beforeEachList) {
				for (Consumer<Action> c : beforeEachList)
					c.accept(action);
			}
			checkAndDoCommon(action);
		});

		joinChild();
	}

	@SuppressWarnings("unused")
	public void addBeforeEach(Consumer<Action> r) {
		synchronized (beforeEachList) {
			beforeEachList.add(r);
		}
	}

    @SuppressWarnings("unused")
    public void addOnRemoveListener(Consumer<Action> r) {
        synchronized (onRemoveListeners) {
            onRemoveListeners.add(r);
        }
    }

    @SuppressWarnings("unused")
    public void addOnAddListener(Consumer<Action> r) {
        synchronized (onAddListeners) {
            onAddListeners.add(r);
        }
    }

	@SuppressWarnings("WeakerAccess")
	public void secureForEach(Consumer<? super Action> f) {
		List<Action> calledActions;
		calledActions = new ArrayList<>();

		Action action;
		synchronized(this) {
			if (size() > 0)
				action = get(0);
			else
				return;
		}
		while (action != null) {
			if (!calledActions.contains(action)) {
				f.accept(action);
				calledActions.add(action);
			}

			synchronized(this) {
				int index = indexOf(action) + 1;
				if (index >= size()) {
					if (!calledActions.containsAll(this) && size() > 0)
						action = get(0); // Restart
					else
						action = null;
				} else
					action = get(index);
			}
		}
	}


	@SuppressWarnings("WeakerAccess")
	public void joinChild() {
		secureForEach((Action a) -> {
			try {
				a.join();
			} catch (InterruptedException ignored) {
			}
		});
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

		secureForEach(Action::joinNext);

		return this;
	}

	private void checkAndDoCommon(Action action) {
		assert action != null;
		if (isEnding())
			return;

		action.context = this;
		boolean condition = action.check() && !action.isRunning();
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
	public synchronized int size() {
		return list.size();
	}

	@Override
	public synchronized boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public synchronized boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public synchronized Iterator<Action> iterator() {
		return list.iterator();
	}

	@Override
	public synchronized void forEach(Consumer<? super Action> action) {
		list.forEach(action);
	}

	@Override
	public synchronized Object[] toArray() {
		return list.toArray();
	}

	@SuppressWarnings("SuspiciousToArrayCall")
	@Override
	public synchronized <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public synchronized boolean add(Action action) {
		if (contains(action))
			throw new AddedException(action);

        synchronized (onAddListeners) {
            for (Consumer<Action> c : onAddListeners)
                c.accept(action);
        }

		return list.add(action);
	}

	@Override
	public synchronized boolean remove(Object o) {
        if (!(o instanceof Action))
            return false;

        synchronized (onRemoveListeners) {
            for (Consumer<Action> c : onRemoveListeners)
                c.accept((Action) o);
        }

		return list.remove(o);
	}

	@Override
	public synchronized boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public synchronized boolean addAll(Collection<? extends Action> c) {
		return list.addAll(c);
	}

	@Override
	public synchronized boolean addAll(int index, Collection<? extends Action> c) {
		return list.addAll(c);
	}

	@Override
	public synchronized boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public synchronized boolean removeIf(Predicate<? super Action> filter) {
		return list.removeIf(filter);
	}

	@Override
	public synchronized boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public synchronized void replaceAll(UnaryOperator<Action> operator) {
		list.replaceAll(operator);
	}

	@Override
	public synchronized void sort(Comparator<? super Action> c) {
		list.sort(c);
	}

	@Override
	public synchronized void clear() {
		list.clear();
	}

	@Override
	public synchronized Action get(int index) {
		return list.get(index);
	}

	@Override
	public synchronized Action set(int index, Action element) {
		return list.set(index, element);
	}

	@Override
	public synchronized void add(int index, Action element) {
		list.add(index, element);
	}

	@Override
	public synchronized Action remove(int index) {
		return list.remove(index);
	}

	@Override
	public synchronized int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public synchronized int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public synchronized ListIterator<Action> listIterator() {
		return list.listIterator();
	}

	@Override
	public synchronized ListIterator<Action> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public synchronized List<Action> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public synchronized Spliterator<Action> spliterator() {
		return list.spliterator();
	}

	@Override
	public synchronized Stream<Action> stream() {
		return list.stream();
	}

	@Override
	public synchronized Stream<Action> parallelStream() {
		return list.parallelStream();
	}

	@SuppressWarnings("WeakerAccess")
	class AddedException extends RuntimeException {
		public AddedException(Action a) {
			super("Action " + a + " already added in this list");
		}
	}
}
