package es.danisales.listeners;

import es.danisales.datastructures.ListAdapter;
import es.danisales.tasks.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Listener1<T> extends ListAdapter<Consumer<T>> implements Listener {
    private final Action.Mode modeConcurrency;
    private T defaultArg1 = null;

    private Listener1(List<Consumer<T>> listAdapter, Action.Mode mode) {
        super(listAdapter);

        this.modeConcurrency = mode;
    }

    public static <T> Listener1<T> newInstanceSequential() {
        return new Listener1<>(new ArrayList<>(), Action.Mode.SEQUENTIAL);
    }

    public static <T> Listener1<T> newInstanceSequentialSafeThread() {
        return new Listener1<>(new CopyOnWriteArrayList<>(), Action.Mode.SEQUENTIAL);
    }

    public static <T> Listener1<T> newInstanceConcurrent() {
        return new Listener1<>(new ArrayList<>(), Action.Mode.CONCURRENT);
    }

    public static <T> Listener1<T> newInstanceConcurrentSafeThread() {
        return new Listener1<>(new CopyOnWriteArrayList<>(), Action.Mode.CONCURRENT);
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
