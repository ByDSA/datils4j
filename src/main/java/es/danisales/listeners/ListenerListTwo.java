package es.danisales.listeners;

import es.danisales.tasks.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.BiConsumer;

public class ListenerListTwo<T, U> extends ListenerListAdapter<BiConsumer<T, U>> {
    private T defaultArg1 = null;
    private U defaultArg2 = null;

    private ListenerListTwo(Action.Mode mode, Safety safety) {
        super(mode, safety);
    }

    @SuppressWarnings("unused")
    public static @NonNull <T, U> ListenerListTwo<T, U> newInstanceSequential() {
        return new ListenerListTwo<>(Action.Mode.SEQUENTIAL, Safety.NonThreadSafe);
    }

    @SuppressWarnings("unused")
    public static @NonNull <T, U> ListenerListTwo<T, U> newInstanceSequentialThreadSafe() {
        return new ListenerListTwo<>(Action.Mode.SEQUENTIAL, Safety.ThreadSafe);
    }

    @SuppressWarnings("unused")
    public static @NonNull <T, U> ListenerListTwo<T, U> newInstanceConcurrent() {
        return new ListenerListTwo<>(Action.Mode.CONCURRENT, Safety.NonThreadSafe);
    }

    @SuppressWarnings("unused")
    public static @NonNull <T, U> ListenerListTwo<T, U> newInstanceConcurrentThreadSafe() {
        return new ListenerListTwo<>(Action.Mode.CONCURRENT, Safety.ThreadSafe);
    }

    public void call(T arg1, U arg2) {
        if (getMode() == Action.Mode.SEQUENTIAL)
            forEach(r -> r.accept(arg1, arg2));
        else if (getMode() == Action.Mode.CONCURRENT)
            parallelStream().forEach((r -> r.accept(arg1, arg2)));
        else
            throw new IllegalStateException();
    }

    @SuppressWarnings("unused")
    public void setDefaultArg(T arg1, U arg2) {
        defaultArg1 = arg1;
        defaultArg2 = arg2;
    }

    @Override
    public void call() {
        call(defaultArg1, defaultArg2);
    }
}
