package es.danisales.listeners;

import es.danisales.datastructures.ListAdapter;
import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListenerZero extends ListAdapter<Runnable> implements ListenerList<Runnable> {
    private final Action.Mode modeConcurrency;

    private ListenerZero(List<Runnable> listAdapter, Action.Mode mode) {
        super(listAdapter);

        this.modeConcurrency = mode;
    }

    public static @NonNull ListenerZero newInstanceSequential() {
        return new ListenerZero(new ArrayList<>(), Action.Mode.SEQUENTIAL);
    }

    public static @NonNull ListenerZero newInstanceSequentialThreadSafe() {
        return new ListenerZero(new CopyOnWriteArrayList<>(), Action.Mode.SEQUENTIAL);
    }

    public static @NonNull ListenerZero newInstanceConcurrent() {
        return new ListenerZero(new ArrayList<>(), Action.Mode.CONCURRENT);
    }

    public static @NonNull ListenerZero newInstanceConcurrentThreadSafe() {
        return new ListenerZero(new CopyOnWriteArrayList<>(), Action.Mode.CONCURRENT);
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
