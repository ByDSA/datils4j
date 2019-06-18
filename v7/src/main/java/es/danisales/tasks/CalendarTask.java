package es.danisales.tasks;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import es.danisales.rules.Rule;
import es.danisales.time.Calendar;
import es.danisales.time.CalendarInterface;

public abstract class CalendarTask extends Action implements CalendarInterface {
	Calendar calendar = new Calendar();
	
	@Override
	public boolean add(Rule e) {
		return calendar.add( e );
	}

	public boolean add(Calendar c) {
		calendar.addAll( c );
		for(Rule e : c.getExceptions())
			calendar.addException( e );
		
		return true;
	}
	
	public List<Rule> getExceptions() {
		return calendar.getExceptions();
	}

	@Override
	public void add(int index, Rule element) {
		calendar.add( index, element );
	}

	@Override
	public boolean addAll(Collection<? extends Rule> c) {
		return calendar.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Rule> c) {
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
	public boolean containsAll(Collection<?> c) {
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
	public Iterator<Rule> iterator() {
		return calendar.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return calendar.lastIndexOf(o);
	}

	@Override
	public ListIterator<Rule> listIterator() {
		return calendar.listIterator();
	}

	@Override
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
	public boolean removeAll(Collection<?> c) {
		return calendar.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return calendar.retainAll(c);
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
	public List<Rule> subList(int fromIndex, int toIndex) {
		return calendar.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return calendar.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return calendar.toArray( a );
	}

	@Override
	public void addException(Rule r) {
		calendar.addException( r );
	}

	@Override
	public boolean check() {
		return calendar.check();
	}

	public abstract boolean innerApply(int n);

}
