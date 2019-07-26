package es.danisales.rules;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class ListOfRules implements Rule, List<Rule> {
    private final List<Rule> rulesListAdaptor = new ArrayList<>();
    private final boolean defaultEmptyValue;

    private ListOfRules(boolean d) {
        defaultEmptyValue = d;
    }

    public static ListOfRules of(boolean defaultEmptyValue, Rule... r) {
        ListOfRules l = new ListOfRules(defaultEmptyValue);
        if (r != null)
            l.addAll(Arrays.asList(r));

        return l;
    }

    @Override
    public boolean check() {
        synchronized (rulesListAdaptor) {
            if (rulesListAdaptor.isEmpty())
                return defaultEmptyValue;

            for (Rule r : rulesListAdaptor)
                if (!r.check())
                    return false;
        }
        return true;
    }

    @Override
    public int size() {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.isEmpty();
        }
    }

    @Override
    public boolean contains(Object o) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.contains(o);
        }
    }

    @Override
    public @NonNull Iterator<Rule> iterator() {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.iterator();
        }
    }

    @Override
    public @NonNull Object[] toArray() {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.toArray();
        }
    }

    @Override
    public boolean add(Rule o) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.add(o);
        }
    }

    @Override
    public boolean remove(Object o) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.remove(o);
        }
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Rule> c) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.addAll(c);
        }
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends Rule> c) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.addAll(c);
        }
    }

    @Override
    public void clear() {
        synchronized (rulesListAdaptor) {
            rulesListAdaptor.clear();
        }
    }

    @Override
    public Rule get(int index) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.get(index);
        }
    }

    @Override
    public Rule set(int index, Rule element) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.set(index, element);
        }
    }

    @Override
    public void add(int index, Rule element) {
        synchronized (rulesListAdaptor) {
            rulesListAdaptor.add(index, element);
        }
    }

    @Override
    public Rule remove(int index) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.remove(index);
        }
    }

    @Override
    public int indexOf(Object o) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.lastIndexOf(o);
        }
    }

    @Override
    public @NonNull ListIterator<Rule> listIterator() {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.listIterator();
        }
    }

    @Override
    public @NonNull ListIterator<Rule> listIterator(int index) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.listIterator(index);
        }
    }

    @Override
    public @NonNull List<Rule> subList(int fromIndex, int toIndex) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.subList(fromIndex, toIndex);
        }
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.retainAll(c);
        }
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.removeAll(c);
        }
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.containsAll(c);
        }
    }

    @Override
    public @NonNull <T> T[] toArray(@NonNull T[] a) {
        synchronized (rulesListAdaptor) {
            return rulesListAdaptor.toArray(a);
        }
    }
}
