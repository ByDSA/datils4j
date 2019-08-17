package es.danisales.listeners;

import es.danisales.datastructures.ListAdapter;
import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class ListenerTwo<T, U> extends ListAdapter<BiConsumer<T, U>> implements Listener<BiConsumer<T, U>> {
    private final Action.Mode modeConcurrency;
    private T defaultArg1 = null;
    private U defaultArg2 = null;

    private ListenerTwo(List<BiConsumer<T, U>> listAdapter, Action.Mode mode) {
        super(listAdapter);

        this.modeConcurrency = mode;
    }

    public static @NonNull <T, U> ListenerTwo<T, U> newInstanceSequential() {
        return new ListenerTwo<>(new ArrayList<>(), Action.Mode.SEQUENTIAL);
    }

    public static @NonNull <T, U> ListenerTwo<T, U> newInstanceSequentialThreadSafe() {
        return new ListenerTwo<>(new CopyOnWriteArrayList<>(), Action.Mode.SEQUENTIAL);
    }

    public static @NonNull <T, U> ListenerTwo<T, U> newInstanceConcurrent() {
        return new ListenerTwo<>(new ArrayList<>(), Action.Mode.CONCURRENT);
    }

    public static @NonNull <T, U> ListenerTwo<T, U> newInstanceConcurrentThreadSafe() {
        return new ListenerTwo<>(new CopyOnWriteArrayList<>(), Action.Mode.CONCURRENT);
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
