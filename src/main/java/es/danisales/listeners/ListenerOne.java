package es.danisales.listeners;

import es.danisales.datastructures.ListAdapter;
import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ListenerOne<T> extends ListAdapter<Consumer<T>> implements Listener<Consumer<T>> {
    private final Action.Mode modeConcurrency;
    private T defaultArg1 = null;

    private ListenerOne(List<Consumer<T>> listAdapter, Action.Mode mode) {
        super(listAdapter);

        this.modeConcurrency = mode;
    }

    public static @NonNull <T> ListenerOne<T> newInstanceSequential() {
        return new ListenerOne<>(new ArrayList<>(), Action.Mode.SEQUENTIAL);
    }

    public static @NonNull <T> ListenerOne<T> newInstanceSequentialThreadSafe() {
        return new ListenerOne<>(new CopyOnWriteArrayList<>(), Action.Mode.SEQUENTIAL);
    }

    public static @NonNull <T> ListenerOne<T> newInstanceConcurrent() {
        return new ListenerOne<>(new ArrayList<>(), Action.Mode.CONCURRENT);
    }

    public static @NonNull <T> ListenerOne<T> newInstanceConcurrentThreadSafe() {
        return new ListenerOne<>(new CopyOnWriteArrayList<>(), Action.Mode.CONCURRENT);
    }

    public void call(T arg1) {
        if (getMode() == Action.Mode.SEQUENTIAL)
            forEach(r -> r.accept(arg1));
        else if (getMode() == Action.Mode.CONCURRENT)
            parallelStream().forEach((r -> r.accept(arg1)));
        else
            throw new IllegalStateException();
    }

    public void setDefaultArg(T arg) {
        defaultArg1 = arg;
    }

    @Override
    public void call() {
        call(defaultArg1);
    }

    @Override
    public Action.Mode getMode() {
        return modeConcurrency;
    }
}
