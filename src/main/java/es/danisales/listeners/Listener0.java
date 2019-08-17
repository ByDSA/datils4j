package es.danisales.listeners;

import es.danisales.datastructures.ListAdapter;
import es.danisales.tasks.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Listener0 extends ListAdapter<Runnable> implements Listener {
    private final Action.Mode modeConcurrency;

    private Listener0(List<Runnable> listAdapter, Action.Mode mode) {
        super(listAdapter);

        this.modeConcurrency = mode;
    }

    public static Listener0 newInstanceSequential() {
        return new Listener0(new ArrayList<>(), Action.Mode.SEQUENTIAL);
    }

    public static Listener0 newInstanceSequentialSafeThread() {
        return new Listener0(new CopyOnWriteArrayList<>(), Action.Mode.SEQUENTIAL);
    }

    public static Listener0 newInstanceConcurrent() {
        return new Listener0(new ArrayList<>(), Action.Mode.CONCURRENT);
    }

    public static Listener0 newInstanceConcurrentSafeThread() {
        return new Listener0(new CopyOnWriteArrayList<>(), Action.Mode.CONCURRENT);
    }

    public void call() {
        if (getMode() == Action.Mode.SEQUENTIAL)
            forEach(Runnable::run);
        else if (getMode() == Action.Mode.CONCURRENT)
            parallelStream().forEach(Runnable::run);
        else
            throw new IllegalStateException();
    }

    @Override
    public Action.Mode getMode() {
        return modeConcurrency;
    }
}
