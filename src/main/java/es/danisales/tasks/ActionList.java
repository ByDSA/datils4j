package es.danisales.tasks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

public class ActionList implements Action, List<Action> {
	final ActionAdapter<ActionList> actionAdapter;
	private final List<Action> listAdapter = new ArrayList<>();

	private final ConcurrentHashMap<Action, Integer> times;
	private final List<Consumer<Action>> beforeEachList = new ArrayList<>();
	private final List<Consumer<Action>> onRemoveListeners = new ArrayList<>();
	private final List<Consumer<Action>> onAddListeners = new ArrayList<>();

	protected ActionList(Mode m) {
		times = new ConcurrentHashMap<>();
		actionAdapter = new ActionAdapter.Builder<ActionList>()
				.setMode(m)
				.setRun(this::innerRun)
				.setCaller(this)
				.build();
	}

	public static ActionList of(Mode mode, Action... actions) {
		return of(mode, Arrays.asList(actions));
	}

	public static ActionList of(Mode mode, List<Action> actions) {
		ActionList ret = new ActionList(mode);
		if (actions != null)
			ret.addAll(actions);

		return ret;
	}

	private void innerRun(ActionList self) {
		self.secureForEach((Action action) -> {
			synchronized (self.beforeEachList) {
				for (Consumer<Action> c : self.beforeEachList)
					c.accept(action);
			}
			self.checkAndDoCommon(action);
		});

		self.joinChild();
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
	public void join() {
		try {
			actionAdapter.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void joinNext() {
		actionAdapter.joinNext();

		secureForEach(Action::joinNext);
	}

	@Override
	public String getName() {
		return actionAdapter.getName();
	}

	@Override
	public void setName(String s) {
		actionAdapter.setName(s);
	}

	@Override
	public boolean hasPrevious(Action a) {
		return actionAdapter.hasPrevious(a);
	}

	@Override
	public boolean hasNext(Action a) {
		return actionAdapter.hasNext(a);
	}

	@Override
	public Object getContext() {
		return actionAdapter.getContext();
	}

	@Override
	public void run(Object context) {
		actionAdapter.run(context);
	}

	@Override
	public Consumer<ActionList> getFunc() {
		return actionAdapter.getFunc();
	}

	@Override
	public void setCheckFunction(Supplier<Boolean> f) {
		actionAdapter.setCheckFunction(f);
	}

	@Override
	public boolean check() {
		return actionAdapter.check();
	}

	private void checkAndDoCommon(Action action) {
		assert action != null;
		if (isEnding())
			return;

		boolean condition = action.check() && !action.isRunning();
		if ( condition ) {
			checkNotNull(times);
			Integer n = times.get( action );
			if (n == null)
				n = 0;
			action.run(this);
			times.put( action, n+1 );
		}
	}

	@Override
	public long getCheckingTime() {
		return actionAdapter.getCheckingTime();
	}

	@Override
	public void setCheckingTime(long checkingTime) {
		actionAdapter.setCheckingTime(checkingTime);
	}

	@Override
	public void addAfter(Runnable r) {
		actionAdapter.addAfter(r);
	}

	@Override
	public void addInterruptedListener(Runnable a) {
		actionAdapter.addInterruptedListener(a);
	}

	@Override
	public boolean isRunning() {
		return actionAdapter.isRunning();
	}

	@Override
	public boolean isWaitingCheck() {
		return actionAdapter.isWaitingCheck();
	}

	@Override
	public boolean isEnding() {
		return actionAdapter.isEnding();
	}

	@Override
	public boolean isDone() {
		return actionAdapter.isDone();
	}

	@Override
	public void interrupt() {
		actionAdapter.interrupt();
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
	public boolean isConcurrent() {
		return actionAdapter.isConcurrent();
	}

	@Override
	public Mode getMode() {
		return actionAdapter.getMode();
	}

	@Override
	public void addNext(Action a) {
		actionAdapter.addNext(a);
	}

	@Override
	public void addPrevious(Action a) {
		actionAdapter.addPrevious(a);
	}

	@Override
	public synchronized int size() {
		return listAdapter.size();
	}

	@Override
	public synchronized boolean isEmpty() {
		return listAdapter.isEmpty();
	}

	@Override
	public synchronized boolean contains(Object o) {
		return listAdapter.contains(o);
	}

	@Override
	public synchronized Iterator<Action> iterator() {
		return listAdapter.iterator();
	}

	@Override
	public synchronized void forEach(Consumer<? super Action> action) {
		listAdapter.forEach(action);
	}

	@Override
	public synchronized Object[] toArray() {
		return listAdapter.toArray();
	}

	@SuppressWarnings("SuspiciousToArrayCall")
	@Override
	public synchronized <T> T[] toArray(T[] a) {
		return listAdapter.toArray(a);
	}

	@Override
	public synchronized boolean add(Action action) {
		int index = indexOf(action);
		if (index >= 0 && get(index) == action)
			throw new AddedException(action);

		synchronized (onAddListeners) {
			for (Consumer<Action> c : onAddListeners)
				c.accept(action);
		}

		return listAdapter.add(action);
	}

	@Override
	public synchronized boolean remove(Object o) {
		if (!(o instanceof Action))
			return false;

		synchronized (onRemoveListeners) {
			for (Consumer<Action> c : onRemoveListeners)
				c.accept((Action) o);
		}

		return listAdapter.remove(o);
	}

	@Override
	public synchronized boolean containsAll(Collection<?> c) {
		return listAdapter.containsAll(c);
	}

	@Override
	public synchronized boolean addAll(Collection<? extends Action> c) {
		return listAdapter.addAll(c);
	}

	@Override
	public synchronized boolean addAll(int index, Collection<? extends Action> c) {
		return listAdapter.addAll(c);
	}

	@Override
	public synchronized boolean removeAll(Collection<?> c) {
		return listAdapter.removeAll(c);
	}

	@Override
	public synchronized boolean removeIf(Predicate<? super Action> filter) {
		return listAdapter.removeIf(filter);
	}

	@Override
	public synchronized boolean retainAll(Collection<?> c) {
		return listAdapter.retainAll(c);
	}

	@Override
	public synchronized void replaceAll(UnaryOperator<Action> operator) {
		listAdapter.replaceAll(operator);
	}

	@Override
	public synchronized void sort(Comparator<? super Action> c) {
		listAdapter.sort(c);
	}

	@Override
	public synchronized void clear() {
		listAdapter.clear();
	}

	@Override
	public synchronized Action get(int index) {
		return listAdapter.get(index);
	}

	@Override
	public synchronized Action set(int index, Action element) {
		return listAdapter.set(index, element);
	}

	@Override
	public synchronized void add(int index, Action element) {
		listAdapter.add(index, element);
	}

	@Override
	public synchronized Action remove(int index) {
		return listAdapter.remove(index);
	}

	@Override
	public synchronized int indexOf(Object o) {
		return listAdapter.indexOf(o);
	}

	@Override
	public synchronized int lastIndexOf(Object o) {
		return listAdapter.lastIndexOf(o);
	}

	@Override
	public synchronized ListIterator<Action> listIterator() {
		return listAdapter.listIterator();
	}

	@Override
	public synchronized ListIterator<Action> listIterator(int index) {
		return listAdapter.listIterator(index);
	}

	@Override
	public synchronized List<Action> subList(int fromIndex, int toIndex) {
		return listAdapter.subList(fromIndex, toIndex);
	}

	@Override
	public synchronized Spliterator<Action> spliterator() {
		return listAdapter.spliterator();
	}

	@Override
	public synchronized Stream<Action> stream() {
		return listAdapter.stream();
	}

	@Override
	public synchronized Stream<Action> parallelStream() {
		return listAdapter.parallelStream();
	}

	@Override
	public void run() {
		actionAdapter.run();
	}

	@SuppressWarnings("WeakerAccess")
	class AddedException extends RuntimeException {
		public AddedException(Action a) {
			super("Action " + a + " already added in this listAdapter");
		}
	}
}
