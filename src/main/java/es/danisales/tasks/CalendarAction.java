package es.danisales.tasks;

import es.danisales.rules.Rule;
import es.danisales.time.CalendarImp;
import es.danisales.time.CalendarInterface;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class CalendarAction extends ActionAdapter implements CalendarInterface {
	private final CalendarImp calendar = new CalendarImp();

	private CalendarAction(Mode m, Consumer<CalendarAction> f) {
		super(new CalendarTaskBuilder().setMode(m).setRun(f));
	}

	public static CalendarAction of(Mode m, Consumer<CalendarAction> f) {
		return new CalendarAction(m, f);
	}

	// todo
	@Override
	public boolean check() {
		return false;
	}

	public void add(@NonNull CalendarImp c) {
		calendar.addAll(c);
		calendar.addAllException(c.getExceptions());
	}

	@Override
	public boolean add(@NonNull Rule e) {
		return calendar.add( e );
	}

	@Override
	public void forEach(Consumer<? super Rule> action) {
		calendar.forEach(action);
	}

	@NonNull
	public List<Rule> getExceptions() {
		return calendar.getExceptions();
	}

	@Override
	public void add(int index, Rule element) {
		calendar.add( index, element );
	}

	@Override
	public boolean addAll(@NonNull Collection<? extends Rule> c) {
		return calendar.addAll(c);
	}

	@Override
	public boolean addAll(int index, @NonNull Collection<? extends Rule> c) {
		return calendar.addAll(index, c);
	}

	@Override
	public void clear() {
		calendar.clear();
	}

	@Override
	public boolean contains(Object o) {
		return calendar.contains(o);
	}

	@Override
	public boolean containsAll(@NonNull Collection<?> c) {
		return calendar.containsAll(c);
	}

	@Override
	public Rule get(int index) {
		return calendar.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return calendar.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return calendar.isEmpty();
	}

	@Override
	@NonNull
	public Iterator<Rule> iterator() {
		return calendar.iterator();
	}

	@Override
	public boolean removeIf(Predicate<? super Rule> filter) {
		return calendar.removeIf(filter);
	}

	@Override
	public int lastIndexOf(Object o) {
		return calendar.lastIndexOf(o);
	}

	@Override
	@NonNull
	public ListIterator<Rule> listIterator() {
		return calendar.listIterator();
	}

	@Override
	@NonNull
	public ListIterator<Rule> listIterator(int index) {
		return calendar.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return calendar.remove(o);
	}

	@Override
	public Rule remove(int index) {
		return calendar.remove(index);
	}

	@Override
	public boolean removeAll(@NonNull Collection<?> c) {
		return calendar.removeAll(c);
	}

	@Override
	public void replaceAll(UnaryOperator<Rule> operator) {
		calendar.replaceAll(operator);
	}

	@Override
	public boolean retainAll(@NonNull Collection<?> c) {
		return calendar.retainAll(c);
	}

	@Override
	public void sort(Comparator<? super Rule> c) {
		calendar.sort(c);
	}

	@Override
	public Spliterator<Rule> spliterator() {
		return calendar.spliterator();
	}

	@Override
	public Rule set(int index, Rule element) {
		return calendar.set(index, element);
	}

	@Override
	public int size() {
		return calendar.size();
	}

	@Override
	@NonNull
	public List<Rule> subList(int fromIndex, int toIndex) {
		return calendar.subList(fromIndex, toIndex);
	}

	@Override
	public Stream<Rule> stream() {
		return calendar.stream();
	}

	@Override
	public Stream<Rule> parallelStream() {
		return calendar.parallelStream();
	}

	@SuppressWarnings("SuspiciousToArrayCall")
	@Override
	@NonNull
	public <T> T[] toArray(@NonNull T[] a) {
		return calendar.toArray( a );
	}

	@Override
	@NonNull
	public Object[] toArray() {
		return calendar.toArray();
	}

	@Override
	public void addAllException(@NonNull List<? extends Rule> r) {
		calendar.addAllException(r);
	}

	@Override
	public void addException(@NonNull Rule r) {
		calendar.addException(r);
	}

	public static CalendarTaskBuilder builder() {
		return new CalendarTaskBuilder();
	}

	public static class CalendarTaskBuilder extends ActionBuilder<CalendarTaskBuilder, CalendarAction, CalendarAction> {
		private CalendarTaskBuilder() {
		}

		@Override
        protected @NonNull CalendarAction buildOnce() {
			return new CalendarAction(mode, function);
		}

		@Override
        protected @NonNull CalendarTaskBuilder self() {
			return this;
		}
	}
}
