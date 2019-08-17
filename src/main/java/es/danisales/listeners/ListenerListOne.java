package es.danisales.listeners;

import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

public class ListenerListOne<T> extends ListenerListAdapter<Consumer<T>> {
    private T defaultArg1 = null;

    private ListenerListOne(Action.Mode mode, Safety safety) {
        super(mode, safety);
    }

    @SuppressWarnings("unused")
    public static @NonNull <T> ListenerListOne<T> newInstanceSequential() {
        return new ListenerListOne<>(Action.Mode.SEQUENTIAL, Safety.NonThreadSafe);
    }

    public static @NonNull <T> ListenerListOne<T> newInstanceSequentialThreadSafe() {
        return new ListenerListOne<>(Action.Mode.SEQUENTIAL, Safety.ThreadSafe);
    }

    @SuppressWarnings("unused")
    public static @NonNull <T> ListenerListOne<T> newInstanceConcurrent() {
        return new ListenerListOne<>(Action.Mode.CONCURRENT, Safety.NonThreadSafe);
    }

    @SuppressWarnings("unused")
    public static @NonNull <T> ListenerListOne<T> newInstanceConcurrentThreadSafe() {
        return new ListenerListOne<>(Action.Mode.CONCURRENT, Safety.ThreadSafe);
    }

    public void call(T arg1) {
        if (getMode() == Action.Mode.SEQUENTIAL)
            forEach(r -> r.accept(arg1));
        else if (getMode() == Action.Mode.CONCURRENT)
            parallelStream().forEach((r -> r.accept(arg1)));
        else
            throw new IllegalStateException();
    }

    @SuppressWarnings("unused")
    public void setDefaultArg(T arg) {
        defaultArg1 = arg;
    }

    @Override
    public void call() {
        call(defaultArg1);
    }
}
