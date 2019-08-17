package es.danisales.listeners;

import es.danisales.datastructures.ListAdapter;
import es.danisales.tasks.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class Listener2<T, U> extends ListAdapter<BiConsumer<T, U>> implements Listener {
    private final Action.Mode modeConcurrency;
    private T defaultArg1 = null;
    private U defaultArg2 = null;

    private Listener2(List<BiConsumer<T, U>> listAdapter, Action.Mode mode) {
        super(listAdapter);

        this.modeConcurrency = mode;
    }

    public static <T, U> Listener2<T, U> newInstanceSequential() {
        return new Listener2<>(new ArrayList<>(), Action.Mode.SEQUENTIAL);
    }

    public static <T, U> Listener2<T, U> newInstanceSequentialSafeThread() {
        return new Listener2<>(new CopyOnWriteArrayList<>(), Action.Mode.SEQUENTIAL);
    }

    public static <T, U> Listener2<T, U> newInstanceConcurrent() {
        return new Listener2<>(new ArrayList<>(), Action.Mode.CONCURRENT);
    }

    public static <T, U> Listener2<T, U> newInstanceConcurrentSafeThread() {
        return new Listener2<>(new CopyOnWriteArrayList<>(), Action.Mode.CONCURRENT);
    }

    public void call(T arg1, U arg2) {
        if (getMode() == Action.Mode.SEQUENTIAL)
            forEach(r -> r.accept(arg1, arg2));
        else if (getMode() == Action.Mode.CONCURRENT)
            parallelStream().forEach((r -> r.accept(arg1, arg2)));
        else
            throw new IllegalStateException();
    }

    public void setDefaultArg(T arg1, U arg2) {
        defaultArg1 = arg1;
        defaultArg2 = arg2;
    }

    @Override
    public void call() {
        call(defaultArg1, defaultArg2);
    }

    @Override
    public Action.Mode getMode() {
        return modeConcurrency;
    }
}
